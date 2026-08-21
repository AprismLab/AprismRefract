package net.minecraftforge.eventbus.api;

/**
 * Forge API shim: event priority enum. Controls the order in which
 * event listeners are invoked.
 *
 * @author BlockConnect@StarsailsClover
 */
public enum EventPriority {
    HIGHEST,
    HIGH,
    NORMAL,
    LOW,
    LOWEST
}
