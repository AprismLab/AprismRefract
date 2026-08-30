package net.fabricmc.api;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Fabric API shim: the entrypoint interface implemented by Quilt mods'
 * {@code init} entrypoints (Quilt loader ships a built-in Fabric API
 * compatibility layer, so genuine Quilt mods implement this interface).
 * Bundled with the Quilt-Support extension (.aep) so that genuine Quilt mods
 * can be instantiated and invoked without the real Quilt loader on the
 * classpath.
 *
 * <p>Mirrors the Quilt-loader-provided {@code net.fabricmc.api.ModInitializer}
 * contract: a single no-arg {@code onInitialize()} called during mod
 * initialization.
 *
 * <p>Extracted from the Aprism core ({@code aprism-loader-core}) in
 * v26.0-Alpha.2 per the loader-support extraction: each loader's translation
 * layer (bridge + API shims) lives on its own AprismRefract branch.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface ModInitializer {

    /**
     * Runs the mod initializer. Called once during the INIT phase.
     */
    void onInitialize();
}
