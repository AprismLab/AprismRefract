package net.neoforged.neoforge.common;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import net.neoforged.bus.api.IEventBus;

/**
 * NeoForge API shim: the global NeoForge event bus. Unlike the mod-scoped
 * {@code IEventBus} (injected into mod constructors), this is the shared
 * game-level event bus for NeoForge game events. Under Aprism, this is
 * backed by the Aprism event bus.
 *
 * <p>Many NeoForge mods register game event listeners on
 * {@code NeoForge.EVENT_BUS} during mod construction. This shim provides
 * the static field so those registrations succeed.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class NeoForge {

    /**
     * The global NeoForge event bus. Initialized by the Aprism runtime
     * during extension loading. Mods register game event listeners here.
     */
    public static volatile IEventBus EVENT_BUS;

    private NeoForge() {
    }

    /**
     * Sets the global event bus. Called by the Aprism runtime.
     *
     * @param bus the event bus instance
     */
    public static void setEventBus(IEventBus bus) {
        EVENT_BUS = bus;
    }
}
