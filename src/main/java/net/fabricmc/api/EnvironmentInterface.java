package net.fabricmc.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fabric API shim: declares that a type implements an interface only on a
 * specific environment side. No-op marker under Aprism.
 *
 * @author BlockConnect@StarsailsClover
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnvironmentInterface {
    EnvType value();

    Class<?> itf();
}
