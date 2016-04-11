package org.junit.runners.model;

import org.junit.runner.Runner;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An immutable type safe set of parameters that can be passed through to {@link RunnerBuilder} and
 * {@link Runner} classes.
 *
 * <p>This provides support for code that runs tests to pass through environment specific
 * information to the test runners and indirectly to the test instances.
 *
 * <p>Initialize a {@link RunnerParams} like this. Keys for values that are not a generic type can
 * be created directly from the class, keys for generic types require the creation of a subclass
 * of {@link TypeLiteral}.
 * <pre>
 * {@code
 * TypeLiteral<List<String>> listOfStrings = new TypeLiteral<List<String>>() {};
 *    Key<List<String>> namesKey = Key.of(listOfStrings, "org.acme.names");
 *    Key<Boolean> optionKey = Key.of(Boolean.class, "org.acme.option");
 *    RunnerParams params = RunnerParams.builder()
 *        .put(namesKey, Arrays.asList("fred", "wilma"))
 *        .put(optionKey, true)
 *        .build();
 * }
 * </pre>
 *
 * <p>The data can be retrieved like this:
 * <pre>
 * {@code
 * TypeLiteral<List<String>> listOfStrings = new TypeLiteral<List<String>>() {};
 *    Key<List<String>> namesKey = Key.of(listOfStrings, "org.acme.names");
 *    Key<Boolean> optionKey = Key.of(Boolean.class, "org.acme.option");
 *    boolean option = params.get(optionKey, false);
 *    List<String> names = params.get(namesKey);
 * }
 * </pre>
 *
 * @see TypeLiteral
 * @see Key
 *
 * @since 4.13
 */
public class RunnerParams {

    private static final RunnerParams EMPTY_PARAMS = new RunnerParams();

    /**
     * Get an empty set of {@link RunnerParams}.
     */
    public static RunnerParams emptyParams() {
        return EMPTY_PARAMS;
    }

    /**
     * Get a {@link Builder} for {@link RunnerParams}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A type safe key into {@link RunnerParams}
     *
     * <p>Keys are equal if and only if their type (the value of the {@link TypeLiteral} type
     * parameter) and name are equal.
     *
     * @param <T> the type of the value that can be stored and retrieved by the key.
     */
    public static class Key<T> {

        private final String name;
        private final Type type;
        private final T defaultValue;

        /**
         * Get a key for storing and retrieving values of the specified class and name.
         */
        public static <V> Key<V> of(Class<V> clazz, String name) {
            return new Key<V>(name, clazz, null);
        }

        /**
         * Get a key for storing and retrieving values of the specified class and name.
         */
        public static <V> Key<V> of(Class<V> clazz, String name, V defaultValue) {
            return new Key<V>(name, clazz, defaultValue);
        }

        /**
         * Get a key for storing and retrieving values of the specified type and name.
         */
        public static <V> Key<V> of(TypeLiteral<V> typeLiteral, String name) {
            return new Key<V>(name, typeLiteral.getType(), null);
        }

        /**
         * Get a key for storing and retrieving values of the specified type and name.
         */
        public static <V> Key<V> of(TypeLiteral<V> typeLiteral, String name, V defaultValue) {
            return new Key<V>(name, typeLiteral.getType(), defaultValue);
        }

        private Key(String name, Type type, T defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key<?> key = (Key<?>) o;

            return name.equals(key.name) && type.equals(key.type);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }

        @SuppressWarnings("unchecked")
        private T cast(Object o) {
            return (T) o;
        }

        @Override
        public String toString() {
            return "Key{name='" + name + '\'' + ", type=" + type + '}';
        }
    }

    /**
     * A builder of {@link RunnerParams}.
     */
    public static class Builder {

        private final Map<Key<?>, Object> data;

        private Builder() {
            data = new LinkedHashMap<Key<?>, Object>();
        }

        /**
         * Put a value with the specified key.
         *
         * @param key   the key
         * @param value the value
         * @param <V>   the type of the value.
         * @return this to allow chaining.
         */
        public <V> Builder put(Key<V> key, V value) {
            data.put(key, value);
            return this;
        }

        /**
         * Build the parameters.
         */
        public RunnerParams build() {
            if (data.isEmpty()) {
                return emptyParams();
            } else {
                return new RunnerParams(data);
            }
        }
    }

    private final Map<Key<?>, Object> data;

    private RunnerParams(Map<Key<?>, Object> data) {
        this.data = new LinkedHashMap<Key<?>, Object>(data);
    }

    /**
     * Constructor for {@link #emptyParams()}.
     */
    private RunnerParams() {
        data = Collections.emptyMap();
    }

    /**
     * Get the value for the specified key, will return null if value does not exist.
     *
     * @param key the key
     * @param <V> the type of the value.
     * @return the value, or null.
     */
    public <V> V get(Key<V> key) {
        return get(key, key.getDefaultValue());
    }

    /**
     * Get the value for the specified key, will return the default value if the value does not
     * exist.
     *
     * @param key          the key
     * @param defaultValue the default value, overrides any default value in the key.
     * @param <V>          the type of the value.
     * @return the value, or null.
     */
    public <V, D extends V> V get(Key<V> key, D defaultValue) {
        if (data.containsKey(key)) {
            Object value = data.get(key);
            return key.cast(value);
        } else {
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        return "RunnerParams" + data;
    }
}
