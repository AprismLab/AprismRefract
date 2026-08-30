package net.minecraftforge.fml.common;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forge API shim: the {@code @Mod} annotation that marks a Forge mod's
 * entrypoint class. Bundled with the Forge-Support extension (.aep) so that
 * genuine Forge mods can be discovered and instantiated without the real
 * Forge/FML runtime on the classpath.
 *
 * <p>Mirrors the Forge {@code net.minecraftforge.fml.common.Mod} contract: the
 * annotation {@link #value()} must match one of the mod ids declared in the
 * mod's {@code META-INF/mods.toml}. Forge entrypoints are NOT declared in the
 * manifest; they are discovered by scanning classes for this annotation. The
 * annotated class's constructor IS the mod initialization (optionally
 * accepting an {@code IEventBus}).
 *
 * <p>Extracted from the Aprism core ({@code aprism-loader-core}) in
 * v26.0-Alpha.2 per the loader-support extraction: each loader's translation
 * layer (bridge + API shims) lives on its own AprismRefract branch.
 *
 * @author BlockConnect@StarsailsClover
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Mod {

    /**
     * The mod id this entrypoint class belongs to. Must match one of the mod
     * ids in {@code META-INF/mods.toml}.
     *
     * @return the mod id
     */
    String value();
}
