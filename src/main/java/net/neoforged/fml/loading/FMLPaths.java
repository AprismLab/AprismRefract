package net.neoforged.fml.loading;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * NeoForge API shim: standard filesystem locations. The real enum resolves
 * launcher-provided paths; under Aprism GAMEDIR falls back to the working
 * directory (or the aprism.game.root system property when present).
 *
 * @author BlockConnect@StarsailsClover
 */
public enum FMLPaths {

    /** The game root directory. */
    GAMEDIR,
    /** The config directory (gamedir/config). */
    CONFIGDIR,
    /** The mods directory (gamedir/mods). */
    MODDIR;

    /**
     * Resolves this location.
     *
     * @return the absolute path for this location
     */
    public Path get() {
        Path gameDir = resolveGameDir();
        return switch (this) {
            case GAMEDIR -> gameDir;
            case CONFIGDIR -> gameDir.resolve("config");
            case MODDIR -> gameDir.resolve("mods");
        };
    }

    private static Path resolveGameDir() {
        String prop = System.getProperty("aprism.game.root");
        if (prop != null && !prop.isBlank()) {
            return Paths.get(prop).toAbsolutePath();
        }
        return Paths.get("").toAbsolutePath();
    }
}
