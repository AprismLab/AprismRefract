package com.aprism.refract.neoforge;

import java.util.List;
import java.util.logging.Logger;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.loader.loaderext.LoaderEntrypointHandler;

/**
 * The NeoForge entrypoint-dispatch handler. Registered against loader key
 * {@code N} via {@code ExtensionContext.registerEntrypointHandler}, it fully
 * owns NeoForge entrypoint dispatch — the Aprism core no longer needs any
 * NeoForge-specific translation code.
 *
 * <p>Extracted from the Aprism core in v26.0-Alpha.2 per the loader-support
 * extraction architecture: the core ships only the
 * {@link LoaderEntrypointHandler} seam and the Aprism-native fallback; this
 * handler supplies the NeoForge-specific behaviour, and the NeoForge API
 * shims ({@code net.neoforged.fml.common.Mod},
 * {@code net.neoforged.bus.api.IEventBus}) are bundled in the same extension
 * jar.
 *
 * <p>Behaviour contract (mirrors the former core built-in dispatch):
 * <ol>
 *   <li>NeoForge entrypoints are annotation-discovered, not manifest-declared:
 *       {@link NeoForgeEntrypointBridge#findModClasses} scans the mod jar's
 *       bytecode for classes annotated with {@code @Mod} whose value matches
 *       the mod id.</li>
 *   <li>Construction IS initialization: only the {@link AprismPhase#INIT}
 *       phase constructs; the {@code @Mod} class is instantiated with an
 *       injected mod-scoped {@code IEventBus}. All other phases are no-ops —
 *       after construction the mod is event-driven.</li>
 *   <li>Construction is idempotent: a repeated INIT does not re-construct.</li>
 * </ol>
 *
 * @author BlockConnect@StarsailsClover
 */
public final class NeoForgeEntrypointHandler implements LoaderEntrypointHandler {

    /** The NeoForge loader key reserved in Aprism FACT.md 9.14. */
    public static final String NEOFORGE_KEY = "N";

    private static final Logger LOG = Logger.getLogger(NeoForgeEntrypointHandler.class.getName());

    @Override
    public String loaderKey() {
        return NEOFORGE_KEY;
    }

    /**
     * Fully owns NeoForge dispatch: the Aprism-native {@code IAprismMod}
     * fallback must NOT run afterwards, exactly like the former core built-in
     * path (which returned after dispatching).
     */
    @Override
    public boolean isExclusive() {
        return true;
    }

    @Override
    public void invoke(LoadedModContainer container, AprismPhase phase) {
        // NeoForge initialization is construction itself; only INIT constructs.
        if (phase != AprismPhase.INIT) {
            return;
        }
        // Idempotent: a repeated INIT must not re-construct.
        if (container.getInstance() != null) {
            return;
        }
        List<String> modClasses = NeoForgeEntrypointBridge.findModClasses(
                container.getSourcePath(), container.getId());
        if (modClasses.isEmpty()) {
            LOG.warning("NeoForge mod " + container.getId()
                    + " has no @Mod entrypoint class; skipping");
            return;
        }
        try {
            // The handler is loaded by the AprismClassLoader from the .aep's
            // embedded jar, so its classloader IS the shared class space that
            // also contains the mod jars — the entrypoint class resolves here.
            Class<?> clazz = Class.forName(modClasses.get(0), true,
                    getClass().getClassLoader());
            Object instance = NeoForgeEntrypointBridge.construct(clazz, new NeoForgeEventBus());
            container.setInstance(instance);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load NeoForge entrypoint for "
                    + container.getId(), e);
        } catch (RuntimeException e) {
            LOG.warning("NeoForge mod " + container.getId()
                    + " failed to construct during INIT: " + e);
        }
    }
}
