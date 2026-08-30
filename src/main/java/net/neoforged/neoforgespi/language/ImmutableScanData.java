package net.neoforged.neoforgespi.language;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Immutable {@link ModFileScanData} implementation produced by the
 * AprismRefract annotation scanner.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ImmutableScanData extends ModFileScanData {

    private final Set<AnnotationData> annotations;

    /**
     * @param annotations the discovered annotation entries
     */
    public ImmutableScanData(Set<AnnotationData> annotations) {
        this.annotations = Set.copyOf(annotations);
    }

    @Override
    public Set<AnnotationData> getAnnotations() {
        return annotations;
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Immutable {@link AnnotationData} entry for one discovered annotation.
     */
    public static final class Data extends AnnotationData {

        private final Type annotationType;
        private final String clazzName;
        private final String memberName;
        private final TargetType targetKind;
        private final Map<String, Object> annotationData;

        /**
         * @param annotationType the ASM annotation type
         * @param clazzName      owning class binary name
         * @param memberName     member (or class) name
         * @param targetKind     target kind
         * @param annotationData element values
         */
        public Data(Type annotationType, String clazzName, String memberName,
                TargetType targetKind, Map<String, Object> annotationData) {
            this.annotationType = annotationType;
            this.clazzName = clazzName;
            this.memberName = memberName;
            this.targetKind = targetKind;
            this.annotationData = Collections.unmodifiableMap(annotationData);
        }

        @Override
        public Type annotationType() {
            return annotationType;
        }

        @Override
        public String clazzName() {
            return clazzName;
        }

        @Override
        public String memberName() {
            return memberName;
        }

        @Override
        public TargetType memberTargetKind() {
            return targetKind;
        }

        @Override
        public Map<String, Object> annotationData() {
            return annotationData;
        }
    }
}
