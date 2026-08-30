package com.aprism.refract.forge;

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
import com.aprism.refract.forge.test.ForgeRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for {@link ForgeEntrypointHandler}: SPI contract (loader key,
 * exclusivity) and the Forge construction model — INIT-only construction,
 * idempotent re-INIT, and no-op for all other phases.
 *
 * @author BlockConnect@StarsailsClover
 */
class ForgeEntrypointHandlerTest {

    private static final String MOD_CLASS =
            "com.aprism.refract.forge.test.ForgeRecordingMod";

    private ForgeEntrypointHandler handler;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ForgeRecordingMod.resetGlobal();
        handler = new ForgeEntrypointHandler();
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @Test
    void servesForgeKeyAndIsExclusive() {
        assertThat(handler.loaderKey()).isEqualTo("Fo");
        assertThat(handler.isExclusive()).isTrue();
    }

    @Test
    void initConstructsModWithInjectedBus() throws IOException {
        LoadedModContainer c = container(writeModJar("forgemod"), "forgemod");
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNotNull();
        assertThat(c.getInstance().getClass().getSimpleName()).isEqualTo("ForgeRecordingMod");
    }

    @Test
    void initIsIdempotent() throws IOException {
        LoadedModContainer c = container(writeModJar("forgemod"), "forgemod");
        handler.invoke(c, AprismPhase.INIT);
        Object first = c.getInstance();
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isSameAs(first);
    }

    @Test
    void nonInitPhasesAreNoOps() throws IOException {
        LoadedModContainer c = container(writeModJar("forgemod"), "forgemod");
        handler.invoke(c, AprismPhase.PREINIT);
        handler.invoke(c, AprismPhase.SETUP);
        handler.invoke(c, AprismPhase.COMPLETE);
        handler.invoke(c, AprismPhase.CLIENT);
        handler.invoke(c, AprismPhase.SERVER);
        assertThat(c.getInstance()).isNull();
    }

    @Test
    void noModClassLeavesInstanceNull() throws IOException {
        Path jar = tempDir.resolve("empty.jar");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jar))) {
            zos.putNextEntry(new ZipEntry("META-INF/mods.toml"));
            zos.write("license=\"MIT\"\n".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        LoadedModContainer c = container(jar, "forgemod");
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNull();
    }

    private static LoadedModContainer container(Path jarPath, String modId) {
        AprismManifest manifest = new AprismManifest(
                1, modId, "1.0.0", "Forge Mod", "desc", "*",
                Map.of(), List.of(), Map.of(), Map.of(), null, List.of(), Map.of());
        return new LoadedModContainer(manifest, jarPath, "Fo");
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
