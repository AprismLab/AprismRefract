package com.aprism.refract.neoforge;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.aprism.refract.neoforge.test.NeoForgeRecordingMod;

import net.neoforged.bus.api.IEventBus;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for the extracted {@link NeoForgeEntrypointBridge}: bytecode
 * scanning for {@code @Mod} entrypoints and constructor injection of the
 * mod-scoped {@link IEventBus}.
 *
 * @author BlockConnect@StarsailsClover
 */
class NeoForgeEntrypointBridgeTest {

    private static final String MOD_CLASS =
            "com.aprism.refract.neoforge.test.NeoForgeRecordingMod";

    @TempDir
    Path tempDir;

    @Test
    void findsModClassByAnnotationScan() throws IOException {
        Path jar = writeModJar("neoforgemod");
        List<String> found = NeoForgeEntrypointBridge.findModClasses(jar, "neoforgemod");
        assertThat(found).containsExactly(MOD_CLASS);
    }

    @Test
    void returnsEmptyWhenModIdDoesNotMatch() throws IOException {
        Path jar = writeModJar("neoforgemod");
        // Scan for a different mod id: the @Mod value won't match
        List<String> found = NeoForgeEntrypointBridge.findModClasses(jar, "othermod");
        assertThat(found).isEmpty();
    }

    @Test
    void constructsModWithInjectedEventBus() throws Exception {
        Class<?> clazz = Class.forName(MOD_CLASS);
        IEventBus bus = new NeoForgeEventBus();
        Object instance = NeoForgeEntrypointBridge.construct(clazz, bus);
        assertThat(instance).isNotNull();
        Object injected = instance.getClass().getDeclaredMethod("getInjectedBus").invoke(instance);
        assertThat(injected).isSameAs(bus);
    }

    @Test
    void constructFallsBackToNoArgConstructorWhenNoBusConstructor() throws Exception {
        // Object has no IEventBus constructor, so construct must fall back to
        // the no-arg constructor rather than failing.
        Object instance = NeoForgeEntrypointBridge.construct(Object.class, new NeoForgeEventBus());
        assertThat(instance).isNotNull();
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Writes a mod jar with the real {@code @Mod} class bytes embedded. */
    private Path writeModJar(String modId) throws IOException {
        Path jar = tempDir.resolve(modId + ".jar");
        String toml = """
                modLoader="javafml"
                loaderVersion="[1,)"
                license="MIT"

                [[mods]]
                modId="%s"
                version="1.0.0"
                """.formatted(modId);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jar))) {
            zos.putNextEntry(new ZipEntry("META-INF/neoforge.mods.toml"));
            zos.write(toml.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            String classPath = MOD_CLASS.replace('.', '/') + ".class";
            zos.putNextEntry(new ZipEntry(classPath));
            try (InputStream in = NeoForgeRecordingMod.class.getResourceAsStream(
                    "NeoForgeRecordingMod.class")) {
                zos.write(in.readAllBytes());
            }
            zos.closeEntry();
        }
        return jar;
    }
}
