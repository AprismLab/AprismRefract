package net.neoforged.fml.config;

/**
 * NeoForge API shim: marker interface for config specs. The real interface
 * carries validation and reload contracts; under Aprism it exists only so
 * {@code ModContainer.registerConfig(Type, IConfigSpec)} resolves.
 *
 * @author BlockConnect@StarsailsClover
 */
public interface IConfigSpec {

    /**
     * @return true if the spec is considered loaded (always true under Aprism)
     */
    default boolean isLoaded() {
        return true;
    }
}
