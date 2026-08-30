package com.aprism.refract.fabric;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aprism.api.AprismPhase;
import com.aprism.refract.fabric.test.FabricRecordingMod;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Unit tests for the extracted {@link FabricEntrypointBridge}: phase-to-method
 * mapping and reflective invocation of Fabric-convention entrypoints.
 *
 * @author BlockConnect@StarsailsClover
 */
class FabricEntrypointBridgeTest {

    @BeforeEach
    void setUp() {
        FabricRecordingMod.resetGlobal();
    }

    @Test
    void phaseMapsToFabricMethodName() {
        assertThat(FabricEntrypointBridge.methodNameFor(AprismPhase.INIT))
                .isEqualTo("onInitialize");
        assertThat(FabricEntrypointBridge.methodNameFor(AprismPhase.CLIENT))
                .isEqualTo("onInitializeClient");
        assertThat(FabricEntrypointBridge.methodNameFor(AprismPhase.SERVER))
                .isEqualTo("onInitializeServer");
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    @Test
    void phasesWithoutFabricEquivalentMapToNull() {
        assertThat(FabricEntrypointBridge.methodNameFor(AprismPhase.PREINIT)).isNull();
        assertThat(FabricEntrypointBridge.methodNameFor(AprismPhase.SETUP)).isNull();
        assertThat(FabricEntrypointBridge.methodNameFor(AprismPhase.COMPLETE)).isNull();
    }

    @Test
    void invokesFabricEntrypointForPhase() {
        FabricRecordingMod mod = new FabricRecordingMod();
        assertThat(FabricEntrypointBridge.invoke(mod, AprismPhase.INIT)).isTrue();
        assertThat(FabricEntrypointBridge.invoke(mod, AprismPhase.CLIENT)).isTrue();
        assertThat(FabricEntrypointBridge.invoke(mod, AprismPhase.SERVER)).isTrue();
        assertThat(FabricRecordingMod.getGlobalCalls())
                .containsExactly("main", "client", "server");
    }

    @Test
    void nonFabricPhasesAreNoOps() {
        FabricRecordingMod mod = new FabricRecordingMod();
        assertThat(FabricEntrypointBridge.invoke(mod, AprismPhase.PREINIT)).isFalse();
        assertThat(FabricEntrypointBridge.invoke(mod, AprismPhase.SETUP)).isFalse();
        assertThat(FabricEntrypointBridge.invoke(mod, AprismPhase.COMPLETE)).isFalse();
        assertThat(FabricRecordingMod.getGlobalCalls()).isEmpty();
    }

    @Test
    void instanceWithoutEntrypointMethodIsNotInvoked() {
        assertThat(FabricEntrypointBridge.invoke(new Object(), AprismPhase.INIT)).isFalse();
    }
}
