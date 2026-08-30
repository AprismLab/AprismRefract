package net.neoforged.fml.config;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: config type holder. The real class carries file paths
 * and reload callbacks; under Aprism only the {@link Type} enum and a
 * constructor-compatible shape are provided.
 *
 * @author BlockConnect@StarsailsClover
 */
public abstract class ModConfig {

    /** Config scope. */
    public enum Type {
        CLIENT,
        COMMON,
        SERVER,
        STARTUP
    }
}
