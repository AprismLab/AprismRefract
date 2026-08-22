package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;

/**
 * NeoForge API shim: client tick event. Marker hierarchy only.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class ClientTickEvent extends Event {

    /** Post-tick phase. */
    public static final class Post extends ClientTickEvent {
    }
}
