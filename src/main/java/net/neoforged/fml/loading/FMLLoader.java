package net.neoforged.fml.loading;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: static loader utilities. Only the members commonly
 * probed by mods are provided; everything answers inert defaults.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class FMLLoader {

    private FMLLoader() {
    }

    /**
     * @return the current MC version string ("unknown" under Aprism)
     */
    public static String versionInfo() {
        return "unknown";
    }

    /**
     * @return true once loading finished (always true post-construction)
     */
    public static boolean isLoadingComplete() {
        return false;
    }

    /**
     * Returns the current FML loader instance (null under Aprism; callers
     * guard on null).
     *
     * @return null
     */
    public static FMLLoader getCurrentOrNull() {
        return null;
    }
}
