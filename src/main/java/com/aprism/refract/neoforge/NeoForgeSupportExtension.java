package com.aprism.refract.neoforge;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

/**
 * NeoForge-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code N}). Registers the {@code neoforge-mods/} folder so that Aprism
 * scans it for genuine NeoForge mods, and registers the NeoForge entrypoint
 * handler that owns NeoForge-convention dispatch.
 *
 * <p>This class is bundled in the NeoForge-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). Since v26.0-Alpha.2 the
 * NeoForge translation layer ({@link NeoForgeEntrypointBridge} +
 * {@link NeoForgeEventBus} + the {@code net.neoforged.*} shims) lives on this
 * branch — not in the Aprism core — and is supplied to the runtime through
 * the {@code LoaderEntrypointHandler} SPI seam via
 * {@link ExtensionContext#registerEntrypointHandler}.
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
        // Own the NeoForge entrypoint dispatch: the core delegates to this
        // handler for every mod discovered under loader key "N".
        context.registerEntrypointHandler(NEOFORGE_KEY, new NeoForgeEntrypointHandler());
        context.getLogger().info("NeoForge-Support registered: scanning "
                + NEOFORGE_MODS_FOLDER + "/ for NeoForge mods (loader key "
                + NEOFORGE_KEY + "), entrypoint dispatch owned by this extension");
    }
}
