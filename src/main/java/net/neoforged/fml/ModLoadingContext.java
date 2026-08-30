package net.neoforged.fml;

import net.neoforged.fml.config.ModConfig;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: the mod loading context handed to mods for container
 * operations. The real FML provides one context per active mod; this shim
 * returns a shared instance whose {@link #getActiveContainer()} answers a
 * per-call stub backed by the most recently constructed mod id.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class ModLoadingContext {

    private static volatile String activeModId = "unknown";

    private ModLoadingContext() {
    }

    /**
     * Returns the shared context instance (real FML semantics).
     *
     * @return the singleton context
     */
    public static ModLoadingContext get() {
        return Holder.INSTANCE;
    }

    /**
     * Sets the id reported by {@link #getActiveContainer()}.
     * Called by the Aprism handler before constructing each mod.
     *
     * @param modId the currently-loading mod id
     */
    public static void setActiveModId(String modId) {
        activeModId = modId;
    }

    /**
     * Returns the active mod container stub.
     *
     * @return a minimal {@link ModContainer} for the active mod
     */
    public ModContainer getActiveContainer() {
        return new ModContainer(activeModId);
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    private static final class Holder {
        private Holder() {
        }

        static final ModLoadingContext INSTANCE = new ModLoadingContext();
    }
}
