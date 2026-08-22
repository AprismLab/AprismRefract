package com.aprism.refract.neoforge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.language.ModFileScanData.TargetType;
import net.neoforged.neoforgespi.language.ImmutableScanData;
import net.neoforged.neoforgespi.language.ImmutableScanData.Data;

/**
 * ASM-based annotation scanner producing real {@link ModFileScanData} for a
 * mod archive (v26.8-Alpha.1). Walks every class file in the mod jar and
 * records each runtime-visible class-level annotation with its element
 * values, mirroring the shape the real FML scan pass produces.
 *
 * <p>This closes the shim gap that left consumers such as JEI with an empty
 * plugin set: their plugin discovery reads scan data for marker annotations
 * (e.g. {@code @JeiPlugin}) and resolves the carrying classes by member name.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ModAnnotationScanner {

    private ModAnnotationScanner() {
    }

    /**
     * Scans the given mod source (jar file) for all runtime-visible
     * class-level annotations.
     *
     * @param modSource the mod archive path
     * @return scan data carrying every discovered entry (never null)
     */
    public static ModFileScanData scan(Path modSource) {
        Set<ModFileScanData.AnnotationData> found = new HashSet<>();
        if (modSource != null && Files.isRegularFile(modSource)) {
            try (FileSystem fs = FileSystems.newFileSystem(modSource, (ClassLoader) null)) {
                try (Stream<Path> stream = Files.walk(fs.getPath("/"))) {
                    List<Path> classFiles = stream
                            .filter(p -> p.toString().endsWith(".class"))
                            .filter(p -> !p.toString().contains("module-info"))
                            .toList();
                    for (Path classFile : classFiles) {
                        collect(classFile, found);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to scan mod jar for annotations: "
                        + modSource, e);
            }
        }
        return new ImmutableScanData(found);
    }

    /**
     * Reads one class file and adds every runtime-visible class annotation.
     */
    private static void collect(Path classFile, Set<ModFileScanData.AnnotationData> out) {
        try (InputStream in = Files.newInputStream(classFile)) {
            ClassReader reader = new ClassReader(in);
            ClassCollector visitor = new ClassCollector(out);
            reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
        } catch (IOException e) {
            // Unreadable single class: skip, do not fail the whole scan.
        }
    }

    /**
     * Visitor capturing every runtime-visible class-level annotation.
     */
    private static final class ClassCollector extends ClassVisitor {

        private final Set<ModFileScanData.AnnotationData> out;
        private String internalName;

        ClassCollector(Set<ModFileScanData.AnnotationData> out) {
            super(Opcodes.ASM9);
            this.out = out;
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                String superName, String[] interfaces) {
            this.internalName = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            // Both RUNTIME- and CLASS-retention annotations are collected:
            // real NeoForge plugin markers (e.g. @JeiPlugin) are CLASS-retention
            // (no RuntimeVisibleAnnotations), and the real FML scan sees them.
            if (internalName == null) {
                return super.visitAnnotation(descriptor, visible);
            }
            Type type = Type.getType(descriptor);
            String binaryName = internalName.replace('/', '.');
            Map<String, Object> values = new HashMap<>();
            return new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public void visit(String name, Object value) {
                    values.put(name, value);
                }

                @Override
                public void visitEnd() {
                    out.add(new Data(type, binaryName, binaryName,
                            TargetType.CLASS, Map.copyOf(values)));
                }
            };
        }
    }
}
