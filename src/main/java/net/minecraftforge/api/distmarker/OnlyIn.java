package net.minecraftforge.api.distmarker;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forge API shim: marks a member as only present on a specific distribution
 * side. The real Forge strips annotated members at build time for the
 * opposite side; under Aprism this annotation is a no-op marker that allows
 * the class to load without {@code ClassNotFoundException}.
 *
 * @author BlockConnect@StarsailsClover
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Documented
public @interface OnlyIn {
    Dist value();
}
