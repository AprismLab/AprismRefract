package net.minecraftforge.api.distmarker;

/**
 * Forge API shim: distribution side enum. Many Forge mods reference
 * {@code Dist.CLIENT} or {@code Dist.DEDICATED_SERVER} in {@code @OnlyIn}
 * annotations and conditional logic. This shim provides the enum so that
 * genuine Forge mods can be loaded without the real Forge runtime.
 *
 * @author BlockConnect@StarsailsClover
 */
public enum Dist {
    CLIENT,
    DEDICATED_SERVER
}
