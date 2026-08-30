package com.aprism.refract.forge;

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

import com.aprism.refract.forge.test.ForgeRecordingMod;

import net.minecraftforge.eventbus.api.IEventBus;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for the extracted {@link ForgeEntrypointBridge}: bytecode
 * scanning for Forge {@code @Mod} entrypoints and constructor injection of
 * the mod-scoped {@link IEventBus}.
 *
 * @author BlockConnect@StarsailsClover
 */
class ForgeEntrypointBridgeTest {

    private static final String MOD_CLASS =
            "com.aprism.refract.forge.test.ForgeRecordingMod";

    @TempDir
    Path tempDir;

    @Test
    void findsModClassByAnnotationScan() throws IOException {
        Path jar = writeModJar("forgemod");
        List<String> found = ForgeEntrypointBridge.findModClasses(jar, "forgemod");
        assertThat(found).containsExactly(MOD_CLASS);
    }

    @Test
    void returnsEmptyWhenModIdDoesNotMatch() throws IOException {
        Path jar = writeModJar("forgemod");
        List<String> found = ForgeEntrypointBridge.findModClasses(jar, "othermod");
        assertThat(found).isEmpty();
    }

    @Test
    void constructsModWithInjectedEventBus() throws Exception {
        Class<?> clazz = Class.forName(MOD_CLASS);
        IEventBus bus = new ForgeEventBus();
        Object instance = ForgeEntrypointBridge.construct(clazz, bus);
        assertThat(instance).isNotNull();
        Object injected = instance.getClass().getDeclaredMethod("getInjectedBus").invoke(instance);
        assertThat(injected).isSameAs(bus);
    }

    @Test
    void constructFallsBackToNoArgConstructorWhenNoBusConstructor() throws Exception {
        Object instance = ForgeEntrypointBridge.construct(Object.class, new ForgeEventBus());
        assertThat(instance).isNotNull();
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Writes a mod jar with the real Forge {@code @Mod} class bytes embedded. */
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
            zos.putNextEntry(new ZipEntry("META-INF/mods.toml"));
            zos.write(toml.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            String classPath = MOD_CLASS.replace('.', '/') + ".class";
            zos.putNextEntry(new ZipEntry(classPath));
            try (InputStream in = ForgeRecordingMod.class.getResourceAsStream(
                    "ForgeRecordingMod.class")) {
                zos.write(in.readAllBytes());
            }
            zos.closeEntry();
        }
        return jar;
    }
}
