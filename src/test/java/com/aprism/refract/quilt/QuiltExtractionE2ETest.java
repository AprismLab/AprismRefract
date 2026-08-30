package com.aprism.refract.quilt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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
import com.aprism.refract.quilt.test.QuiltRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * End-to-end proof of the Quilt loader-support EXTRACTION: loads the
 * Quilt-Support {@code .aep} built by THIS branch through the real Aprism
 * runtime, then verifies a genuine Quilt mod's entrypoints are dispatched by
 * the handler that lives inside the {@code .aep} — not by any Quilt code in
 * the Aprism core.
 *
 * <p>Quilt mods implement the Fabric entrypoint interfaces through Quilt's
 * built-in Fabric compatibility layer; their entrypoints are declared in
 * {@code quilt.mod.json} under {@code init}/{@code client}/{@code server}
 * (the Aprism projection maps {@code init} to {@code main}).
 *
 * <p>The decisive assertion: the handler registered for loader key {@code Q}
 * is defined by the {@link AprismClassLoader} (i.e. loaded from the
 * {@code .aep}'s embedded jar).
 *
 * <p>Skipped when the built artifact is absent; run {@code ./gradlew build
 * packageAep} in this branch first.
 *
 * @author BlockConnect@StarsailsClover
 */
class QuiltExtractionE2ETest {

    /** The Quilt-Support .aep produced by this branch. */
    private static final Path REFRACT_AEP = Paths.get(
            "build", "aprism",
            "Quilt-Support-A[26.0,27.0)-Q[0.29,0.30)-JE-26.2.aep");

    private static final String MOD_CLASS =
            "com.aprism.refract.quilt.test.QuiltRecordingMod";

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
        QuiltRecordingMod.resetGlobal();
    }

    @AfterEach
    void tearDown() {
        AprismRuntime.instance().shutdown();
    }

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerOwnsQuiltDispatch() throws Exception {
        installAepAndMod();

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        assertThat(runtime.getLoaderFolders()).containsEntry("Q", "quilt-mods");
        assertThat(runtime.getExtension("quilt-support")).isNotNull();
        assertThat(runtime.getMods()).hasSize(1);
        assertThat(runtime.getMods().get(0).getLoaderKey()).isEqualTo("Q");

        // DECISIVE: the handler registered for "Q" is loaded FROM the .aep,
        // i.e. defined by the AprismClassLoader. This is the extraction proof.
        Object handler = LoaderEntrypointRegistry.get("Q");
        assertThat(handler).as("Quilt handler registered by the extension").isNotNull();
        assertThat(handler.getClass().getClassLoader())
                .as("handler must be loaded from the .aep's embedded jar")
                .isInstanceOf(AprismClassLoader.class);
        assertThat(handler.getClass().getName())
                .isEqualTo("com.aprism.refract.quilt.QuiltEntrypointHandler");

        // The Quilt mod's INIT entrypoint is invoked through the extracted bridge
        runtime.invokeEntrypoints(AprismPhase.INIT);
        assertThat(QuiltRecordingMod.getGlobalCalls()).containsExactly("main");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerBridgesClientAndServerPhases() throws Exception {
        installAepAndMod();

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        runtime.invokeEntrypoints(AprismPhase.INIT);
        assertThat(QuiltRecordingMod.getGlobalCalls()).containsExactly("main");
        QuiltRecordingMod.resetGlobal();

        runtime.invokeEntrypoints(AprismPhase.CLIENT);
        assertThat(QuiltRecordingMod.getGlobalCalls()).containsExactly("client");
        QuiltRecordingMod.resetGlobal();

        runtime.invokeEntrypoints(AprismPhase.SERVER);
        assertThat(QuiltRecordingMod.getGlobalCalls()).containsExactly("server");
    }

    private void installAepAndMod() throws IOException {
        Path extDir = gameRoot.resolve("aprism-extensions");
        Files.createDirectories(extDir);
        Files.copy(REFRACT_AEP, extDir.resolve("Quilt-Support.aep"),
                StandardCopyOption.REPLACE_EXISTING);
        writeQuiltModJar(gameRoot.resolve("quilt-mods/quiltmod.jar"), "quiltmod");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Writes a Quilt mod jar with a {@code quilt.mod.json} declaring
     * {@code init}/{@code client}/{@code server} entrypoints.
     */
    private static void writeQuiltModJar(Path jarFile, String id) throws IOException {
        Files.createDirectories(jarFile.getParent());
        String quiltJson = """
                {
                  "schema_version": 1,
                  "quilt_loader": {
                    "group": "com.aprism.refract.quilt.test",
                    "id": "%s",
                    "version": "1.0.0",
                    "metadata": { "name": "%s" },
                    "entrypoints": {
                      "init": ["%s"],
                      "client": ["%s"],
                      "server": ["%s"]
                    }
                  }
                }
                """.formatted(id, id, MOD_CLASS, MOD_CLASS, MOD_CLASS);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jarFile))) {
            zos.putNextEntry(new ZipEntry("quilt.mod.json"));
            zos.write(quiltJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }
}
