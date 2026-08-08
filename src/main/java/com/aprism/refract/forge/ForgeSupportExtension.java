package com.aprism.refract.forge;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * Forge-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code Fo}). Registers the {@code forge-mods/} folder so that Aprism scans
 * it for genuine Forge mods.
 *
 * <p>This class is bundled in the Forge-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). The Forge runtime bridge
 * ({@code ForgeEntrypointBridge} + Forge API shim) lives in Aprism's
 * {@code aprism-loader-core}; this extension only declares the loader-support
 * folder.
 *
 * <p>Per FACT.md 9.14 the loader key {@code Fo} is reserved for Forge.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ForgeSupportExtension implements IAprismExtension {

    /** The Forge loader key (reserved per Aprism FACT.md 9.14). */
    public static final String FORGE_KEY = "Fo";

    /** The mod folder handled by Forge loader support. */
    public static final String FORGE_MODS_FOLDER = "forge-mods";

    @Override
    public void onInitialize(ExtensionContext context) {
        context.registerLoaderSupport(FORGE_KEY, FORGE_MODS_FOLDER);
        context.getLogger().info("Forge-Support registered: scanning "
                + FORGE_MODS_FOLDER + "/ for Forge mods (loader key "
                + FORGE_KEY + ")");
    }
}
