package net.fabricmc.api;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Fabric API shim: environment type enum. Used by mods to distinguish
 * client from server execution at runtime and in {@link Environment}
 * annotations.
 *
 * @author BlockConnect@StarsailsClover
 */
public enum EnvType {
    CLIENT,
    SERVER
}
