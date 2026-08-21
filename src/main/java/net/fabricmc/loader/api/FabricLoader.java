package net.fabricmc.loader.api;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;

/**
 * Fabric Loader API shim: the central loader facade. Many Fabric mods call
 * {@code FabricLoader.getInstance()} during initialization to query the
 * environment type, check mod presence, or obtain mod instances. Under
 * Aprism this shim provides those answers from the Aprism runtime state.
 *
 * <p>Supported queries:
 * <ul>
 *   <li>{@link #getEnvironmentType()} - CLIENT/SERVER</li>
 *   <li>{@link #isModLoaded(String)} - checks the Aprism mod list</li>
 *   <li>{@link #getAllMods()} - discovered mod containers (minimal)</li>
 * </ul>
 *
 * @author BlockConnect@StarsailsClover
 */
public interface FabricLoader {

    /**
     * Returns the loader singleton.
     *
     * @return the shared {@code FabricLoader} instance
     */
    static FabricLoader getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Returns the current environment type.
     *
     * @return CLIENT or SERVER
     */
    EnvType getEnvironmentType();

    /**
     * Checks whether a mod with the given id is loaded.
     *
     * @param id the mod id
     * @return true if loaded
     */
    boolean isModLoaded(String id);

    /**
     * Returns all loaded mod containers (minimal projection).
     *
     * @return unmodifiable list of mod ids as containers
     */
    List<String> getAllMods();
}

final class InstanceHolder {
    private InstanceHolder() {
    }

    static final FabricLoader INSTANCE = new FabricLoader() {
        @Override
        public EnvType getEnvironmentType() {
            return AprismBridge.environmentType();
        }

        @Override
        public boolean isModLoaded(String id) {
            return AprismBridge.isModLoaded(id);
        }

        @Override
        public List<String> getAllMods() {
            return AprismBridge.allMods();
        }
    };
}
