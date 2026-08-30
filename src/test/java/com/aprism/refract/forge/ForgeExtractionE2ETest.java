package com.aprism.refract.forge;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.aprism.refract.forge.test.ForgeRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * End-to-end proof of the Forge loader-support EXTRACTION: loads the
 * Forge-Support {@code .aep} built by THIS branch through the real Aprism
 * runtime, then verifies a genuine Forge mod is constructed by the handler
 * that lives inside the {@code .aep} — not by any Forge code in the Aprism
 * core.
 *
 * <p>Forge entrypoints are annotation-discovered (not manifest-declared), so
 * this test embeds the real {@code @Mod} class bytes into the mod jar and
 * asserts through {@code container.getInstance()} + reflection.
 *
 * <p>The decisive assertion: the handler registered for loader key {@code Fo}
 * is defined by the {@link AprismClassLoader} (i.e. loaded from the
 * {@code .aep}'s embedded jar).
 *
 * <p>Skipped when the built artifact is absent; run {@code ./gradlew build
 * packageAep} in this branch first.
 *
 * @author BlockConnect@StarsailsClover
 */
class ForgeExtractionE2ETest {

    /** The Forge-Support .aep produced by this branch. */
    private static final Path REFRACT_AEP = Paths.get(
            "build", "aprism",
            "Forge-Support-A[26.0,27.0)-Fo[54.0,55.0)-JE-26.2.aep");

    private static final String MOD_CLASS =
            "com.aprism.refract.forge.test.ForgeRecordingMod";

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
        ForgeRecordingMod.resetGlobal();
    }

    @AfterEach
    void tearDown() {
        AprismRuntime.instance().shutdown();
    }

    @Test
    @EnabledIf("refractAepPresent")
    void extractedHandlerOwnsForgeDispatch() throws Exception {
        installAepAndMod();

        AprismRuntime runtime = AprismRuntime.instance();
        runtime.initialize(null, "26.1.0", "JE", "26.2");
        runtime.performLoad(gameRoot, gameRoot.resolve("aprism-extensions"));

        assertThat(runtime.getLoaderFolders()).containsEntry("Fo", "forge-mods");
        assertThat(runtime.getExtension("forge-support")).isNotNull();
        assertThat(runtime.getMods()).hasSize(1);
        assertThat(runtime.getMods().get(0).getLoaderKey()).isEqualTo("Fo");

        // DECISIVE: the handler registered for "Fo" is loaded FROM the .aep,
        // i.e. defined by the AprismClassLoader. This is the extraction proof.
        Object handler = LoaderEntrypointRegistry.get("Fo");
        assertThat(handler).as("Forge handler registered by the extension").isNotNull();
        assertThat(handler.getClass().getClassLoader())
                .as("handler must be loaded from the .aep's embedded jar")
                .isInstanceOf(AprismClassLoader.class);
        assertThat(handler.getClass().getName())
                .isEqualTo("com.aprism.refract.forge.ForgeEntrypointHandler");

        // INIT constructs the @Mod class with an injected IEventBus
        runtime.invokeEntrypoints(AprismPhase.INIT);
        LoadedModContainer container = runtime.getMods().get(0);
        assertThat(container.getInstance()).isNotNull();
        assertThat(container.getInstance().getClass().getSimpleName())
                .isEqualTo("ForgeRecordingMod");
        Object bus = container.getInstance().getClass()
                .getDeclaredMethod("getInjectedBus").invoke(container.getInstance());
        assertThat(bus).as("IEventBus injected at construction").isNotNull();
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
        Files.copy(REFRACT_AEP, extDir.resolve("Forge-Support.aep"),
                StandardCopyOption.REPLACE_EXISTING);
        writeForgeModJar(gameRoot.resolve("forge-mods/forgemod.jar"), "forgemod");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Writes a Forge mod jar containing a {@code META-INF/mods.toml} and the
     * real {@code @Mod} class bytes.
     */
    private static void writeForgeModJar(Path jarFile, String modId) throws IOException {
        Files.createDirectories(jarFile.getParent());
        String toml = """
                modLoader="javafml"
                loaderVersion="[1,)"
                license="MIT"

                [[mods]]
                modId="%s"
                version="1.0.0"
                displayName="%s"
                description="test forge mod"
                """.formatted(modId, modId);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(jarFile))) {
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
    }
}
