package net.neoforged.neoforge.client.event;

import net.neoforged.bus.api.Event;

/**
 * NeoForge API shim: client player network lifecycle events (logging in/out
 * of a server). Marker hierarchy only; Aprism does not drive these.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class ClientPlayerNetworkEvent extends Event {

    /** Fired when the client starts logging into a server. */
    public static final class LoggingIn extends ClientPlayerNetworkEvent {
    }

    /** Fired when the client logs out of a server. */
    public static final class LoggingOut extends ClientPlayerNetworkEvent {
    }
}
