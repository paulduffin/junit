package org.junit.internal.runners.junit3;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.Description;

import java.util.List;

/**
 * A factory for creating components of a test suite.
 *
 * @param <T> the type of the 'test' component
 *
 * @since 4.13
 */
public interface TestHierarchyFactory<T> {

    /**
     * Create a representation of a {@link TestSuite}.
     *
     * @param name     the name of the suite.
     * @param children the transformed children of the test.
     * @return the representation of the {@link TestSuite}.
     */
    T createSuite(String name, List<T> children);

    /**
     * Create a representation of the {@link TestCase}.
     *
     * @param testCase    the test case.
     * @param description the description of the test case.
     * @return the representation of the {@link TestCase}.
     */
    T createTestCase(TestCase testCase, Description description);

    /**
     * Create a representation of a custom {@link Test}.
     *
     * @param test        the test.
     * @param description the description of the test case.
     * @return the representation of the {@link Test}.
     */
    T createCustomTest(Test test, Description description);
}
