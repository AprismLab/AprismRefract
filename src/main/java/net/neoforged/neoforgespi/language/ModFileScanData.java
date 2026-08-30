package net.neoforged.neoforgespi.language;

import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge SPI shim: per-mod annotation scan result. Under Aprism the scan
 * is performed by the loader-support extension itself (ASM pass over the
 * mod jar's class files), producing the same shape the real FML scan would.
 *
 * <p>NOTE: this mirrors real NeoForge's type shape - {@code ModFileScanData}
 * is an abstract CLASS there (mod bytecode invokes its methods via
 * invokevirtual), so it must be a class here too; making it an interface
 * triggers IncompatibleClassChangeError in mods compiled upstream.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class ModFileScanData {

    /**
     * Returns all annotation entries found in the scanned mod file.
     *
     * @return the annotation set (never null)
     */
    public abstract Set<AnnotationData> getAnnotations();

    /**
     * One discovered annotation entry. Abstract class per real NeoForge
     * (invoked via invokevirtual by mod bytecode).
     */
    public abstract static class AnnotationData {

        /**
         * Returns the ASM type of the annotation descriptor.
         *
         * @return the annotation type
         */
        public abstract Type annotationType();

        /**
         * Returns the binary name of the class carrying the annotation.
         *
         * @return the owning class binary name
         */
        public abstract String clazzName();

        /**
         * Returns the target member name. For class targets this is the
         * binary class name (resolvable via {@code Class.forName}).
         *
         * @return the member name
         */
        public abstract String memberName();

        /**
         * Returns what kind of target carries the annotation.
         *
         * @return the target kind
         */
        public abstract TargetType memberTargetKind();

        /**
         * Returns the annotation's element values (may be empty).
         *
         * @return unmodifiable annotation data map
         */
        public abstract Map<String, Object> annotationData();
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Kind of program element carrying an annotation.
     */
    public enum TargetType {
        /** Annotation-type target. */
        ANNOTATION_TYPE,
        /** Class target. */
        CLASS,
        /** Field target. */
        FIELD,
        /** Method target. */
        METHOD
    }
}
