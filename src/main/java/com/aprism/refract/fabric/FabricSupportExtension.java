package com.aprism.refract.fabric;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * Fabric-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code Fa}). Registers the {@code fabric-mods/} folder so that Aprism
 * scans it for genuine Fabric mods, and registers the Fabric entrypoint
 * handler that owns Fabric-convention dispatch.
 *
 * <p>This class is bundled in the Fabric-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). Since v26.0-Alpha.2 the
 * Fabric translation layer ({@link FabricEntrypointBridge} + the
 * {@code net.fabricmc.api.*} shims) lives on this branch — not in the Aprism
 * core — and is supplied to the runtime through the
 * {@code LoaderEntrypointHandler} SPI seam via
 * {@link ExtensionContext#registerEntrypointHandler}.
 *
 * <p>Per FACT.md 9.14 the loader key {@code Fa} is reserved for Fabric.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class FabricSupportExtension implements IAprismExtension {

    /** The Fabric loader key (reserved per Aprism FACT.md 9.14). */
    public static final String FABRIC_KEY = "Fa";

    /** The mod folder handled by Fabric loader support. */
    public static final String FABRIC_MODS_FOLDER = "fabric-mods";

    @Override
    public void onInitialize(ExtensionContext context) {
        context.registerLoaderSupport(FABRIC_KEY, FABRIC_MODS_FOLDER);
        // Own the Fabric entrypoint dispatch: the core delegates to this
        // handler for every mod discovered under loader key "Fa".
        context.registerEntrypointHandler(FABRIC_KEY,
                new FabricEntrypointHandler(context.getEventBus(), context.getRegistry(), null));
        context.getLogger().info("Fabric-Support registered: scanning "
                + FABRIC_MODS_FOLDER + "/ for Fabric mods (loader key "
                + FABRIC_KEY + "), entrypoint dispatch owned by this extension");
    }
}
