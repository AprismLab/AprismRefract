package com.aprism.refract.neoforge;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * NeoForge-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code N}). Registers the {@code neoforge-mods/} folder so that Aprism
 * scans it for genuine NeoForge mods.
 *
 * <p>This class is bundled in the NeoForge-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). The NeoForge runtime bridge
 * ({@code @Mod} annotation discovery + {@code IEventBus} constructor
 * injection) lives in Aprism's {@code aprism-loader-core}; this extension
 * only declares the loader-support folder.
 *
 * <p>Per FACT.md 9.14 the loader key {@code N} is reserved for NeoForge.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class NeoForgeSupportExtension implements IAprismExtension {

    /** The NeoForge loader key (reserved per Aprism FACT.md 9.14). */
    public static final String NEOFORGE_KEY = "N";

    /** The mod folder handled by NeoForge loader support. */
    public static final String NEOFORGE_MODS_FOLDER = "neoforge-mods";

    @Override
    public void onInitialize(ExtensionContext context) {
        context.registerLoaderSupport(NEOFORGE_KEY, NEOFORGE_MODS_FOLDER);
        context.getLogger().info("NeoForge-Support registered: scanning "
                + NEOFORGE_MODS_FOLDER + "/ for NeoForge mods (loader key "
                + NEOFORGE_KEY + ")");
    }
}
