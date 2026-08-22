package net.neoforged.fml.event;

/**
 * NeoForge API shim: marker interface for mod-bus events. The real FML uses
 * it to validate that an event type belongs on the mod-scoped bus; under
 * Aprism it exists so event-class resolution succeeds.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface IModBusEvent {
}
