package com.aprism.refract.quilt;

import java.util.List;
import java.util.logging.Logger;

import com.aprism.api.AprismContext;
import com.aprism.api.AprismEventBus;
import com.aprism.api.AprismPhase;
import com.aprism.api.AprismRegistry;
import com.aprism.api.IAprismMod;
import com.aprism.api.imc.InterModComms;
import com.aprism.loader.AprismContextImpl;
import com.aprism.loader.LoadedModContainer;
import com.aprism.loader.loaderext.LoaderEntrypointHandler;

/**
 * The Quilt entrypoint-dispatch handler. Registered against loader key
 * {@code Q} via {@code ExtensionContext.registerEntrypointHandler}, it fully
 * owns Quilt entrypoint dispatch — the Aprism core no longer needs any
 * Quilt-specific translation code.
 *
 * <p>Extracted from the Aprism core in v26.0-Alpha.2 per the loader-support
 * extraction architecture: the core ships only the
 * {@link LoaderEntrypointHandler} seam and the Aprism-native fallback; this
 * handler supplies the Quilt-specific behaviour, and the Fabric API shims
 * ({@code net.fabricmc.api.*}) — which Quilt mods use through Quilt's built-in
 * Fabric compatibility layer — are bundled in the same extension jar.
 *
 * <p>Behaviour contract (mirrors the former core built-in dispatch):
 * <ol>
 *   <li>Read the {@code entrypoints} list for the phase's key from the mod
 *       manifest (Quilt's {@code quilt.mod.json} is projected to an Aprism
 *       manifest; the Quilt-native {@code init} entrypoint key is projected
 *       to {@code main}).</li>
 *   <li>Instantiate each entrypoint class through this handler's classloader —
 *       the shared {@code AprismClassLoader} that also contains the mod jars,
 *       so mod classes resolve.</li>
 *   <li>If the instance implements {@link IAprismMod}, dispatch the
 *       Aprism-native lifecycle method for the phase.</li>
 *   <li>Otherwise invoke the Quilt (Fabric-convention) entrypoint
 *       ({@code onInitialize} / {@code onInitializeClient} /
 *       {@code onInitializeServer}) reflectively.</li>
 *   <li>Retain the first instantiated instance on the container (FACT.md 9.2
 *       reference-identity invariant).</li>
 * </ol>
 *
 * <p>A mod throwing during its own entrypoint is isolated: its remaining
 * entrypoints are skipped and the lifecycle of the other mods continues.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class QuiltEntrypointHandler implements LoaderEntrypointHandler {

    /** The Quilt loader key reserved in Aprism FACT.md 9.14. */
    public static final String QUILT_KEY = "Q";

    private static final Logger LOG = Logger.getLogger(QuiltEntrypointHandler.class.getName());

    /** Shared event bus handed in at registration (may be {@code null}). */
    private final AprismEventBus eventBus;
    /** Shared registry handed in at registration (may be {@code null}). */
    private final AprismRegistry registry;
    /** Shared inter-mod comms surface (may be {@code null}). */
    private final InterModComms interModComms;

    /**
     * @param eventBus      the shared Aprism event bus (or {@code null})
     * @param registry      the shared Aprism registry (or {@code null})
     * @param interModComms the shared inter-mod comms surface (or {@code null})
     */
    public QuiltEntrypointHandler(AprismEventBus eventBus, AprismRegistry registry,
                                  InterModComms interModComms) {
        this.eventBus = eventBus;
        this.registry = registry;
        this.interModComms = interModComms;
    }

    @Override
    public String loaderKey() {
        return QUILT_KEY;
    }

    /**
     * Fully owns Quilt dispatch: the Aprism-native {@code IAprismMod} fallback
     * must NOT run afterwards, exactly like the former core built-in path
     * (which returned after dispatching).
     */
    @Override
    public boolean isExclusive() {
        return true;
    }

    @Override
    public void invoke(LoadedModContainer container, AprismPhase phase) {
        // Register the mod with the FabricLoader shim (v26.7-Alpha.3) so
        // FabricLoader.getInstance().isModLoaded(id) answers for genuine mods.
        QuiltLoaderBridge.registerMod(container.getId());
        List<String> entrypoints = entrypointsFor(container, phase);
        if (entrypoints.isEmpty()) {
            return;
        }
        // The handler is loaded by the AprismClassLoader from the .aep's embedded
        // jar, so its classloader IS the shared class space that also contains
        // the mod jars — mod entrypoint classes resolve here.
        ClassLoader loader = getClass().getClassLoader();
        for (String className : entrypoints) {
            try {
                Class<?> clazz = Class.forName(className, true, loader);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (instance instanceof IAprismMod mod) {
                    // Aprism-native mod living under quilt-mods/: full lifecycle
                    invokePhaseMethod(mod, container, phase);
                } else {
                    // Quilt (Fabric-convention) entrypoint
                    QuiltEntrypointBridge.invoke(instance, phase);
                }
                if (container.getInstance() == null) {
                    container.setInstance(instance);
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to invoke entrypoint " + className
                        + " for mod " + container.getId() + " in phase " + phase, e);
            } catch (RuntimeException e) {
                // A mod throwing during its own entrypoint is isolated so it
                // cannot abort the lifecycle of the remaining mods.
                LOG.warning("Mod " + container.getId() + " threw in phase " + phase
                        + "; skipping its remaining entrypoints: " + e);
                break;
            }
        }
    }

    /**
     * Resolves the entrypoint class names declared for the phase's key.
     *
     * @param container the mod container
     * @param phase     the lifecycle phase
     * @return the declared entrypoint class names (may be empty)
     */
    private static List<String> entrypointsFor(LoadedModContainer container, AprismPhase phase) {
        var entrypoints = container.getManifest().entrypoints();
        if (entrypoints == null) {
            return List.of();
        }
        return entrypoints.getOrDefault(entrypointKeyFor(phase), List.of());
    }

    /**
     * Maps a lifecycle phase to the entrypoint key, matching the core
     * convention ({@code main} for PREINIT/INIT/SETUP/COMPLETE — the Quilt
     * {@code init} key is projected to {@code main} by the manifest
     * projection — {@code client} for CLIENT, {@code server} for SERVER).
     *
     * @param phase the lifecycle phase
     * @return the entrypoint key
     */
    private static String entrypointKeyFor(AprismPhase phase) {
        return switch (phase) {
            case PREINIT, INIT, SETUP, COMPLETE -> "main";
            case CLIENT -> "client";
            case SERVER -> "server";
        };
    }

    /**
     * Dispatches the Aprism-native lifecycle method for a mod that implements
     * {@link IAprismMod}.
     *
     * @param mod       the mod instance
     * @param container the mod container
     * @param phase     the lifecycle phase
     */
    private void invokePhaseMethod(IAprismMod mod, LoadedModContainer container, AprismPhase phase) {
        AprismContext context = new AprismContextImpl(container, eventBus, registry, interModComms);
        switch (phase) {
            case PREINIT -> mod.onPreInitialize(context);
            case INIT -> mod.onInitialize(context);
            case SETUP -> mod.onSetup(context);
            case COMPLETE -> mod.onComplete(context);
            case CLIENT, SERVER -> mod.onInitialize(context);
        }
    }
}
