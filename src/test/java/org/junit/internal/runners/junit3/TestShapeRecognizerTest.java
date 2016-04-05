package org.junit.internal.runners.junit3;

import junit.framework.TestSuite;
import junit.tests.framework.NoTestCaseClass;
import junit.tests.framework.OneTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.junit3.tests.CustomNoSuitableConstructorTest;
import org.junit.internal.runners.junit3.tests.CustomTest;
import org.junit.internal.runners.junit3.tests.TestCaseOverrideRun;
import org.junit.internal.runners.junit3.tests.TestSuiteOverrideRun;
import org.junit.internal.runners.junit3.tests.TestSuiteOverrideRunTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

/**
 * Tests for {@link TestShapeRecognizer}.
 */
@RunWith(Parameterized.class)
public class TestShapeRecognizerTest {

    private final Class<?> testClass;
    private final TestClassShape expectedShape;

    @Parameters(name = "{index}: {0} is a {1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        NoTestCaseClass.class,
                        TestClassShape.NOT_TEST,
                },
                {
                        CustomTest.class,
                        TestClassShape.CUSTOM_TEST,
                },
                {
                        CustomNoSuitableConstructorTest.class,
                        TestClassShape.CUSTOM_TEST,
                },
                {
                        OneTestCase.class,
                        TestClassShape.STANDARD_TEST_CASE,
                },
                {
                        TestCaseOverrideRun.class,
                        TestClassShape.CUSTOM_TEST_CASE,
                },
                {
                        TestSuiteOverrideRun.class,
                        TestClassShape.CUSTOM_TEST_SUITE,
                },
                {
                        TestSuiteOverrideRunTest.class,
                        TestClassShape.CUSTOM_TEST_SUITE,
                },
                {
                        TestSuite.class,
                        TestClassShape.STANDARD_TEST_SUITE,
                },
        });
    }

    public TestShapeRecognizerTest(Class<?> testClass, TestClassShape expectedShape) {
        this.testClass = testClass;
        this.expectedShape = expectedShape;
    }

    @Test
    public void getShape() {
        TestShapeRecognizer standardRecognizer = TestShapeRecognizer.standardRecognizer();
        Assert.assertEquals(expectedShape, standardRecognizer.examineTestShape(testClass));
    }
}
    