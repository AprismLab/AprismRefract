package com.aprism.refract.quilt;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.AprismBridge;

/**
 * Bridge between the Quilt-Support extension and the Fabric Loader API shim.
 * Configures the environment type during extension initialization and
 * registers discovered mod ids so {@code FabricLoader.isModLoaded} answers
 * correctly for genuine Quilt/Fabric-convention mods.
 *
 * @author BlockConnect@StarsailsClover
 */
final class QuiltLoaderBridge {

    private QuiltLoaderBridge() {
    }

    /**
     * Configures the Fabric Loader shim environment type.
     *
     * @param type the environment type (CLIENT default under Aprism JE client)
     */
    static void configureEnvironment(EnvType type) {
        AprismBridge.configure(type);
    }

    /**
     * Registers a discovered mod id with the Fabric Loader shim.
     *
     * @param id the mod id from the projected manifest
     */
    static void registerMod(String id) {
        AprismBridge.registerMod(id);
    }
}
