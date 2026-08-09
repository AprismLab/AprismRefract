package com.aprism.refract.neoforge;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.manifest.AprismManifest;
import com.aprism.refract.neoforge.test.NeoForgeRecordingMod;

/**
 * Unit tests for {@link NeoForgeEntrypointHandler}: SPI contract (loader key,
 * exclusivity) and the NeoForge construction model — INIT-only construction,
 * idempotent re-INIT, and no-op for all other phases.
 *
 * @author BlockConnect@StarsailsClover
 */
class NeoForgeEntrypointHandlerTest {

    private static final String MOD_CLASS =
            "com.aprism.refract.neoforge.test.NeoForgeRecordingMod";

    private NeoForgeEntrypointHandler handler;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        NeoForgeRecordingMod.resetGlobal();
        handler = new NeoForgeEntrypointHandler();
    }

    @Test
    void servesNeoForgeKeyAndIsExclusive() {
        assertThat(handler.loaderKey()).isEqualTo("N");
        assertThat(handler.isExclusive()).isTrue();
    }

    @Test
    void initConstructsModWithInjectedBus() throws IOException {
        LoadedModContainer c = container(writeModJar("neoforgemod"), "neoforgemod");
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNotNull();
        assertThat(c.getInstance().getClass().getSimpleName()).isEqualTo("NeoForgeRecordingMod");
    }

    @Test
    void initIsIdempotent() throws IOException {
        LoadedModContainer c = container(writeModJar("neoforgemod"), "neoforgemod");
        handler.invoke(c, AprismPhase.INIT);
        Object first = c.getInstance();
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isSameAs(first);
    }

    @Test
    void nonInitPhasesAreNoOps() throws IOException {
        LoadedModContainer c = container(writeModJar("neoforgemod"), "neoforgemod");
        handler.invoke(c, AprismPhase.PREINIT);
        handler.invoke(c, AprismPhase.SETUP);
        handler.invoke(c, AprismPhase.COMPLETE);
        handler.invoke(c, AprismPhase.CLIENT);
        handler.invoke(c, AprismPhase.SERVER);
        assertThat(c.getInstance()).isNull();
    }

    @Test
    void noModClassLeavesInstanceNull() throws IOException {
        // Jar without any @Mod class: nothing to construct
        Path jar = tempDir.resolve("empty.jar");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jar))) {
            zos.putNextEntry(new ZipEntry("META-INF/neoforge.mods.toml"));
            zos.write("license=\"MIT\"\n".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        LoadedModContainer c = container(jar, "neoforgemod");
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNull();
    }

    private static LoadedModContainer container(Path jarPath, String modId) {
        AprismManifest manifest = new AprismManifest(
                1, modId, "1.0.0", "NeoForge Mod", "desc", "*",
                Map.of(), List.of(), Map.of(), Map.of(), null, List.of(), Map.of());
        return new LoadedModContainer(manifest, jarPath, "N");
    }

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
