package com.aprism.refract.forge;

import com.aprism.api.ExtensionContext;
import com.aprism.api.IAprismExtension;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Forge-Support Aprism Extension entrypoint (loader-support, loader key
 * {@code Fo}). Registers the {@code forge-mods/} folder so that Aprism
 * scans it for genuine Forge mods, and registers the Forge entrypoint handler
 * that owns Forge-convention dispatch.
 *
 * <p>This class is bundled in the Forge-Support {@code .aep} and its
 * {@link #onInitialize(ExtensionContext)} is invoked by the Aprism runtime
 * during phase 1 (before any mods are scanned). Since v26.0-Alpha.2 the Forge
 * translation layer ({@link ForgeEntrypointBridge} + {@link ForgeEventBus} +
 * the {@code net.minecraftforge.*} shims) lives on this branch — not in the
 * Aprism core — and is supplied to the runtime through the
 * {@code LoaderEntrypointHandler} SPI seam via
 * {@link ExtensionContext#registerEntrypointHandler}.
 *
 * <p>Since v26.7-Alpha.2 the extension also initializes the Forge environment
 * shims: {@link FMLEnvironment} (dist/side) and {@link MinecraftForge#EVENT_BUS}
 * (global event bus). This allows genuine Forge mods that query these static
 * fields during class loading to receive valid values instead of null.
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
        // Initialize Forge environment shims (v26.7-Alpha.2).
        FMLEnvironment.init(Dist.CLIENT, LogicalSide.CLIENT);

        // MinecraftForge.EVENT_BUS is the global game-level event bus.
        MinecraftForge.setEventBus(new ForgeEventBus());

        //GitHub@NDBlockConnect | BlockConnect@StarsailsClover
        context.registerLoaderSupport(FORGE_KEY, FORGE_MODS_FOLDER);
        // Own the Forge entrypoint dispatch: the core delegates to this
        // handler for every mod discovered under loader key "Fo".
        context.registerEntrypointHandler(FORGE_KEY, new ForgeEntrypointHandler());
        context.getLogger().info("Forge-Support registered: scanning "
                + FORGE_MODS_FOLDER + "/ for Forge mods (loader key "
                + FORGE_KEY + "), entrypoint dispatch owned by this extension");
    }
}
