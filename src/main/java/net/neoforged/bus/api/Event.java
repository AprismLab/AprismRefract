package net.neoforged.bus.api;

/**
 * NeoForge API shim: base class for bus events. Mods that define or consume
 * custom event hierarchies extend this. Under Aprism only cancellation
 * state is modelled; priority/phase bookkeeping is not.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class Event {

    private boolean canceled;

    /**
     * Marks the event as canceled (no-op effect under Aprism beyond state).
     *
     * @param canceled whether to cancel
     */
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * @return whether the event was canceled
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * @return whether this event type supports cancellation (always true)
     */
    public boolean isCancelable() {
        return true;
    }
}
