package com.aprism.refract.quilt;

import java.lang.reflect.Method;

import com.aprism.api.AprismPhase;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Bridges Quilt loader entrypoint conventions onto Aprism phase dispatch.
 *
 * <p>Extracted from the Aprism core ({@code aprism-loader-core}) in
 * v26.0-Alpha.2 per the loader-support extraction: the core ships only the
 * {@code LoaderEntrypointHandler} seam, and each loader's translation layer
 * (bridge + API shims) lives on its own AprismRefract branch so it can be
 * adapted to Quilt loader versions independently of the Aprism core.
 *
 * <p>Quilt loader ships a built-in Fabric API compatibility layer, so genuine
 * Quilt mods implement the Fabric entrypoint interfaces
 * ({@code net.fabricmc.api.ModInitializer} and its client/server variants).
 * Their entrypoints are declared in {@code quilt.mod.json} under the
 * {@code init}/{@code client}/{@code server} keys; the Aprism manifest
 * projection maps the Quilt-native {@code init} key to {@code main} so the
 * Fabric-convention bridge dispatches Quilt mods without a Quilt-specific
 * code path:
 *
 * <ul>
 *   <li>{@code init} (projected to {@code main}) →
 *       {@code ModInitializer.onInitialize()} (no args)</li>
 *   <li>{@code client} → {@code ClientModInitializer.onInitializeClient()}</li>
 *   <li>{@code server} → {@code DedicatedServerModInitializer.onInitializeServer()}</li>
 * </ul>
 *
 * <p>Only the {@link AprismPhase#INIT}, {@link AprismPhase#CLIENT}, and
 * {@link AprismPhase#SERVER} phases map to Quilt entrypoints; Quilt has no
 * PREINIT/SETUP/COMPLETE equivalents, so those phases are no-ops for Quilt
 * mods.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class QuiltEntrypointBridge {

    /** The no-arg method name for the Quilt {@code init} (main) entrypoint. */
    public static final String MAIN_METHOD = "onInitialize";
    /** The no-arg method name for the Quilt {@code client} entrypoint. */
    public static final String CLIENT_METHOD = "onInitializeClient";
    /** The no-arg method name for the Quilt {@code server} entrypoint. */
    public static final String SERVER_METHOD = "onInitializeServer";

    private QuiltEntrypointBridge() {
    }

    /**
     * Maps an Aprism phase to the Quilt (Fabric-convention) entrypoint method
     * name to invoke.
     *
     * @param phase the Aprism lifecycle phase
     * @return the method name, or {@code null} if the phase has no Quilt
     *         equivalent
     */
    public static String methodNameFor(AprismPhase phase) {
        return switch (phase) {
            case INIT -> MAIN_METHOD;
            case CLIENT -> CLIENT_METHOD;
            case SERVER -> SERVER_METHOD;
            case PREINIT, SETUP, COMPLETE -> null;
        };
    }

    /**
     * Invokes the Quilt (Fabric-convention) entrypoint method appropriate to
     * the phase on the given entrypoint instance.
     *
     * @param instance the instantiated Quilt entrypoint object
     * @param phase    the Aprism lifecycle phase
     * @return {@code true} if an entrypoint method was found and invoked,
     *         {@code false} if the phase has no Quilt equivalent
     */
    public static boolean invoke(Object instance, AprismPhase phase) {
        String methodName = methodNameFor(phase);
        if (methodName == null) {
            return false;
        }
        Method method = findNoArgMethod(instance.getClass(), methodName);
        if (method == null) {
            return false;
        }
        try {
            method.setAccessible(true);
            method.invoke(instance);
            return true;
        } catch (ReflectiveOperationException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new RuntimeException("Quilt entrypoint " + methodName
                    + " failed on " + instance.getClass().getName(), cause);
        }
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Finds a no-arg method with the given name on the class or any of its
     * superclasses/interfaces.
     *
     * @param clazz      the entrypoint class
     * @param methodName the method name to look up
     * @return the method, or {@code null} if not found
     */
    private static Method findNoArgMethod(Class<?> clazz, String methodName) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException ignored) {
                for (Class<?> iface : c.getInterfaces()) {
                    Method m = findInInterface(iface, methodName);
                    if (m != null) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Searches an interface (and its super-interfaces) for the no-arg method.
     *
     * @param iface      the interface
     * @param methodName the method name
     * @return the method, or {@code null} if not found
     */
    private static Method findInInterface(Class<?> iface, String methodName) {
        try {
            return iface.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            for (Class<?> parent : iface.getInterfaces()) {
                Method m = findInInterface(parent, methodName);
                if (m != null) {
                    return m;
                }
            }
            return null;
        }
    }
}
