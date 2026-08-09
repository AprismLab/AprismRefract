package net.neoforged.bus.api;

import java.util.function.Consumer;

/**
 * NeoForge API shim: the mod-scoped event bus injected into NeoForge mod
 * constructors. Bundled with the NeoForge-Support extension (.aep) so that
 * genuine NeoForge mods can be instantiated without the real NeoForge/FML
 * runtime on the classpath.
 *
 * <p>Mirrors the minimal surface of {@code net.neoforged.bus.api.IEventBus}
 * that mods use during construction: registering event listeners. The branch
 * backs this shim with its own {@code NeoForgeEventBus}, so listeners
 * registered during mod construction are held by the branch extension.
 *
 * <p>Extracted from the Aprism core ({@code aprism-loader-core}) in
 * v26.0-Alpha.2 per the loader-support extraction.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface IEventBus {

    /**
     * Registers an event listener for the given event type.
     *
     * @param type     the event class
     * @param consumer the listener invoked when an event of {@code type} is posted
     * @param <T>      the event type
     */
    <T> void addListener(Class<T> type, Consumer<T> consumer);

    /**
     * Registers a generic event listener.
     *
     * @param consumer the listener invoked for any posted event
     */
    void addGenericListener(Consumer<Object> consumer);

    /**
     * Posts an event to all registered listeners.
     *
     * @param event the event instance
     */
    void post(Object event);
}
