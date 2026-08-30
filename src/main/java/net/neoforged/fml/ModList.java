package net.neoforged.fml;

import java.nio.file.Path;
import java.util.List;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: the loaded-mod list facade. Mods query {@code ModList.get().isLoaded(id)}
 * for inter-mod presence checks. Under Aprism answers come from the ids
 * registered by the SupportExtension handler during mod discovery.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ModList {

    private static volatile List<String> loadedIds = List.of();
    private static volatile List<net.neoforged.neoforgespi.language.ModFileScanData> scanData = List.of();

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

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * Returns annotation scan data across all mod files. Under v26.8+ this
     * carries REAL data produced by the loader-support extension's ASM scan
     * of each discovered mod jar.
     *
     * @return unmodifiable list of per-mod scan data
     */
    public List<net.neoforged.neoforgespi.language.ModFileScanData> getAllScanData() {
        return List.copyOf(scanData);
    }

    /**
     * Looks up a mod container by id (empty under Aprism: containers are not
     * retained by the shim facade).
     *
     * @param modId the mod id
     * @return empty optional
     */
    public java.util.Optional<ModContainer> getModContainerById(String modId) {
        return java.util.Optional.empty();
    }

    /**
     * Replaces the aggregated scan-data list. Called by the Aprism handler
     * after each mod's jar is scanned.
     *
     * @param data the full scan-data list
     */
    public static void setAllScanData(List<net.neoforged.neoforgespi.language.ModFileScanData> data) {
        scanData = List.copyOf(data);
    }

    private static final class Holder {
        private Holder() {
        }

        static final ModList INSTANCE = new ModList();
    }
}
