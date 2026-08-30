package com.aprism.refract.liteloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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

import com.aprism.refract.liteloader.test.LiteModRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for the extracted {@link LiteLoaderEntrypointBridge}: bytecode
 * scanning of {@code .litemod} archives for {@code LiteMod} entrypoints and
 * reflective invocation of {@code init(File)}.
 *
 * @author BlockConnect@StarsailsClover
 */
class LiteLoaderEntrypointBridgeTest {

    private static final String MOD_CLASS =
            "com.aprism.refract.liteloader.test.LiteModRecordingMod";

    @TempDir
    Path tempDir;

    @Test
    void findsLiteModClassByInterfaceScan() throws IOException {
        Path archive = writeLiteMod("litemod");
        List<String> found = LiteLoaderEntrypointBridge.findModClasses(archive);
        assertThat(found).contains(MOD_CLASS);
    }

    @Test
    void invokeInitPassesConfigFolder() throws Exception {
        LiteModRecordingMod mod = new LiteModRecordingMod();
        LiteModRecordingMod.resetGlobal();
        File configFolder = tempDir.resolve("config/litemod").toFile();
        boolean invoked = LiteLoaderEntrypointBridge.invokeInit(mod, configFolder);
        assertThat(invoked).isTrue();
        assertThat(mod.getReceivedConfigFolder()).isEqualTo(configFolder);
        assertThat(LiteModRecordingMod.getGlobalCalls()).containsExactly("init");
    }

    @Test
    void invokeInitReturnsFalseWithoutInitMethod() {
        assertThat(LiteLoaderEntrypointBridge.invokeInit(new Object(), new File("x"))).isFalse();
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /** Writes a .litemod archive with the real {@code LiteMod} class bytes. */
    private Path writeLiteMod(String modId) throws IOException {
        Path archive = tempDir.resolve(modId + ".litemod");
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
