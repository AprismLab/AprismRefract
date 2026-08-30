package net.fabricmc.loader.api;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Fabric Loader API shim: the central loader facade. Many Quilt mods call
 * {@code FabricLoader.getInstance()} during initialization to query the
 * environment type, check mod presence, or obtain mod instances. Under
 * Aprism this shim provides those answers from the Aprism runtime state.
 *
 * <p>Quilt ships a built-in Fabric compatibility layer, so most Quilt mods
 * use this facade rather than the native {@code org.quiltmc.loader.api}
 * surface. This shim serves both.
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
     * @return unmodifiable collection of mod ids as containers
     */
    java.util.Collection<String> getAllMods();
}

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover

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
        public java.util.Collection<String> getAllMods() {
            return AprismBridge.allMods();
        }
    };
}
