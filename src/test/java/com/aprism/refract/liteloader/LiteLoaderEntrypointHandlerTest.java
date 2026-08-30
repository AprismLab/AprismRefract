package com.aprism.refract.liteloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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
import com.aprism.refract.liteloader.test.LiteModRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for {@link LiteLoaderEntrypointHandler}: SPI contract (loader
 * key, exclusivity) and the LiteLoader init model — INIT-only construction +
 * {@code init(File)} invocation, idempotent re-INIT, and no-op for all other
 * phases.
 *
 * @author BlockConnect@StarsailsClover
 */
class LiteLoaderEntrypointHandlerTest {

    private static final String MOD_CLASS =
            "com.aprism.refract.liteloader.test.LiteModRecordingMod";

    private LiteLoaderEntrypointHandler handler;

    @TempDir
    Path gameRoot;

    @BeforeEach
    void setUp() {
        LiteModRecordingMod.resetGlobal();
        handler = new LiteLoaderEntrypointHandler();
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @Test
    void servesLiteLoaderKeyAndIsExclusive() {
        assertThat(handler.loaderKey()).isEqualTo("L");
        assertThat(handler.isExclusive()).isTrue();
    }

    @Test
    void initConstructsAndInvokesInitWithConfigFolder() throws IOException {
        LoadedModContainer c = container(writeLiteMod("litemod"), "litemod");
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNotNull();
        // init(File) was invoked with <gameRoot>/config/<modId>
        File expected = gameRoot.resolve("config").resolve("litemod").toFile();
        Object received = reflectionConfigFolder(c.getInstance());
        assertThat(received).isEqualTo(expected);
    }

    @Test
    void initIsIdempotent() throws IOException {
        LoadedModContainer c = container(writeLiteMod("litemod"), "litemod");
        handler.invoke(c, AprismPhase.INIT);
        Object first = c.getInstance();
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isSameAs(first);
    }

    @Test
    void nonInitPhasesAreNoOps() throws IOException {
        LoadedModContainer c = container(writeLiteMod("litemod"), "litemod");
        handler.invoke(c, AprismPhase.PREINIT);
        handler.invoke(c, AprismPhase.SETUP);
        handler.invoke(c, AprismPhase.COMPLETE);
        handler.invoke(c, AprismPhase.CLIENT);
        handler.invoke(c, AprismPhase.SERVER);
        assertThat(c.getInstance()).isNull();
    }

    @Test
    void noLiteModClassLeavesInstanceNull() throws IOException {
        Path archive = gameRoot.resolve("liteloader-mods/empty.litemod");
        Files.createDirectories(archive.getParent());
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(archive))) {
            zos.putNextEntry(new ZipEntry("litemod.json"));
            zos.write("{\"name\":\"empty\"}".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        LoadedModContainer c = container(archive, "empty");
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNull();
    }

    private static Object reflectionConfigFolder(Object instance) {
        try {
            return instance.getClass().getDeclaredMethod("getReceivedConfigFolder")
                    .invoke(instance);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private LoadedModContainer container(Path archivePath, String modId) {
        AprismManifest manifest = new AprismManifest(
                1, modId, "1.0.0", "LiteLoader Mod", "desc", "*",
                Map.of(), List.of(), Map.of(), Map.of(), null, List.of(), Map.of());
        return new LoadedModContainer(manifest, archivePath, "L");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Writes a .litemod archive with the real {@code LiteMod} class bytes. */
    private Path writeLiteMod(String modId) throws IOException {
        Path archive = gameRoot.resolve("liteloader-mods").resolve(modId + ".litemod");
        Files.createDirectories(archive.getParent());
        String json = """
                {
                  "name": "%s",
                  "version": "1.0.0",
                  "mcversion": "1.12.2",
                  "revision": 1
                }
                """.formatted(modId);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(archive))) {
            zos.putNextEntry(new ZipEntry("litemod.json"));
            zos.write(json.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            String classPath = MOD_CLASS.replace('.', '/') + ".class";
            zos.putNextEntry(new ZipEntry(classPath));
            try (InputStream in = LiteModRecordingMod.class.getResourceAsStream(
                    "LiteModRecordingMod.class")) {
                zos.write(in.readAllBytes());
            }
            zos.closeEntry();
        }
        return archive;
    }
}
