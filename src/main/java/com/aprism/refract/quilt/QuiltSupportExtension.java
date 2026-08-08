package com.aprism.refract.quilt;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * Quilt-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code Q}). Registers the {@code quilt-mods/} folder so that Aprism scans
 * it for genuine Quilt mods.
 *
 * <p>This class is bundled in the Quilt-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). Quilt loader ships a built-in
 * Fabric API compatibility layer, so Quilt mods implement
 * {@code net.fabricmc.api.ModInitializer}; their entrypoints are dispatched
 * through the Fabric-convention bridge in Aprism's {@code aprism-loader-core}
 * (the Quilt-native {@code init} entrypoint key is projected to {@code main}
 * during manifest projection). This extension only declares the
 * loader-support folder.
 *
 * <p>Per FACT.md 9.14 the loader key {@code Q} is reserved for Quilt.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class QuiltSupportExtension implements IAprismExtension {

    /** The Quilt loader key (reserved per Aprism FACT.md 9.14). */
    public static final String QUILT_KEY = "Q";

    /** The mod folder handled by Quilt loader support. */
    public static final String QUILT_MODS_FOLDER = "quilt-mods";

    @Override
    public void onInitialize(ExtensionContext context) {
        context.registerLoaderSupport(QUILT_KEY, QUILT_MODS_FOLDER);
        context.getLogger().info("Quilt-Support registered: scanning "
                + QUILT_MODS_FOLDER + "/ for Quilt mods (loader key "
                + QUILT_KEY + ")");
    }
}
