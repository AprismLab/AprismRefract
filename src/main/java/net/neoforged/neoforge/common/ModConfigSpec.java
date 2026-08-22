package net.neoforged.neoforge.common;

import java.util.function.Supplier;

import net.neoforged.fml.config.IConfigSpec;

/**
 * NeoForge API shim: config spec with builder and typed value holders.
 * The real spec validates TOML against declared entries; under Aprism the
 * Builder records nothing, returns inert typed {@link ConfigValue} holders
 * seeded with their declared defaults, and {@link #build()} returns an
 * inert spec. Values behave as {@link Supplier}s so mods that wrap config
 * entries in suppliers resolve and read defaults.
 *
 * @author BlockConnect@StarsailsClover
 */
public class ModConfigSpec implements IConfigSpec {

    /**
     * Inert config value holder backed by the declared default. Implements
     * {@link Supplier} so mod-side supplier wrapping resolves naturally.
     *
     * @param <T> value type
     */
    public static class ConfigValue<T> implements Supplier<T> {

        private final T defaultValue;

        /**
         * @param defaultValue the declared default value
         */
        public ConfigValue(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        /**
         * @return the current value (always the default under Aprism)
         */
        @Override
        public T get() {
            return defaultValue;
        }

        /**
         * Sets the in-memory value (not persisted).
         *
         * @param value the new value
         */
        public void set(T value) {
            // no persistence under Aprism
        }
    }

    /**
     * Boolean-typed config value.
     */
    public static final class BooleanValue extends ConfigValue<Boolean> {

        /**
         * @param defaultValue the declared default
         */
        public BooleanValue(Boolean defaultValue) {
            super(defaultValue);
        }
    }

    /**
     * Integer-typed config value.
     */
    public static final class IntValue extends ConfigValue<Integer> {

        /**
         * @param defaultValue the declared default
         */
        public IntValue(Integer defaultValue) {
            super(defaultValue);
        }
    }

    /**
     * Long-typed config value.
     */
    public static final class LongValue extends ConfigValue<Long> {

        /**
         * @param defaultValue the declared default
         */
        public LongValue(Long defaultValue) {
            super(defaultValue);
        }
    }

    /**
     * Double-typed config value.
     */
    public static final class DoubleValue extends ConfigValue<Double> {

        /**
         * @param defaultValue the declared default
         */
        public DoubleValue(Double defaultValue) {
            super(defaultValue);
        }
    }

    /**
     * Enum-typed config value.
     *
     * @param <V> enum type
     */
    public static final class EnumValue<V extends Enum<V>> extends ConfigValue<V> {

        /**
         * @param defaultValue the declared default
         */
        public EnumValue(V defaultValue) {
            super(defaultValue);
        }
    }

    /**
     * Fluent builder shim returning inert typed value holders.
     */
    public static final class Builder {

        /**
         * Defines a boolean entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @return the boolean value holder
         */
        public BooleanValue define(String path, boolean defaultValue) {
            return new BooleanValue(defaultValue);
        }

        /**
         * Defines an integer entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @return the int value holder
         */
        public IntValue define(String path, int defaultValue) {
            return new IntValue(defaultValue);
        }

        /**
         * Defines a long entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @return the long value holder
         */
        public LongValue define(String path, long defaultValue) {
            return new LongValue(defaultValue);
        }

        /**
         * Defines a double entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @return the double value holder
         */
        public DoubleValue define(String path, double defaultValue) {
            return new DoubleValue(defaultValue);
        }

        /**
         * Defines a string entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @return the string value holder
         */
        public ConfigValue<String> define(String path, String defaultValue) {
            return new ConfigValue<>(defaultValue);
        }

        /**
         * Defines a list entry.
         *
         * @param path         entry path
         * @param defaultValue default list
         * @param <T>          element type
         * @return the list value holder
         */
        public <T> ConfigValue<java.util.List<T>> defineList(String path,
                java.util.List<T> defaultValue) {
            return new ConfigValue<>(defaultValue);
        }

        /**
         * Defines a generic entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @param <T>          value type
         * @return the value holder
         */
        public <T> ConfigValue<T> define(String path, T defaultValue) {
            return new ConfigValue<>(defaultValue);
        }

        /**
         * Defines an enum entry.
         *
         * @param path         entry path
         * @param defaultValue default value
         * @param clazz        enum class
         * @param <V>          enum type
         * @return the enum value holder
         */
        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue,
                Class<V> clazz) {
            return new EnumValue<>(defaultValue);
        }

        /**
         * Opens a comment block (ignored).
         *
         * @param comment comment text
         * @return this builder
         */
        public Builder comment(String comment) {
            return this;
        }

        /**
         * Enters a sub-section (path prefix; ignored).
         *
         * @param key section key
         * @return this builder
         */
        public Builder push(String key) {
            return this;
        }

        /**
         * Exits the current sub-section (ignored).
         *
         * @return this builder
         */
        public Builder pop() {
            return this;
        }

        /**
         * Builds an inert spec.
         *
         * @return the built (inert) spec
         */
        public ModConfigSpec build() {
            return new ModConfigSpec();
        }
    }
}
