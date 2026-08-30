package com.aprism.refract.fabric;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.manifest.AprismManifest;
import com.aprism.refract.fabric.test.FabricRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for {@link FabricEntrypointHandler}: SPI contract (loader key,
 * exclusivity) and manifest-driven entrypoint dispatch without a full runtime.
 *
 * @author BlockConnect@StarsailsClover
 */
class FabricEntrypointHandlerTest {

    private static final String FIXTURE_CLASS =
            "com.aprism.refract.fabric.test.FabricRecordingMod";

    private FabricEntrypointHandler handler;

    @BeforeEach
    void setUp() {
        FabricRecordingMod.resetGlobal();
        handler = new FabricEntrypointHandler(null, null, null);
    }

    private static LoadedModContainer container(Map<String, List<String>> entrypoints) {
        AprismManifest manifest = new AprismManifest(
                1, "fabricmod", "1.0.0", "Fabric Mod", "desc", "*",
                entrypoints, List.of(), Map.of(), Map.of(), null, List.of(), Map.of());
        return new LoadedModContainer(manifest, Path.of("fabricmod.jar"), "Fa");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @Test
    void servesFabricKeyAndIsExclusive() {
        assertThat(handler.loaderKey()).isEqualTo("Fa");
        assertThat(handler.isExclusive()).isTrue();
    }

    @Test
    void invokesMainEntrypointOnInit() {
        handler.invoke(container(Map.of("main", List.of(FIXTURE_CLASS))), AprismPhase.INIT);
        assertThat(FabricRecordingMod.getGlobalCalls()).containsExactly("main");
    }

    @Test
    void invokesClientAndServerEntrypoints() {
        Map<String, List<String>> eps = Map.of(
                "client", List.of(FIXTURE_CLASS),
                "server", List.of(FIXTURE_CLASS));
        handler.invoke(container(eps), AprismPhase.CLIENT);
        handler.invoke(container(eps), AprismPhase.SERVER);
        assertThat(FabricRecordingMod.getGlobalCalls()).containsExactly("client", "server");
    }

    @Test
    void preinitSetupCompleteDispatchNothing() {
        LoadedModContainer c = container(Map.of("main", List.of(FIXTURE_CLASS)));
        handler.invoke(c, AprismPhase.PREINIT);
        handler.invoke(c, AprismPhase.SETUP);
        handler.invoke(c, AprismPhase.COMPLETE);
        assertThat(FabricRecordingMod.getGlobalCalls()).isEmpty();
    }

    @Test
    void emptyEntrypointsAreANoOp() {
        handler.invoke(container(Map.of()), AprismPhase.INIT);
        assertThat(FabricRecordingMod.getGlobalCalls()).isEmpty();
    }

    @Test
    void firstInstanceIsRetainedOnContainer() {
        LoadedModContainer c = container(Map.of("main", List.of(FIXTURE_CLASS)));
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNotNull();
        assertThat(c.getInstance(FabricRecordingMod.class)).isPresent();
    }
}
