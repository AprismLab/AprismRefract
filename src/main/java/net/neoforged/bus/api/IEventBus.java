package net.neoforged.bus.api;

import java.util.function.Consumer;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
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
    <T>     void addListener(Class<T> type, Consumer<T> consumer);

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Registers an event listener with the receiveCancelled flag (NeoForge
     * convention overload). Under Aprism the flag is accepted and ignored;
     * cancelled-event semantics are not modelled by the shim bus.
     *
     * @param receiveCancelled whether to receive cancelled events
     * @param type             the event class
     * @param consumer         the listener invoked when an event of {@code type} is posted
     * @param <T>              the event type
     */
    default <T> void addListener(boolean receiveCancelled, Class<T> type, Consumer<T> consumer) {
        addListener(type, consumer);
    }

    /**
     * Registers an event listener with priority and cancelled-receipt flags
     * (full NeoForge convention overload, used by mods that wrap registration
     * in their own helpers). Priority ordering is not modelled by the shim
     * bus; the flag arguments are accepted and ignored.
     *
     * @param priority         the listener priority
     * @param receiveCancelled whether to receive cancelled events
     * @param type             the event class
     * @param consumer         the listener invoked when an event of {@code type} is posted
     * @param <T>              the event type
     */
    default <T> void addListener(EventPriority priority, boolean receiveCancelled,
            Class<T> type, Consumer<T> consumer) {
        addListener(type, consumer);
    }

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

    /**
     * Registers all {@code @SubscribeEvent}-annotated methods on the given
     * target object as event listeners. The event type is inferred from each
     * method's single parameter.
     *
     * @param target the object whose annotated methods to register
     */
    void register(Object target);
}
