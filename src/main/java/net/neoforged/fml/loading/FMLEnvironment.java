package net.neoforged.fml.loading;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.LogicalSide;

/**
 * NeoForge API shim: static environment information. Many NeoForge mods
 * query {@code FMLEnvironment.dist} or {@code FMLEnvironment.side} during
 * initialization to conditionally register client-only or server-only
 * content. Under Aprism, the values are determined from the running agent
 * arguments (mcEdit + side).
 *
 * <p>Default values assume a client-side JE environment. The
 * {@link #init(Dist, LogicalSide)} method allows the Aprism runtime to
 * override these values based on the actual launch configuration.
 *
 * @author BlockConnect@StarsailsClover
 */
public final class FMLEnvironment {

    /** The distribution side (CLIENT or DEDICATED_SERVER). */
    public static volatile Dist dist = Dist.CLIENT;

    /** The logical side (CLIENT or SERVER). */
    public static volatile LogicalSide side = LogicalSide.CLIENT;

    /** Whether the runtime is a development environment (always false under Aprism). */
    public static volatile boolean production = true;

    /** The naming mapping type (always "srg" under Aprism for compatibility). */
    public static volatile String naming = "srg";

    private FMLEnvironment() {
    }

    /**
     * Initializes the environment values. Called by the Aprism runtime
     * during extension loading to reflect the actual launch configuration.
     *
     * @param dist the distribution side
     * @param side the logical side
     */
    public static void init(Dist dist, LogicalSide side) {
        FMLEnvironment.dist = dist;
        FMLEnvironment.side = side;
    }
}
