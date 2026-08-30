package com.aprism.refract.liteloader;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * LiteLoader-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code L}). Registers the {@code liteloader-mods/} folder so that Aprism
 * scans it for genuine {@code .litemod} mods, and registers the LiteLoader
 * entrypoint handler that owns LiteLoader-convention dispatch.
 *
 * <p>This class is bundled in the LiteLoader-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). Since v26.0-Alpha.2 the
 * LiteLoader translation layer ({@link LiteLoaderEntrypointBridge} + the
 * {@code com.mumfrey.liteloader.core.LiteMod} shim) lives on this branch —
 * not in the Aprism core — and is supplied to the runtime through the
 * {@code LoaderEntrypointHandler} SPI seam via
 * {@link ExtensionContext#registerEntrypointHandler}.
 *
 * <p>Per FACT.md 9.14 the loader key {@code L} is reserved for LiteLoader.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class LiteLoaderSupportExtension implements IAprismExtension {

    /** The LiteLoader key (reserved per Aprism FACT.md 9.14). */
    public static final String LITELOADER_KEY = "L";

    /** The mod folder handled by LiteLoader support. */
    public static final String LITELOADER_MODS_FOLDER = "liteloader-mods";

    @Override
    public void onInitialize(ExtensionContext context) {
        context.registerLoaderSupport(LITELOADER_KEY, LITELOADER_MODS_FOLDER);
        // Own the LiteLoader entrypoint dispatch: the core delegates to this
        // handler for every mod discovered under loader key "L".
        context.registerEntrypointHandler(LITELOADER_KEY, new LiteLoaderEntrypointHandler());
        context.getLogger().info("LiteLoader-Support registered: scanning "
                + LITELOADER_MODS_FOLDER + "/ for LiteLoader mods (loader key "
                + LITELOADER_KEY + "), entrypoint dispatch owned by this extension");
    }
}
