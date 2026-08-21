package net.fabricmc.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fabric API shim: marks a member as present on a specific environment side.
 * The real Fabric toolchain strips annotated members at build time for the
 * opposite side; under Aprism this annotation is a no-op marker that allows
 * the class to load without {@code ClassNotFoundException}.
 *
 * @author BlockConnect@StarsailsClover
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Documented
public @interface Environment {
    EnvType value();

    /**
     * Fabric API shim: marker interface qualifier used with
     * {@code @EnvironmentInterface}. No-op under Aprism.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Client {
    }

    /**
     * Fabric API shim: marker interface qualifier used with
     * {@code @EnvironmentInterface}. No-op under Aprism.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Server {
    }
}
