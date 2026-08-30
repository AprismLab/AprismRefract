package com.aprism.refract.neoforge;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
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
 * <p>Since v26.7-Alpha.1 the extension also initializes the NeoForge
 * environment shims: {@link FMLEnvironment} (dist/side) and
 * {@link NeoForge#EVENT_BUS} (global event bus). This allows genuine
 * NeoForge mods that query these static fields during class loading to
 * receive valid values instead of {@code null}.
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
        // Initialize NeoForge environment shims (v26.7-Alpha.1).
        // FMLEnvironment.dist/side are used by many NeoForge mods during
        // class loading to conditionally register client/server content.
        // Default to CLIENT side; the Aprism runtime's side argument can
        // override this via FMLEnvironment.init() if needed.
        FMLEnvironment.init(Dist.CLIENT, LogicalSide.CLIENT);

        // NeoForge.EVENT_BUS is the global game-level event bus. Many mods
        // register game event listeners on it during construction. We provide
        // a shared bus instance backed by the Aprism event bus.
        NeoForge.setEventBus(new NeoForgeEventBus());

        //GitHub@NDBlockConnect | BlockConnect@StarsailsClover
        context.registerLoaderSupport(NEOFORGE_KEY, NEOFORGE_MODS_FOLDER);
        // Own the NeoForge entrypoint dispatch: the core delegates to this
        // handler for every mod discovered under loader key "N".
        context.registerEntrypointHandler(NEOFORGE_KEY, new NeoForgeEntrypointHandler());
        context.getLogger().info("NeoForge-Support registered: scanning "
                + NEOFORGE_MODS_FOLDER + "/ for NeoForge mods (loader key "
                + NEOFORGE_KEY + "), entrypoint dispatch owned by this extension");
    }
}
