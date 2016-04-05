package org.junit.internal.runners.junit3;

import junit.framework.TestCase;
import junit.tests.framework.InheritedTestCase;
import junit.tests.framework.NoTestCaseClass;
import junit.tests.framework.NoTestCases;
import junit.tests.framework.NotPublicTestCase;
import junit.tests.framework.NotVoidTestCase;
import junit.tests.framework.OneTestCase;
import junit.tests.framework.OverrideTestCase;
import junit.tests.framework.ThreeTestCases;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.RunOutputRule;
import org.junit.internal.runners.RunOutputRule.SuiteExpectations;
import org.junit.internal.runners.junit3.tests.CustomNoSuitableConstructorTest;
import org.junit.internal.runners.junit3.tests.CustomTest;
import org.junit.internal.runners.junit3.tests.ErrorTestCase;
import org.junit.internal.runners.junit3.tests.FailureTestCase;
import org.junit.internal.runners.junit3.tests.InvalidTestMethod;
import org.junit.internal.runners.junit3.tests.SuiteMethod;
import org.junit.internal.runners.junit3.tests.SuiteMethodWithCustomTests;
import org.junit.internal.runners.junit3.tests.TestCaseOverrideRun;
import org.junit.internal.runners.junit3.tests.WithBrokenSuiteMethod;

/**
 * Tests for checking the processing of {@link TestCase} based classes.
 */
public abstract class AbstractJUnit3CompatibilityTest {

    /**
     * A rule to aid with the building of expectations, and checking result with the output from
     * the test.
     */
    @Rule
    public RunOutputRule<Class<?>> runOutputRule;

    public AbstractJUnit3CompatibilityTest(RunOutputRule.Builder<Class<?>> builder) {
        runOutputRule = builder.build();
    }

    @TestClassShapesUsed(TestClassShape.NOT_TEST)
    @Test
    public void notTestClass() {
        runOutputRule.forClass(NoTestCaseClass.class)
                .test(null)
                .initializationFailure("No tests found in " + NoTestCaseClass.class.getName())
                .check(NoTestCaseClass.class);
    }

    @TestClassShapesUsed(TestClassShape.CUSTOM_TEST)
    @Test
    public void customTestClass() {
        runOutputRule.forClass(CustomTest.class)
                .test("<test1>").output("test - test1").passed()
                .test("<test2>").failure("Failed")
                .test("<test3>").error("java.lang.Exception: Exception")
                .check(CustomTest.class);
    }

    @TestClassShapesUsed(TestClassShape.CUSTOM_TEST_CASE)
    @Test
    public void testCaseOverrideRun() {
        runOutputRule.forClass(TestCaseOverrideRun.class)
                .test("test1").output("Before\ntest1\nAfter").passed()
                .test("test2").output("Before\ntest2\nAfter").passed()
                .check(TestCaseOverrideRun.class);
    }

    @TestClassShapesUsed(TestClassShape.CUSTOM_TEST)
    @Test
    public void customNoSuitableConstructor() {
        runOutputRule.forClass(CustomNoSuitableConstructorTest.class)
                .test(null)
                .initializationFailure("Class " + CustomNoSuitableConstructorTest.class.getName()
                        + " has no public constructor TestCase(String name) or TestCase()")
                .check(CustomNoSuitableConstructorTest.class);
    }

    @Test
    public void oneTestCase() {
        runOutputRule.forClass(OneTestCase.class)
                .test("testCase").passed()
                .check(OneTestCase.class);
    }

    @Test
    public void inheritedTests() {
        runOutputRule.forClass(InheritedTestCase.class)
                .test("test2").passed()
                .test("testCase").passed()
                .check(InheritedTestCase.class);
    }

    @Test
    public void noTestCases() {
        String className = NoTestCases.class.getName();
        runOutputRule.forClass(NoTestCases.class)
                .test(null)
                .initializationFailure("No tests found in " + className)
                .check(NoTestCases.class);
    }

    @Test
    public void notPublicTestCase() {
        String className = NotPublicTestCase.class.getName();
        runOutputRule.forClass(NotPublicTestCase.class)
                .test("testNotPublic")
                .initializationFailure("Test method isn't public: testNotPublic(" + className + ")")
                .test("testPublic")
                .passed()
                .check(NotPublicTestCase.class);
    }

    @Test
    public void notVoidTestCase() {
        runOutputRule.forClass(NotVoidTestCase.class)
                .test("testVoid").passed()
                .check(NotVoidTestCase.class);
    }

    @Test
    public void errorTestCase() {
        runOutputRule.forClass(ErrorTestCase.class)
                .test("testError")
                .output("Before it breaks")
                .error("java.lang.IllegalStateException")
                .check(ErrorTestCase.class);
    }

    @Test
    public void failureTestCase() {
        runOutputRule.forClass(FailureTestCase.class)
                .test("testFail").failure("Failed")
                .check(FailureTestCase.class);
    }

    @Test
    public void oneTestCaseEclipseSeesSameStructureAs381() {
        runOutputRule.forClass(ThreeTestCases.class)
                .test("testCase").passed()
                .test("testCase2").passed()
                .test("testCase3thisTimeItsPersonal").passed()
                .check(ThreeTestCases.class);
    }

    @Test
    public void shadowedTests() {
        runOutputRule.forClass(OverrideTestCase.class)
                .test("testCase").passed()
                .check(OverrideTestCase.class);
    }

    @TestClassShapesUsed(TestClassShape.SUITE_METHOD)
    @Test
    public void suiteMethod() {
        runOutputRule.forSuite(new SuiteExpectations() {{
            forClass(OneTestCase.class)
                    .test("testCase").passed();
        }}).check(SuiteMethod.class);
    }

    @TestClassShapesUsed(TestClassShape.SUITE_METHOD)
    @Test
    public void suiteMethodWithCustomTests() {
        runOutputRule.forSuite(new SuiteExpectations() {{
            forSuite(new SuiteExpectations() {{
                output("Before Suite");
                forClass(ThreeTestCases.class)
                        .test("testCase").passed()
                        .test("testCase2").passed();
                output("After Suite");
            }});
            forClass(OneTestCase.class)
                    .test("testCase").passed();
            forClass(CustomTest.class)
                    .test("<test1>").output("test - test1").passed();
        }}).check(SuiteMethodWithCustomTests.class);
    }

    @TestClassShapesUsed(TestClassShape.SUITE_METHOD)
    @Test
    public void brokenSuiteMethod() {
        runOutputRule.forSuite(new SuiteExpectations() {{
            forClass(WithBrokenSuiteMethod.class)
                    .test("initializationError")
                    .initializationError("java.lang.IllegalStateException: Broken");
        }}).check(WithBrokenSuiteMethod.class);
    }

    @Test
    public void invalidTestMethod() {
        runOutputRule.forClass(InvalidTestMethod.class)
                .test("testInvalid")
                .initializationFailure("Test method isn't public: "
                        + "testInvalid(" + InvalidTestMethod.class.getName() + ")")
                .test("testValid")
                .passed()
                .check(InvalidTestMethod.class);
    }
}
