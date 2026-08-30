package net.fabricmc.api;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * Fabric API shim: the entrypoint interface implemented by Fabric mods'
 * {@code server} entrypoints. Bundled with the Fabric-Support extension
 * (.aep) so that genuine Fabric mods can be instantiated and invoked without
 * the real Fabric Loader on the classpath.
 *
 * <p>Mirrors the Fabric Loader
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
