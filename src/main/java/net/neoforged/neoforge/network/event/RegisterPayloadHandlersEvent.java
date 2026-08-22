package net.neoforged.neoforge.network.event;

/**
 * NeoForge API shim: fired on the mod event bus so mods can register network
 * payload handlers with the {@code PayloadRegistrar}. Under Aprism the class
 * exists so listener registrations resolve; the event itself is not posted
 * because Aprism does not model the NeoForge networking handshake.
 *
 * @author BlockConnect@StarsailsClover
 */
public class RegisterPayloadHandlersEvent {
}
