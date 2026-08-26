package com.aprism.refract.forge;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.loader.loaderext.LoaderEntrypointHandler;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

/**
 * The Forge entrypoint-dispatch handler. Registered against loader key
 * {@code Fo} via {@code ExtensionContext.registerEntrypointHandler}, it fully
 * owns Forge entrypoint dispatch — the Aprism core no longer needs any
 * Forge-specific translation code.
 *
 * <p>Extracted from the Aprism core in v26.0-Alpha.2 per the loader-support
 * extraction architecture: the core ships only the
 * {@link LoaderEntrypointHandler} seam and the Aprism-native fallback; this
 * handler supplies the Forge-specific behaviour, and the Forge API shims
 * ({@code net.minecraftforge.fml.common.Mod},
 * {@code net.minecraftforge.eventbus.api.IEventBus}) are bundled in the same
 * extension jar.
 *
 * <p>Behaviour contract:
 * <ol>
 *   <li>Forge entrypoints are annotation-discovered, not manifest-declared:
 *       {@link ForgeEntrypointBridge#findModClasses} scans the mod jar's
 *       bytecode for classes annotated with Forge's {@code @Mod} whose value
 *       matches the mod id.</li>
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
 *         <li>SETUP → {@code InterModEnqueueEvent}</li>
 *         <li>COMPLETE → {@code InterModProcessEvent} + {@code FMLLoadCompleteEvent}</li>
 *         <li>SERVER/PREINIT → no Forge lifecycle equivalent</li>
 *       </ul>
 *   </li>
 * </ol>
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ForgeEntrypointHandler implements LoaderEntrypointHandler {

    /** The Forge loader key reserved in Aprism FACT.md 9.14. */
    public static final String FORGE_KEY = "Fo";

    private static final Logger LOG = Logger.getLogger(ForgeEntrypointHandler.class.getName());

    /** Per-container event bus reference, keyed by mod container identity. */
    private static final Map<LoadedModContainer, ForgeEventBus> BUS_MAP = new WeakHashMap<>();

    @Override
    public String loaderKey() {
        return FORGE_KEY;
    }

    /**
     * Fully owns Forge dispatch: the Aprism-native {@code IAprismMod} fallback
     * must NOT run afterwards, exactly like the former core built-in path
     * (which returned after dispatching).
     */
    @Override
    public boolean isExclusive() {
        return true;
    }

    // GitHub@NDBlockConnect | BlockConnect@StarsailsClover
    @Override
    public void invoke(LoadedModContainer container, AprismPhase phase) {
        // TCCL discipline (v26.8-Alpha.3, ported from the neoforge branch):
        // ServiceLoader.load() resolves against the TCCL; mod-side
        // META-INF/services lookups must see the shared AprismClassLoader.
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
        // GitHub@NDBlockConnect | BlockConnect@StarsailsClover
        // v26.9-Alpha.1: event ORDER aligned with real Forge - client setup
        // fires BEFORE load-complete (Aprism phase order runs COMPLETE
        // before CLIENT; LoadComplete moved to the CLIENT tail).
        switch (phase) {
            case INIT ->
                initOrFireLifecycleEvent(container, new FMLCommonSetupEvent());
            case SETUP ->
                fireLifecycleEvent(container, new InterModEnqueueEvent());
            case COMPLETE ->
                fireLifecycleEvent(container, new InterModProcessEvent());
            case CLIENT -> {
                fireLifecycleEvent(container, new FMLClientSetupEvent());
                fireLifecycleEvent(container, new FMLLoadCompleteEvent());
            }
            case PREINIT, SERVER -> { /* no Forge lifecycle equivalent */ }
        }
    }

    /**
     * Constructs the mod on first call (INIT) and fires the given lifecycle
     * event through the mod's event bus. On subsequent calls, only fires the
     * event (the instance is already constructed).
     */
    private void initOrFireLifecycleEvent(LoadedModContainer container, Object lifecycleEvent) {
        if (container.getInstance() == null) {
            List<String> modClasses = ForgeEntrypointBridge.findModClasses(
                    container.getSourcePath(), container.getId());
            if (modClasses.isEmpty()) {
                LOG.warning("Forge mod " + container.getId()
                        + " has no @Mod entrypoint class; skipping");
                return;
            }
            try {
                // The handler is loaded by the AprismClassLoader from the .aep's
                // embedded jar, so its classloader IS the shared class space that
                // also contains the mod jars — the entrypoint class resolves here.
                Class<?> clazz = Class.forName(modClasses.get(0), true,
                        getClass().getClassLoader());
                ForgeEventBus bus = new ForgeEventBus();
                Object instance = ForgeEntrypointBridge.construct(clazz, bus);
                container.setInstance(instance);
                BUS_MAP.put(container, bus);
                bus.post(lifecycleEvent);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load Forge entrypoint for "
                        + container.getId(), e);
            } catch (RuntimeException e) {
                LOG.warning("Forge mod " + container.getId()
                        + " failed to construct during INIT: " + e);
            }
        } else {
            fireLifecycleEvent(container, lifecycleEvent);
        }
    }

    /**
     * Fires a lifecycle event through the mod's retained event bus.
     */
    private void fireLifecycleEvent(LoadedModContainer container, Object event) {
        if (container.getInstance() == null) {
            return;
        }
        ForgeEventBus bus = BUS_MAP.get(container);
        if (bus != null) {
            bus.post(event);
        }
    }
}
