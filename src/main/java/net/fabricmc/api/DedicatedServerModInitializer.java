package net.fabricmc.api;

/**
 * Fabric API shim: the entrypoint interface implemented by Quilt mods'
 * {@code server} entrypoints (Quilt loader ships a built-in Fabric API
 * compatibility layer). Bundled with the Quilt-Support extension (.aep) so
 * that genuine Quilt mods can be instantiated and invoked without the real
 * Quilt loader on the classpath.
 *
 * <p>Mirrors the Quilt-loader-provided
 * {@code net.fabricmc.api.DedicatedServerModInitializer} contract.
 *
 * <p>Extracted from the Aprism core ({@code aprism-loader-core}) in
 * v26.0-Alpha.2 per the loader-support extraction.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface DedicatedServerModInitializer {

    /**
     * Runs the dedicated-server mod initializer. Called once during the SERVER
     * phase.
     */
    void onInitializeServer();
}
