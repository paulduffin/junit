package org.junit.internal.runners.junit3;

import junit.framework.TestCase;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * A factory for creating components of a {@link TestCase} derived test suite.
 *
 * @param <C> the base class of test classes this can handle
 * @param <S> the type of the 'suite' component
 * @param <T> the type of the 'test' component
 *
 * @since 4.13
 */
public interface TestFactory<C, S, T> {

    /**
     * Create the 'test' for the method.
     */
    T createTest(Class<? extends C> testClass, String methodName, Annotation[] annotations);

    /**
     * Create a 'test' that when run will throw the supplied throwable.
     */
    T createTestForInitializationError(
            Class<? extends C> testClass, String name, Annotation[] annotations, String message);

    /**
     * Constructs a test suite from the given class and list of tests.
     */
    S createSuite(Class<? extends C> testClass, List<T> tests);
}
