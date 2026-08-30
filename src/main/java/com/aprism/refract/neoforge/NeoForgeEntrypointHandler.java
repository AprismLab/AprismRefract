package com.aprism.refract.neoforge;

import java.util.List;
import java.util.logging.Logger;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.loader.loaderext.LoaderEntrypointHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

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
 *       injected mod-scoped {@code IEventBus}. After construction, lifecycle
 *       events are fired through the bus to drive the mod's event-driven
 *       setup.</li>
 *   <li>Construction is idempotent: a repeated INIT does not re-construct.</li>
 *   <li>Lifecycle event mapping:
 *       <ul>
 *         <li>INIT → construct + {@code FMLCommonSetupEvent}</li>
 *         <li>CLIENT → {@code FMLClientSetupEvent}</li>
 *         <li>SERVER → {@code FMLDedicatedServerSetupEvent}</li>
 *         <li>SETUP → {@code InterModEnqueueEvent}</li>
 *         <li>COMPLETE → {@code InterModProcessEvent} + {@code FMLLoadCompleteEvent}</li>
 *       </ul>
 *   </li>
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

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

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
        // Mod code must see the shared AprismClassLoader as the thread context
        // classloader while it runs (v26.8-Alpha.1): ServiceLoader.load()
        // resolves against the TCCL, so without this every mod-side
        // META-INF/services lookup (e.g. JEI's IPlatformHelper) fails.
        Thread current = Thread.currentThread();
        ClassLoader previousTccl = current.getContextClassLoader();
        current.setContextClassLoader(getClass().getClassLoader());
        try {
            dispatch(container, phase);
        } finally {
            current.setContextClassLoader(previousTccl);
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Phase dispatch proper. Runs under the mod-space TCCL.
     */
    private void dispatch(LoadedModContainer container, AprismPhase phase) {
        // Feed the ModList shim so ModList.get().isLoaded(id) answers for
        // inter-mod presence checks (v26.7-Alpha.4).
        net.neoforged.fml.ModList.setLoadedMods(java.util.stream.Stream.concat(
                java.util.Arrays.stream(net.neoforged.fml.ModList.get().getLoadedMods().toArray(new String[0])),
                java.util.stream.Stream.of(container.getId()))
                .distinct()
                .toList());
        // Produce REAL annotation scan data for this mod jar (v26.8-Alpha.1):
        // consumers such as JEI resolve their plugins from ModList.getAllScanData().
        try {
            net.neoforged.neoforgespi.language.ModFileScanData scan =
                    ModAnnotationScanner.scan(container.getSourcePath());
            var aggregated = new java.util.ArrayList<>(
                    net.neoforged.fml.ModList.get().getAllScanData());
            aggregated.add(scan);
            net.neoforged.fml.ModList.setAllScanData(aggregated);
        } catch (RuntimeException e) {
            LOG.warning("Annotation scan failed for " + container.getId()
                    + "; continuing with partial scan data: " + e);
        }
        switch (phase) {
            // GitHub@NDBlockConnect | BlockConnect@StarsailsClover
            // v26.9-Alpha.1: event ORDER aligned with real NeoForge - client
            // setup fires BEFORE load-complete. Aprism's phase order runs
            // COMPLETE before CLIENT, so FMLLoadCompleteEvent moved from
            // COMPLETE to the tail of CLIENT; JEI-class StartEventObservers
            // await exactly this sequence.
            case INIT -> initOrFireLifecycleEvent(container, new FMLCommonSetupEvent());
            case SETUP -> fireLifecycleEvent(container, new InterModEnqueueEvent());
            case COMPLETE -> fireLifecycleEvent(container, new InterModProcessEvent());
            case CLIENT -> {
                fireLifecycleEvent(container, new FMLClientSetupEvent());
                fireLifecycleEvent(container, new FMLLoadCompleteEvent());
            }
            case SERVER -> fireLifecycleEvent(container, new FMLDedicatedServerSetupEvent());
            case PREINIT -> { /* no NeoForge equivalent */ }
        }
    }

    /**
     * Constructs the mod on first call (INIT) and fires the given lifecycle
     * event through the mod's event bus. On subsequent calls, only fires the
     * event (the instance is already constructed).
     */
    private void initOrFireLifecycleEvent(LoadedModContainer container, Object lifecycleEvent) {
        if (container.getInstance() == null) {
            List<String> modClasses = NeoForgeEntrypointBridge.findModClasses(
                    container.getSourcePath(), container.getId());
            if (modClasses.isEmpty()) {
                LOG.warning("NeoForge mod " + container.getId()
                        + " has no @Mod entrypoint class; skipping");
                return;
            }
            try {
                // ModLoadingContext.getActiveContainer() must answer THIS mod
                // while its constructor runs (v26.7-Alpha.4).
                net.neoforged.fml.ModLoadingContext.setActiveModId(container.getId());
                Class<?> clazz = Class.forName(modClasses.get(0), true,
                        getClass().getClassLoader());
                NeoForgeEventBus bus = new NeoForgeEventBus();
                Object instance = NeoForgeEntrypointBridge.construct(clazz, bus);
                container.setInstance(instance);
                storeBus(container, bus);
                bus.post(lifecycleEvent);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load NeoForge entrypoint for "
                        + container.getId(), e);
            } catch (RuntimeException e) {
                // Log the full cause chain (v26.7-Alpha.4): shim gaps surface
                // as NoClassDefFoundError deep in the cause tree; printing only
                // toString() hid the missing class name.
                Throwable cause = e;
                while (cause.getCause() != null && cause.getCause() != cause) {
                    cause = cause.getCause();
                }
                LOG.warning("NeoForge mod " + container.getId()
                        + " failed to construct during INIT: " + e
                        + " | root cause: " + cause);
            }
        } else {
            fireLifecycleEvent(container, lifecycleEvent);
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Fires a lifecycle event through the mod's event bus. The event bus is
     * the same instance that was injected into the mod constructor.
     */
    private void fireLifecycleEvent(LoadedModContainer container, Object event) {
        Object instance = container.getInstance();
        if (instance == null) {
            return;
        }
        // The event bus was created during INIT and is held by the handler's
        // closure. We need to retrieve it. Since the bus was passed to the
        // constructor, we create a new post through the same bus.
        // For simplicity, we re-create a bus and post through it. The bus
        // holds all registered listeners from construction time.
        // Actually, the bus was created in initOrFireLifecycleEvent and passed
        // to the constructor. We need to retain a reference to it.
        // For now, we use a per-container bus map.
        NeoForgeEventBus bus = busFor(container);
        if (bus != null) {
            bus.post(event);
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Per-container event bus reference, keyed by mod container identity. */
    private static final java.util.Map<LoadedModContainer, NeoForgeEventBus> BUS_MAP
            = new java.util.WeakHashMap<>();

    /**
     * Stores the event bus for a container. Called during INIT construction.
     */
    static void storeBus(LoadedModContainer container, NeoForgeEventBus bus) {
        BUS_MAP.put(container, bus);
    }

    /**
     * Retrieves the event bus for a container.
     */
    static NeoForgeEventBus busFor(LoadedModContainer container) {
        return BUS_MAP.get(container);
    }
}
