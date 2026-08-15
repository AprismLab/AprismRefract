package com.aprism.refract.quilt;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * Quilt-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code Q}). Registers the {@code quilt-mods/} folder so that Aprism scans
 * it for genuine Quilt mods, and registers the Quilt entrypoint handler that
 * owns Quilt-convention dispatch.
 *
 * <p>This class is bundled in the Quilt-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). Since v26.0-Alpha.2 the Quilt
 * translation layer ({@link QuiltEntrypointBridge} + the
 * {@code net.fabricmc.api.*} shims that Quilt mods use through Quilt's
 * built-in Fabric compatibility layer) lives on this branch — not in the
 * Aprism core — and is supplied to the runtime through the
 * {@code LoaderEntrypointHandler} SPI seam via
 * {@link ExtensionContext#registerEntrypointHandler}.
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
        // Own the Quilt entrypoint dispatch: the core delegates to this
        // handler for every mod discovered under loader key "Q".
        context.registerEntrypointHandler(QUILT_KEY,
                new QuiltEntrypointHandler(context.getEventBus(), context.getRegistry(), null));
        context.getLogger().info("Quilt-Support registered: scanning "
                + QUILT_MODS_FOLDER + "/ for Quilt mods (loader key "
                + QUILT_KEY + "), entrypoint dispatch owned by this extension");
    }
}
