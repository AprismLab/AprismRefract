package net.fabricmc.api;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Fabric API shim: the entrypoint interface implemented by Fabric mods'
 * {@code main} entrypoints. Bundled with the Fabric-Support extension (.aep)
 * so that genuine Fabric mods can be instantiated and invoked without the real
 * Fabric Loader on the classpath.
 *
 * <p>Mirrors the Fabric Loader {@code net.fabricmc.api.ModInitializer}
 * contract: a single no-arg {@code onInitialize()} called during mod
 * initialization.
 *
 * <p>Extracted from the Aprism core ({@code aprism-loader-core}) in
 * v26.0-Alpha.2 per the loader-support extraction: each loader's translation
 * layer (bridge + API shims) lives on its own AprismRefract branch so it can
 * be adapted to loader versions independently of the Aprism core.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface ModInitializer {

    /**
     * Runs the mod initializer. Called once during the INIT phase.
     */
    void onInitialize();
}
