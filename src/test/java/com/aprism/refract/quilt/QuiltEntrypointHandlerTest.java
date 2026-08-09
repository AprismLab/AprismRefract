package com.aprism.refract.quilt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aprism.api.AprismPhase;
import com.aprism.loader.LoadedModContainer;
import com.aprism.manifest.AprismManifest;
import com.aprism.refract.quilt.test.QuiltRecordingMod;

/**
 * Unit tests for {@link QuiltEntrypointHandler}: SPI contract (loader key,
 * exclusivity) and manifest-driven entrypoint dispatch without a full runtime.
 * The Quilt-native {@code init} key is projected to {@code main} by the Aprism
 * manifest projection, so the handler reads the {@code main} key.
 *
 * @author BlockConnect@StarsailsClover
 */
class QuiltEntrypointHandlerTest {

    private static final String FIXTURE_CLASS =
            "com.aprism.refract.quilt.test.QuiltRecordingMod";

    private QuiltEntrypointHandler handler;

    @BeforeEach
    void setUp() {
        QuiltRecordingMod.resetGlobal();
        handler = new QuiltEntrypointHandler(null, null);
    }

    private static LoadedModContainer container(Map<String, List<String>> entrypoints) {
        AprismManifest manifest = new AprismManifest(
                1, "quiltmod", "1.0.0", "Quilt Mod", "desc", "*",
                entrypoints, List.of(), Map.of(), Map.of(), null, List.of(), Map.of());
        return new LoadedModContainer(manifest, Path.of("quiltmod.jar"), "Q");
    }

    @Test
    void servesQuiltKeyAndIsExclusive() {
        assertThat(handler.loaderKey()).isEqualTo("Q");
        assertThat(handler.isExclusive()).isTrue();
    }

    @Test
    void invokesMainEntrypointOnInit() {
        handler.invoke(container(Map.of("main", List.of(FIXTURE_CLASS))), AprismPhase.INIT);
        assertThat(QuiltRecordingMod.getGlobalCalls()).containsExactly("main");
    }

    @Test
    void invokesClientAndServerEntrypoints() {
        Map<String, List<String>> eps = Map.of(
                "client", List.of(FIXTURE_CLASS),
                "server", List.of(FIXTURE_CLASS));
        handler.invoke(container(eps), AprismPhase.CLIENT);
        handler.invoke(container(eps), AprismPhase.SERVER);
        assertThat(QuiltRecordingMod.getGlobalCalls()).containsExactly("client", "server");
    }

    @Test
    void preinitSetupCompleteDispatchNothing() {
        LoadedModContainer c = container(Map.of("main", List.of(FIXTURE_CLASS)));
        handler.invoke(c, AprismPhase.PREINIT);
        handler.invoke(c, AprismPhase.SETUP);
        handler.invoke(c, AprismPhase.COMPLETE);
        assertThat(QuiltRecordingMod.getGlobalCalls()).isEmpty();
    }

    @Test
    void emptyEntrypointsAreANoOp() {
        handler.invoke(container(Map.of()), AprismPhase.INIT);
        assertThat(QuiltRecordingMod.getGlobalCalls()).isEmpty();
    }

    @Test
    void firstInstanceIsRetainedOnContainer() {
        LoadedModContainer c = container(Map.of("main", List.of(FIXTURE_CLASS)));
        handler.invoke(c, AprismPhase.INIT);
        assertThat(c.getInstance()).isNotNull();
        assertThat(c.getInstance(QuiltRecordingMod.class)).isPresent();
    }
}
