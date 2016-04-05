package org.junit.internal.runners.junit3;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * The 'shape' of a JUnit3 test class.
 *
 * @since 4.13
 */
public enum TestClassShape {
    /**
     * The test class is a {@link TestCase} and does not override
     * {@link TestCase#run(TestResult)}.
     */
    STANDARD_TEST_CASE,

    /**
     * The test class is a {@link TestSuite} and does not override either
     * {@link TestSuite#run(TestResult)} or {@link TestSuite#runTest(Test, TestResult)}..
     */
    STANDARD_TEST_SUITE,

    /**
     * The test class has a static suite() method.
     */
    SUITE_METHOD,

    /**
     * A non-standard {@link TestCase}.
     */
    CUSTOM_TEST_CASE,

    /**
     * A non-standard {@link TestSuite}.
     */
    CUSTOM_TEST_SUITE,

    /**
     * All other {@link Test} implementations.
     */
    CUSTOM_TEST,

    /**
     * Everything else.
     */
    NOT_TEST
}
