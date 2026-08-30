package net.neoforged.fml;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: logical side enum. Used by mods to distinguish client
 * from server execution at runtime.
 *
 * @author BlockConnect@StarsailsClover
 */
public enum LogicalSide {
    CLIENT,
    SERVER;

    /**
     * @return true if this is the client side
     */
    public boolean isClient() {
        return this == CLIENT;
    }

    /**
     * @return true if this is the server side
     */
    public boolean isServer() {
        return this == SERVER;
    }
}
