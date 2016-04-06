package org.junit.internal.runners.junit3;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Constructs a {@code List<Test>} from a {@link Test} class.
 *
 * <p>Primarily used by {@link TestSuite#TestSuite(Class)} to populate it with a list of tests.
 *
 * @since 4.13
 */
public class TestSuiteFactory implements TestFactory<Object, List<Test>, Test> {

    @Override
    public Test createTest(Class<?> testClass, String methodName,
                           Annotation[] annotations) {
        return TestSuite.createTest(testClass, methodName);
    }

    @Override
    public Test createTestForInitializationError(Class<?> testClass, String name,
                                                 Annotation[] annotations, final String message) {
        return TestSuite.warning(message);
    }

    @Override
    public List<Test> createSuite(Class<?> testClass, List<Test> tests) {
        return tests;
    }
}
