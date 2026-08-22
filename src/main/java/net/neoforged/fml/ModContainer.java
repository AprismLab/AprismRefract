package net.neoforged.fml;

import java.util.logging.Logger;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;

/**
 * NeoForge API shim: per-mod container. The real container carries the mod
 * metadata, config registration, and extension points; under Aprism only the
 * surface mods touch during construction is provided. {@link #registerConfig}
 * accepts and logs the request without persisting anything.
 *
 * @author BlockConnect@StarsailsClover
 */
public class ModContainer {

    private static final Logger LOG = Logger.getLogger(ModContainer.class.getName());

    private final String modId;

    /**
     * @param modId the mod id this container represents
     */
    public ModContainer(String modId) {
        this.modId = modId;
    }

    /**
     * @return the mod id
     */
    public String getModId() {
        return modId;
    }

    /**
     * Registers a config with the container (no-op under Aprism; logged).
     *
     * @param type config scope
     * @param spec the config spec
     */
    public void registerConfig(Type type, net.neoforged.fml.config.IConfigSpec spec) {
        LOG.info("[shim] registerConfig(" + type + ") for " + modId
                + " accepted (no persistence under Aprism)");
    }
}
