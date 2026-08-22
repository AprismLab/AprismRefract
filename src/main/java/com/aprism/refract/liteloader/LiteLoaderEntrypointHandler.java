package com.aprism.refract.liteloader;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.loader.loaderext.LoaderEntrypointHandler;

/**
 * The LiteLoader entrypoint-dispatch handler. Registered against loader key
 * {@code L} via {@code ExtensionContext.registerEntrypointHandler}, it fully
 * owns LiteLoader entrypoint dispatch — the Aprism core no longer needs any
 * LiteLoader-specific translation code.
 *
 * <p>Extracted from the Aprism core in v26.0-Alpha.2 per the loader-support
 * extraction architecture: the core ships only the
 * {@link LoaderEntrypointHandler} seam and the Aprism-native fallback; this
 * handler supplies the LiteLoader-specific behaviour, and the LiteLoader API
 * shim ({@code com.mumfrey.liteloader.core.LiteMod}) is bundled in the same
 * extension jar.
 *
 * <p>Behaviour contract (mirrors the former core built-in dispatch):
 * <ol>
 *   <li>LiteLoader entrypoints are interface-discovered, not manifest-declared:
 *       {@link LiteLoaderEntrypointBridge#findModClasses} scans the
 *       {@code .litemod} archive's bytecode for classes implementing
 *       {@code LiteMod} (or named with the historical {@code LiteMod}
 *       prefix).</li>
 *   <li>Initialization is a single {@code init(File)} call: only the
 *       {@link AprismPhase#INIT} phase constructs the entrypoint and invokes
 *       {@code init(File)} with the mod's config folder
 *       ({@code <gameRoot>/config/<modId>}). All other phases are no-ops.</li>
 *   <li>Construction is idempotent: a repeated INIT does not re-construct.</li>
 * </ol>
 *
 * @author BlockConnect@StarsailsClover
 */
public final class LiteLoaderEntrypointHandler implements LoaderEntrypointHandler {

    /** The LiteLoader key reserved in Aprism FACT.md 9.14. */
    public static final String LITELOADER_KEY = "L";

    /** The folder that LiteLoader mods are discovered under. */
    private static final String LITELOADER_MODS_FOLDER = "liteloader-mods";

    private static final Logger LOG = Logger.getLogger(LiteLoaderEntrypointHandler.class.getName());

    @Override
    public String loaderKey() {
        return LITELOADER_KEY;
    }

    /**
     * Fully owns LiteLoader dispatch: the Aprism-native {@code IAprismMod}
     * fallback must NOT run afterwards, exactly like the former core built-in
     * path (which returned after dispatching).
     */
    @Override
    public boolean isExclusive() {
        return true;
    }

    // GitHub@NDBlockConnect | BlockConnect@StarsailsClover
    @Override
    public void invoke(LoadedModContainer container, AprismPhase phase) {
        // TCCL discipline (v26.8-Alpha.3, ported from the neoforge branch):
        // keep the whole dispatch under the mod-space TCCL for uniformity
        // across the five branches; legacy LiteLoader mods rarely use
        // ServiceLoader but the invariant costs nothing and stays consistent.
        Thread current = Thread.currentThread();
        ClassLoader previousTccl = current.getContextClassLoader();
        current.setContextClassLoader(getClass().getClassLoader());
        try {
            dispatch(container, phase);
        } finally {
            current.setContextClassLoader(previousTccl);
        }
    }

    /**
     * Phase dispatch proper. Runs under the mod-space TCCL.
     */
    private void dispatch(LoadedModContainer container, AprismPhase phase) {
        // LiteLoader initialization is a single init(File); only INIT runs.
        if (phase != AprismPhase.INIT) {
            return;
        }
        // Idempotent: a repeated INIT must not re-construct.
        if (container.getInstance() != null) {
            return;
        }
        List<String> modClasses = LiteLoaderEntrypointBridge.findModClasses(
                container.getSourcePath());
        if (modClasses.isEmpty()) {
            LOG.warning("LiteLoader mod " + container.getId()
                    + " has no LiteMod entrypoint class; skipping");
            return;
        }
        try {
            // The handler is loaded by the AprismClassLoader from the .aep's
            // embedded jar, so its classloader IS the shared class space that
            // also contains the mod archives — the entrypoint class resolves here.
            Class<?> clazz = Class.forName(modClasses.get(0), true,
                    getClass().getClassLoader());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            container.setInstance(instance);
            File configFolder = configFolderFor(container);
            LiteLoaderEntrypointBridge.invokeInit(instance, configFolder);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to load LiteLoader entrypoint for "
                    + container.getId(), e);
        } catch (RuntimeException e) {
            LOG.warning("LiteLoader mod " + container.getId()
                    + " failed during INIT: " + e);
        }
    }

    /**
     * Derives the mod's config folder, {@code <gameRoot>/config/<modId>}, the
     * same way the former core dispatch did. The game root is recovered from
     * the mod's source path (a {@code .litemod} sits at
     * {@code <gameRoot>/liteloader-mods/<file>}); when that cannot be derived
     * the method falls back to a bare {@code new File(modId)}, matching the
     * core's fallback.
     *
     * @param container the mod container
     * @return the mod's config folder
     */
    private static File configFolderFor(LoadedModContainer container) {
        Path source = container.getSourcePath();
        Path modsDir = source != null ? source.getParent() : null;
        if (modsDir != null && modsDir.getFileName() != null
                && LITELOADER_MODS_FOLDER.equals(modsDir.getFileName().toString())) {
            Path gameRoot = modsDir.getParent();
            if (gameRoot != null) {
                return gameRoot.resolve("config").resolve(container.getId()).toFile();
            }
        }
        return new File(container.getId());
    }
}
