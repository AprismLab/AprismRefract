package com.aprism.refract.forge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Implementation of the Forge {@link IEventBus} shim bundled on this branch.
 * Injected into Forge mod constructors so genuine mods can register event
 * listeners without the real Forge/FML runtime.
 *
 * <p>Extracted from the Aprism core in v26.0-Alpha.2 per the loader-support
 * extraction. Listeners are keyed by their declared event class. Posting an
 * event dispatches it to every listener whose registered type is assignable
 * from the event's runtime class, mirroring Forge's bus semantics closely
 * enough for construction-time registration patterns.
 *
 * <p>Since v26.7-Alpha.2 the bus also supports {@link #register(Object)}:
 * scanning an object for {@code @SubscribeEvent}-annotated methods and
 * registering them as listeners. The event type is inferred from each
 * method's single parameter.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ForgeEventBus implements IEventBus {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new LinkedHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> void addListener(Class<T> type, Consumer<T> consumer) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>())
                .add((Consumer<Object>) consumer);
    }

    @Override
    public void addGenericListener(Consumer<Object> consumer) {
        listeners.computeIfAbsent(Object.class, k -> new ArrayList<>())
                .add(consumer);
    }

    @Override
    public void post(Object event) {
        if (event == null) {
            return;
        }
        for (Map.Entry<Class<?>, List<Consumer<Object>>> entry : listeners.entrySet()) {
            if (entry.getKey().isInstance(event)) {
                for (Consumer<Object> listener : entry.getValue()) {
                    listener.accept(event);
                }
            }
        }
    }

    @Override
    public void register(Object target) {
        if (target == null) {
            return;
        }
        Class<?> clazz = target.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                continue;
            }
            Class<?> eventType = params[0];
            method.setAccessible(true);
            Consumer<Object> wrapper = event -> {
                try {
                    method.invoke(target, event);
                } catch (ReflectiveOperationException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    throw new RuntimeException("@SubscribeEvent method "
                            + method.getName() + " on " + clazz.getName()
                            + " threw during event dispatch", cause);
                }
            };
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add(wrapper);
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover
}
