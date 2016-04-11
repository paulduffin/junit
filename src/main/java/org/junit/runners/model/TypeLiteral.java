package org.junit.runners.model;

import org.junit.runners.model.RunnerParams.Key;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Provides mechanism for obtaining a {@link Type} for a parameterized class.
 *
 * <p>It's easy to obtain a {@link Type} for a non-parameterized class you just use
 * {@code <class name>.class} but due to type erasure it is harder to do that for a parameterized
 * class. This provides a simply mechanism for doing that taking advantage of the
 * {@link Class#getGenericSuperclass()} method which provides access to the parameterized type of
 * a class's super class. All you need to do is create a subclass of this and call
 * {@link #getType()}, e.g. the following will return a representation of {@code List<String>}.
 *
 * <p>Provided primarily for use with {@link Key}.
 *
 * <pre>
 * {@code new TypeLiteral<List<String>>() {}.getType()}
 * </pre>
 *
 * @see Key
 *
 * @since 4.13
 */
public class TypeLiteral<T> {

    private final Type type;

    private TypeLiteral(Class<T> clazz) {
        type = clazz;
    }

    protected TypeLiteral() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            type = parameterizedType.getActualTypeArguments()[0];
        } else if (getClass() == TypeLiteral.class) {
            // This can only happen when used in this package.
            throw new IllegalStateException(
                    "Must extend the class, e.g. new TypeLiteral<T>() {}");
        } else {
            throw new IllegalStateException(
                    "Must specify a type parameter, e.g. new TypeLiteral<T>() {}");
        }
    }

    public static <V> TypeLiteral<V> of(Class<V> clazz) {
        return new TypeLiteral<V>(clazz);
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeLiteral)) return false;

        TypeLiteral<?> that = (TypeLiteral<?>) o;

        return type.equals(that.type);

    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
