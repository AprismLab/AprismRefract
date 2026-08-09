package com.aprism.refract.fabric;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
import com.aprism.refract.fabric.test.FabricRecordingMod;

/**
 * End-to-end proof of the Fabric loader-support EXTRACTION: loads the
 * Fabric-Support {@code .aep} built by THIS branch through the real Aprism
 * runtime, then verifies a genuine Fabric mod's entrypoints are dispatched by
 * the handler that lives inside the {@code .aep} — not by any Fabric code in
 * the Aprism core.
 *
 * <p>The decisive assertion: the handler registered for loader key {@code Fa}
 * is defined by the {@link AprismClassLoader} (i.e. loaded from the
 * {@code .aep}'s embedded jar). If the Aprism core's built-in Fabric bridge
 * were still doing the work, no handler would be registered at all; if a
 * test-classpath copy were used, the defining classloader would be the app
 * loader, not {@link AprismClassLoader}.
 *
 * <p>Skipped when the built artifact is absent; run {@code ./gradlew build
 * packageAep} in this branch first.
 *
 * @author BlockConnect@StarsailsClover
 */
class FabricExtractionE2ETest {

    /** The Fabric-Support .aep produced by this branch. */
    private static final Path REFRACT_AEP = Paths.get(
            "build", "aprism",
            "Fabric-Support-A[26.0,27.0)-Fa[0.16,0.17)-JE-26.2.aep");

    private static final String FABRIC_MOD_CLASS =
            "com.aprism.refract.fabric.test.FabricRecordingMod";

    @TempDir
    Path gameRoot;

    /**
     * @return whether the built artifact exists (enables the test)
     */
    static boolean refractAepPresent() {
        return Files.isRegularFile(REFRACT_AEP);
    }

    @BeforeEach
    void setUp() {
        FabricRecordingMod.resetGlobal();
    }

    @AfterEach
    void tearDown() {
        AprismRuntime.instance().shutdown();
    }

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerOwnsFabricDispatch() throws Exception {
        // Install this branch's built Fabric-Support.aep
        Path extDir = gameRoot.resolve("aprism-extensions");
        Files.createDirectories(extDir);
        Files.copy(REFRACT_AEP, extDir.resolve("Fabric-Support.aep"),
                StandardCopyOption.REPLACE_EXISTING);

        // A genuine Fabric-style mod in fabric-mods/
        writeFabricModJar(gameRoot.resolve("fabric-mods/fabricmod.jar"), "fabricmod");

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        // The extension registered the Fabric folder and the mod was discovered
        assertThat(runtime.getLoaderFolders()).containsEntry("Fa", "fabric-mods");
        assertThat(runtime.getExtension("fabric-support")).isNotNull();
        List<LoadedModContainer> mods = runtime.getMods();
        assertThat(mods).hasSize(1);
        assertThat(mods.get(0).getLoaderKey()).isEqualTo("Fa");

        // DECISIVE: the handler registered for "Fa" is loaded FROM the .aep,
        // i.e. defined by the AprismClassLoader. This is the extraction proof.
        Object handler = LoaderEntrypointRegistry.get("Fa");
        assertThat(handler).as("Fabric handler registered by the extension").isNotNull();
        assertThat(handler.getClass().getClassLoader())
                .as("handler must be loaded from the .aep's embedded jar")
                .isInstanceOf(AprismClassLoader.class);
        assertThat(handler.getClass().getName())
                .isEqualTo("com.aprism.refract.fabric.FabricEntrypointHandler");

        // The Fabric mod's INIT entrypoint is invoked through the extracted bridge
        runtime.invokeEntrypoints(AprismPhase.INIT);
        assertThat(FabricRecordingMod.getGlobalCalls()).containsExactly("main");
    }

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerBridgesClientAndServerPhases() throws Exception {
        Path extDir = gameRoot.resolve("aprism-extensions");
        Files.createDirectories(extDir);
        Files.copy(REFRACT_AEP, extDir.resolve("Fabric-Support.aep"),
                StandardCopyOption.REPLACE_EXISTING);
        writeFabricModJar(gameRoot.resolve("fabric-mods/fabricmod.jar"), "fabricmod");

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        runtime.invokeEntrypoints(AprismPhase.INIT);
        assertThat(FabricRecordingMod.getGlobalCalls()).containsExactly("main");
        FabricRecordingMod.resetGlobal();

        runtime.invokeEntrypoints(AprismPhase.CLIENT);
        assertThat(FabricRecordingMod.getGlobalCalls()).containsExactly("client");
        FabricRecordingMod.resetGlobal();

        runtime.invokeEntrypoints(AprismPhase.SERVER);
        assertThat(FabricRecordingMod.getGlobalCalls()).containsExactly("server");
    }

    private static void writeFabricModJar(Path jarFile, String id) throws IOException {
        Files.createDirectories(jarFile.getParent());
        String fabricJson = """
                {
                  "schemaVersion": 1,
                  "id": "%s",
                  "version": "1.0.0",
                  "name": "%s",
                  "environment": "*",
                  "entrypoints": {"main": ["%s"], "client": ["%s"], "server": ["%s"]}
                }
                """.formatted(id, id, FABRIC_MOD_CLASS, FABRIC_MOD_CLASS, FABRIC_MOD_CLASS);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jarFile))) {
            zos.putNextEntry(new ZipEntry("fabric.mod.json"));
            zos.write(fabricJson.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }
}
