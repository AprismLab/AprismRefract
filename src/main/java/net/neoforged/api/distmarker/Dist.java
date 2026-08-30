package net.neoforged.api.distmarker;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: distribution side enum with side predicates. Many
 * NeoForge mods reference {@code Dist.CLIENT} / {@code Dist.DEDICATED_SERVER}
 * and call {@link #isClient()} / {@link #isServer()} in conditional logic.
 *
 * @author BlockConnect@StarsailsClover
 */
public enum Dist {
    CLIENT,
    DEDICATED_SERVER;

    /**
     * @return true if this is the client distribution
     */
    public boolean isClient() {
        return this == CLIENT;
    }

    /**
     * @return true if this is the dedicated server distribution
     */
    public boolean isServer() {
        return this == DEDICATED_SERVER;
    }
}
