package net.neoforged.api.distmarker;

/**
 * NeoForge API shim: distribution side enum. Many NeoForge mods reference
 * {@code Dist.CLIENT} or {@code Dist.DEDICATED_SERVER} in {@code @OnlyIn}
 * annotations and conditional logic. This shim provides the enum so that
 * genuine NeoForge mods can be loaded without the real NeoForge runtime.
 *
 * @author BlockConnect@StarsailsClover
 */
public enum Dist {
    CLIENT,
    DEDICATED_SERVER
}
