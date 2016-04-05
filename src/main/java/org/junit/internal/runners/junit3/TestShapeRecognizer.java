package org.junit.internal.runners.junit3;

import static org.junit.internal.runners.junit3.TestClassShape.CUSTOM_TEST;
import static org.junit.internal.runners.junit3.TestClassShape.CUSTOM_TEST_CASE;
import static org.junit.internal.runners.junit3.TestClassShape.CUSTOM_TEST_SUITE;
import static org.junit.internal.runners.junit3.TestClassShape.NOT_TEST;
import static org.junit.internal.runners.junit3.TestClassShape.STANDARD_TEST_CASE;
import static org.junit.internal.runners.junit3.TestClassShape.STANDARD_TEST_SUITE;
import static org.junit.internal.runners.junit3.TestClassShape.SUITE_METHOD;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import java.lang.reflect.Method;

/**
 * Examines a test {@code Class<?>} to see what 'shape' test it is.
 */
public class TestShapeRecognizer {

    private static final TestShapeRecognizer STANDARD = new TestShapeRecognizer();

    /**
     * Get a standard recognizer, i.e. one that adheres to the descriptions of the
     * {@link TestClassShape} values.
     */
    public static TestShapeRecognizer standardRecognizer() {
        return STANDARD;
    }

    private TestShapeRecognizer() {
    }

    /**
     * Examines the 'shape' of the test class.
     * @param testClass the test class.
     * @return the 'shape' of the test class.
     */
    public TestClassShape examineTestShape(Class<?> testClass) {
        try {
            testClass.getMethod("suite");
            return SUITE_METHOD;
        } catch (NoSuchMethodException e) {
            // Does not have a suite() method.
        }

        if (!Test.class.isAssignableFrom(testClass)) {
            return NOT_TEST;
        }

        Class<?> declaringClass;
        try {
            Method method = testClass.getMethod("run", TestResult.class);
            declaringClass = method.getDeclaringClass();
        } catch (NoSuchMethodException e) {
            // This could only happen if the Test interface is changed.
            throw new IllegalStateException(
                    "Test interface has changed, no run(TestResult) method", e);
        }

        if (declaringClass == TestCase.class) {
            return STANDARD_TEST_CASE;
        } else if (declaringClass == TestSuite.class) {
            try {
                Method method = testClass.getMethod("runTest", Test.class, TestResult.class);
                declaringClass = method.getDeclaringClass();
            } catch (NoSuchMethodException e) {
                // This could only happen if the TestSuite class is changed.
                throw new IllegalStateException(
                        "TestSuite class has changed, no runTest(Test, TestResult) method", e);
            }

            if (declaringClass == TestSuite.class) {
                return STANDARD_TEST_SUITE;
            }
        }

        if (TestCase.class.isAssignableFrom(testClass)) {
            return CUSTOM_TEST_CASE;
        } else if (TestSuite.class.isAssignableFrom(testClass)) {
            return CUSTOM_TEST_SUITE;
        }

        return CUSTOM_TEST;
    }
}
