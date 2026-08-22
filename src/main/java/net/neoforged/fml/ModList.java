package net.neoforged.fml;

import java.nio.file.Path;
import java.util.List;

/**
 * NeoForge API shim: the loaded-mod list facade. Mods query {@code ModList.get().isLoaded(id)}
 * for inter-mod presence checks. Under Aprism answers come from the ids
 * registered by the SupportExtension handler during mod discovery.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ModList {

    private static volatile List<String> loadedIds = List.of();

    private ModList() {
    }

    /**
     * Returns the shared ModList instance.
     *
     * @return the singleton
     */
    public static ModList get() {
        return Holder.INSTANCE;
    }

    /**
     * Updates the known mod ids. Called by the Aprism handler after each
     * discovery pass.
     *
     * @param ids all discovered mod ids
     */
    public static void setLoadedMods(List<String> ids) {
        loadedIds = List.copyOf(ids);
    }

    /**
     * Checks whether a mod is loaded.
     *
     * @param id the mod id
     * @return true if loaded
     */
    public boolean isLoaded(String id) {
        return loadedIds.contains(id);
    }

    /**
     * Returns all loaded mod ids.
     *
     * @return unmodifiable list
     */
    public List<String> getLoadedMods() {
        return loadedIds;
    }

    /**
     * Returns annotation scan data across all mod files (empty under Aprism:
     * the shim runtime performs no annotation scan pass).
     *
     * @return unmodifiable empty list
     */
    public List<net.neoforged.neoforgespi.language.ModFileScanData> getAllScanData() {
        return List.of();
    }

    private static final class Holder {
        private Holder() {
        }

        static final ModList INSTANCE = new ModList();
    }
}
