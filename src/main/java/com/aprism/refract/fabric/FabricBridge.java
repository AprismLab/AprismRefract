package com.aprism.refract.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.AprismBridge;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Bridge between the Fabric-Support extension and the Fabric Loader API
 * shim. Configures the environment type during extension initialization and
 * registers discovered mod ids so {@code FabricLoader.isModLoaded} answers
 * correctly for genuine Fabric mods.
 *
 * @author BlockConnect@StarsailsClover
 */
final class FabricBridge {

    private FabricBridge() {
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
