package net.neoforged.neoforge.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.Registry;

import net.neoforged.bus.api.IEventBus;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
/**
 * NeoForge API shim: deferred registry helper (MC-typed variant, compiled
 * only against a local unobfuscated client jar - see build.gradle). Provides
 * the construction-time surface deep mods use: {@code create(registry, modid)}
 * plus entry registration that records (but does not attach) entries.
 *
 * <p>Under Aprism the real registry attachment happens through the core's
 * content-binding/game-event machinery; this shim guarantees signature
 * compatibility so mod classes link and their constructors complete.
 *
 * @author BlockConnect@StarsailsClover
 */
public class DeferredRegister<T> {

    private final String modid;
    private final Registry<T> registry;
    private final List<Entry<?>> entries = new ArrayList<>();

    /**
     * @param registry the vanilla registry to defer into
     * @param modid    the owning mod id
     */
    protected DeferredRegister(Registry<T> registry, String modid) {
        this.registry = registry;
        this.modid = modid;
    }

    /**
     * Mirrors NeoForge's factory: builds a register bound to one vanilla
     * registry and a mod id.
     *
     * @param registry the vanilla registry
     * @param modid    the owning mod id
     * @param <T>      registry value type
     * @return the new deferred register
     */
    public static <T> DeferredRegister<T> create(Registry<T> registry, String modid) {
        return new DeferredRegister<>(registry, modid);
    }

    /**
     * Records an entry. The supplier is NOT invoked here; attachment is the
     * game-side machinery's job.
     *
     * @param name     registry name for the entry
     * @param supplier value supplier
     * @param <I>      concrete type
     * @return a lazy holder resolving to null until attached
     */
    public <I extends T> DeferredHolder register(String name, Supplier<? extends I> supplier) {
        DeferredHolder holder = new DeferredHolder();
        entries.add(new Entry<>(name, holder, supplier));
        return holder;
    }

    /**
     * No-op under Aprism: real NeoForge registers callbacks on the mod event
     * bus here; the shim keeps the call site linkable.
     *
     * @param bus the mod-scoped event bus
     */
    public void register(IEventBus bus) {
        // no-op: attachment handled by Aprism's registry machinery
    }

    //GitHub@NDBlockConnect | BlockConnect@StarsailsClover

    /**
     * @return recorded entry count (diagnostics)
     */
    public int getEntries() {
        return entries.size();
    }

    /**
     * @return the owning mod id
     */
    public String getModid() {
        return modid;
    }

    private record Entry<I>(String name, DeferredHolder holder, Supplier<? extends I> supplier) {
    }
}
