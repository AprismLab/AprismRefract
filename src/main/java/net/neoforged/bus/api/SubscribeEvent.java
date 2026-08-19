package net.neoforged.bus.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NeoForge API shim: marks a method as an event subscriber. When an object
 * is registered via {@code IEventBus.register(Object)}, all methods annotated
 * with {@code @SubscribeEvent} are discovered and registered as listeners.
 *
 * @author BlockConnect@StarsailsClover
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SubscribeEvent {
    /**
     * @return the event priority (default NORMAL)
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * @return whether to receive cancelled events (default false)
     */
    boolean receiveCanceled() default false;
}
