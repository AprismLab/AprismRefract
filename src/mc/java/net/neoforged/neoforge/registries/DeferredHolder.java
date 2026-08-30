package net.neoforged.neoforge.registries;

//GitHub@NDBlockConnect | BlockConnect@StarsailsClover
import java.util.function.Supplier;

/**
 * NeoForge API shim: lazy registry-object holder returned by
 * {@link DeferredRegister#register}. Implements Supplier so mod-side
 * supplier usage links; resolves to null until the game-side registry
 * binder publishes values.
 *
 * @param <T> held registry value type
 * @author BlockConnect@StarsailsClover
 */
public class DeferredHolder<T> implements Supplier<T> {

    private volatile T value;

    @Override
    public T get() {
        return value;
    }

    /**
     * Used by the game-side binder to publish the resolved value.
     *
     * @param v resolved registry value
     */
    public void accept(T v) {
        this.value = v;
    }
}
