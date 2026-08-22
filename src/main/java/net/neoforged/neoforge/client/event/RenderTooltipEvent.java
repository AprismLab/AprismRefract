package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;

/**
 * NeoForge API shim: tooltip render events. Marker hierarchy only.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class RenderTooltipEvent extends Event {

    /** Pre-render phase. */
    public static final class Pre extends RenderTooltipEvent {
    }
}
