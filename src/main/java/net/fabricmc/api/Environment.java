package net.fabricmc.api;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fabric API shim: marks a member as present on a specific environment side.
 * No-op marker under Aprism; allows classes using it to load without the
 * real Fabric toolchain processing.
 *
 * @author BlockConnect@StarsailsClover
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Documented
public @interface Environment {
    EnvType value();
}
