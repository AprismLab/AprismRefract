package com.aprism.refract.quilt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aprism.api.AprismPhase;
import com.aprism.refract.quilt.test.QuiltRecordingMod;

/**
 * Unit tests for the extracted {@link QuiltEntrypointBridge}: phase-to-method
 * mapping and reflective invocation of Quilt (Fabric-convention) entrypoints.
 *
 * @author BlockConnect@StarsailsClover
 */
class QuiltEntrypointBridgeTest {

    @BeforeEach
    void setUp() {
        QuiltRecordingMod.resetGlobal();
    }

    @Test
    void phaseMapsToQuiltMethodName() {
        assertThat(QuiltEntrypointBridge.methodNameFor(AprismPhase.INIT))
                .isEqualTo("onInitialize");
        assertThat(QuiltEntrypointBridge.methodNameFor(AprismPhase.CLIENT))
                .isEqualTo("onInitializeClient");
        assertThat(QuiltEntrypointBridge.methodNameFor(AprismPhase.SERVER))
                .isEqualTo("onInitializeServer");
    }

    @Test
    void phasesWithoutQuiltEquivalentMapToNull() {
        assertThat(QuiltEntrypointBridge.methodNameFor(AprismPhase.PREINIT)).isNull();
        assertThat(QuiltEntrypointBridge.methodNameFor(AprismPhase.SETUP)).isNull();
        assertThat(QuiltEntrypointBridge.methodNameFor(AprismPhase.COMPLETE)).isNull();
    }

    @Test
    void invokesQuiltEntrypointForPhase() {
        QuiltRecordingMod mod = new QuiltRecordingMod();
        assertThat(QuiltEntrypointBridge.invoke(mod, AprismPhase.INIT)).isTrue();
        assertThat(QuiltEntrypointBridge.invoke(mod, AprismPhase.CLIENT)).isTrue();
        assertThat(QuiltEntrypointBridge.invoke(mod, AprismPhase.SERVER)).isTrue();
        assertThat(QuiltRecordingMod.getGlobalCalls())
                .containsExactly("main", "client", "server");
    }

    @Test
    void nonQuiltPhasesAreNoOps() {
        QuiltRecordingMod mod = new QuiltRecordingMod();
        assertThat(QuiltEntrypointBridge.invoke(mod, AprismPhase.PREINIT)).isFalse();
        assertThat(QuiltEntrypointBridge.invoke(mod, AprismPhase.SETUP)).isFalse();
        assertThat(QuiltEntrypointBridge.invoke(mod, AprismPhase.COMPLETE)).isFalse();
        assertThat(QuiltRecordingMod.getGlobalCalls()).isEmpty();
    }

    @Test
    void instanceWithoutEntrypointMethodIsNotInvoked() {
        assertThat(QuiltEntrypointBridge.invoke(new Object(), AprismPhase.INIT)).isFalse();
    }
}
