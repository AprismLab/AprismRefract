package com.aprism.refract.liteloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.condition.EnabledIf;

import com.aprism.api.AprismPhase;
import com.aprism.loader.AprismClassLoader;
import com.aprism.loader.AprismRuntime;
import com.aprism.loader.LoadedModContainer;
import com.aprism.loader.loaderext.LoaderEntrypointRegistry;
import com.aprism.refract.liteloader.test.LiteModRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * End-to-end proof of the LiteLoader loader-support EXTRACTION: loads the
 * LiteLoader-Support {@code .aep} built by THIS branch through the real
 * Aprism runtime, then verifies a genuine {@code .litemod} mod is constructed
 * and its {@code init(File)} invoked by the handler that lives inside the
 * {@code .aep} — not by any LiteLoader code in the Aprism core.
 *
 * <p>LiteLoader entrypoints are interface-discovered (not manifest-declared),
 * so this test embeds the real {@code LiteMod} class bytes into the
 * {@code .litemod} archive and asserts through
 * {@code container.getInstance()} + reflection.
 *
 * <p>The decisive assertion: the handler registered for loader key {@code L}
 * is defined by the {@link AprismClassLoader} (i.e. loaded from the
 * {@code .aep}'s embedded jar).
 *
 * <p>Skipped when the built artifact is absent; run {@code ./gradlew build
 * packageAep} in this branch first.
 *
 * @author BlockConnect@StarsailsClover
 */
class LiteLoaderExtractionE2ETest {

    /** The LiteLoader-Support .aep produced by this branch. */
    private static final Path REFRACT_AEP = Paths.get(
            "build", "aprism",
            "LiteLoader-Support-A[26.0,27.0)-L[1.12,1.13)-JE-26.2.aep");

    private static final String MOD_CLASS =
            "com.aprism.refract.liteloader.test.LiteModRecordingMod";

    @TempDir
    Path gameRoot;

    /**
     * @return whether the built artifact exists (enables the test)
     */
    static boolean refractAepPresent() {
        return Files.isRegularFile(REFRACT_AEP);
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @BeforeEach
    void setUp() {
        LiteModRecordingMod.resetGlobal();
    }

    @AfterEach
    void tearDown() {
        AprismRuntime.instance().shutdown();
    }

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerOwnsLiteLoaderDispatch() throws Exception {
        installAepAndMod();

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        assertThat(runtime.getLoaderFolders()).containsEntry("L", "liteloader-mods");
        assertThat(runtime.getExtension("liteloader-support")).isNotNull();
        assertThat(runtime.getMods()).hasSize(1);
        assertThat(runtime.getMods().get(0).getLoaderKey()).isEqualTo("L");

        // DECISIVE: the handler registered for "L" is loaded FROM the .aep,
        // i.e. defined by the AprismClassLoader. This is the extraction proof.
        Object handler = LoaderEntrypointRegistry.get("L");
        assertThat(handler).as("LiteLoader handler registered by the extension").isNotNull();
        assertThat(handler.getClass().getClassLoader())
                .as("handler must be loaded from the .aep's embedded jar")
                .isInstanceOf(AprismClassLoader.class);
        assertThat(handler.getClass().getName())
                .isEqualTo("com.aprism.refract.liteloader.LiteLoaderEntrypointHandler");

        // INIT constructs the LiteMod class and invokes init(File) with the
        // mod's config folder (<gameRoot>/config/<modId>)
        runtime.invokeEntrypoints(AprismPhase.INIT);
        LoadedModContainer container = runtime.getMods().get(0);
        assertThat(container.getInstance()).isNotNull();
        File expected = gameRoot.resolve("config").resolve("litemod").toFile();
        Object received = container.getInstance().getClass()
                .getDeclaredMethod("getReceivedConfigFolder").invoke(container.getInstance());
        assertThat(received).isEqualTo(expected);
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerIsIdempotentAndNonInitNoOps() throws Exception {
        installAepAndMod();

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        runtime.invokeEntrypoints(AprismPhase.PREINIT);
        runtime.invokeEntrypoints(AprismPhase.SETUP);
        runtime.invokeEntrypoints(AprismPhase.COMPLETE);
        assertThat(runtime.getMods().get(0).getInstance()).isNull();

        runtime.invokeEntrypoints(AprismPhase.INIT);
        Object first = runtime.getMods().get(0).getInstance();
        assertThat(first).isNotNull();
        runtime.invokeEntrypoints(AprismPhase.INIT);
        assertThat(runtime.getMods().get(0).getInstance()).isSameAs(first);
    }

    private void installAepAndMod() throws IOException {
        Path extDir = gameRoot.resolve("aprism-extensions");
        Files.createDirectories(extDir);
        Files.copy(REFRACT_AEP, extDir.resolve("LiteLoader-Support.aep"),
                StandardCopyOption.REPLACE_EXISTING);
        writeLiteMod(gameRoot.resolve("liteloader-mods/litemod.litemod"), "litemod");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Writes a {@code .litemod} archive containing a {@code litemod.json} and
     * the real {@code LiteMod} class bytes.
     */
    private static void writeLiteMod(Path archiveFile, String modId) throws IOException {
        Files.createDirectories(archiveFile.getParent());
        String json = """
                {
                  "name": "%s",
                  "version": "1.0.0",
                  "mcversion": "1.12.2",
                  "revision": 1,
                  "author": "test",
                  "description": "test liteloader mod"
                }
                """.formatted(modId);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(archiveFile))) {
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
    }
}
