package net.neoforged.fml.common;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NeoForge API shim: the {@code @Mod} annotation that marks a NeoForge mod's
 * entrypoint class. Bundled with the NeoForge-Support extension (.aep) so
 * that genuine NeoForge mods can be discovered and instantiated without the
 * real NeoForge/FML runtime on the classpath.
 *
 * <p>Mirrors the NeoForge {@code net.neoforged.fml.common.Mod} contract: the
 * annotation {@link #value()} must match one of the mod ids declared in the
 * mod's {@code META-INF/neoforge.mods.toml}. NeoForge entrypoints are NOT
 * declared in the manifest; they are discovered by scanning classes for this
 * annotation. The annotated class's constructor IS the mod initialization
 * (optionally accepting an {@code IEventBus}).
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
     * ids in {@code META-INF/neoforge.mods.toml}.
     *
     * @return the mod id
     */
    String value();
}
