package com.aprism.refract.liteloader;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * LiteLoader-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code L}). Registers the {@code liteloader-mods/} folder so that Aprism
 * scans it for genuine {@code .litemod} mods.
 *
 * <p>This class is bundled in the LiteLoader-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). The LiteLoader runtime bridge
 * ({@code LiteLoaderEntrypointBridge}: {@code LiteMod} interface bytecode scan
 * + {@code init(File)} invocation) and the {@code LiteMod} shim interface live
 * in Aprism's {@code aprism-loader-core}; this extension only declares the
 * loader-support folder.
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
        context.getLogger().info("LiteLoader-Support registered: scanning "
                + LITELOADER_MODS_FOLDER + "/ for LiteLoader mods (loader key "
                + LITELOADER_KEY + ")");
    }
}
