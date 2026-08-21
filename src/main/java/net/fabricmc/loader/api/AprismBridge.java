package net.fabricmc.loader.api;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;

/**
 * Internal bridge between the Fabric Loader API shim and the Aprism runtime
 * state. Public so the {@code com.aprism.refract.quilt} classes on the same
 * branch can configure it during extension initialization and mod discovery.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class AprismBridge {

    private static volatile EnvType envType = EnvType.CLIENT;
    private static volatile List<String> modIds = new ArrayList<>();

    private AprismBridge() {
    }

    /**
     * Configures the environment type. Called during extension initialization.
     *
     * @param type the environment type
     */
    public static void configure(EnvType type) {
        envType = type;
    }

    /**
     * Registers a discovered mod id so {@code FabricLoader.isModLoaded} and
     * {@code getAllMods} can answer.
     *
     * @param id the mod id
     */
    public static void registerMod(String id) {
        if (id != null && !modIds.contains(id)) {
            List<String> next = new ArrayList<>(modIds);
            next.add(id);
            modIds = next;
        }
    }

    static EnvType environmentType() {
        return envType;
    }

    static boolean isModLoaded(String id) {
        return modIds.contains(id);
    }

    static List<String> allMods() {
        return List.copyOf(modIds);
    }
}
