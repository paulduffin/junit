package org.junit.internal.runners.junit3;

import static org.junit.internal.runners.InitializationErrorStyle.JUNIT3_WARNING;
import static org.junit.internal.runners.InitializationErrorStyle.JUNIT4_INITIALIZATION_ERROR;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.internal.builders.JUnit3Builder;
import org.junit.internal.builders.SuiteMethodBuilder;
import org.junit.internal.runners.ExcludeParameterizedMethodsByName;
import org.junit.internal.runners.LoggingAppenderRule;
import org.junit.internal.runners.LoggingTargetedTestRule;
import org.junit.internal.runners.RunOutputRule;
import org.junit.internal.runners.RunOutputRule.Builder;
import org.junit.internal.runners.RunOutputRule.RunTest;
import org.junit.runner.Description;
import org.junit.runner.RunTests;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * Run the tests in {@link AbstractJUnit3CompatibilityTest} in a number of different configurations
 * to test all the different ways that test suites can be constructed.
 *
 * <p>The test methods are all in the base class to keep them separate from the  infrastructure
 * needed for testing them.
 */
@RunWith(Parameterized.class)
public class JUnit3CompatibilityTest extends AbstractJUnit3CompatibilityTest {

    /**
     * Test using the {@link TestSuite#TestSuite(Class)} constructor.
     */
    private static RunTest<Class<?>> createTestSuiteRunTest() {
        return new RunTestList() {
            @Override
            public void runTest(Class<?> testClass, Comparator<Description> comparator) {
                TestSuite suite = new TestSuite(testClass);

                List<Test> tests = new ArrayList<Test>();
                int count = suite.testCount();
                for (int i = 0; i < count; ++i) {
                    tests.add(suite.testAt(i));
                }

                runTests(testClass, tests, comparator);
            }

            @Override
            public String toString() {
                return TestSuite.class.getName();
            }
        };
    }

    private static final Filter DOES_NOT_SUPPORT_CUSTOM_TEST_OR_NOT_TEST =
            createFilterBy(EnumSet.complementOf(
                    EnumSet.of(TestClassShape.NOT_TEST, TestClassShape.CUSTOM_TEST)))
                    .intersect(new ExcludeParameterizedMethodsByName("brokenSuiteMethod"));

    private static Filter createFilterBy(final EnumSet<TestClassShape> supportedShapes) {
        return new Filter() {
            @Override
            public boolean shouldRun(Description description) {
                TestClassShapesUsed annotation =
                        description.getAnnotation(TestClassShapesUsed.class);
                TestClassShape[] usedShapes;
                if (annotation != null) {
                    usedShapes = annotation.value();
                } else {
                    usedShapes = new TestClassShape[]{TestClassShape.STANDARD_TEST_CASE};
                }
                for (TestClassShape shape : usedShapes) {
                    if (!supportedShapes.contains(shape)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public String describe() {
                return String.valueOf(supportedShapes);
            }
        };
    }

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // The TestSuite(Class<?>) can handle any test apart from suite() methods.
                {
                        runJunit3Builder()
                                .runTest(createTestSuiteRunTest())
                                .filter(createFilterBy(EnumSet.complementOf(
                                        EnumSet.of(TestClassShape.SUITE_METHOD)))),
                },

                // The JUnit3Builder only handles TestCase derived tests.
                {
                        runJunit3Builder()
                                .runTest(RunTests.runWithBuilder(new JUnit3Builder()))
                                .filter(createFilterBy(EnumSet.of(
                                        TestClassShape.STANDARD_TEST_CASE,
                                        TestClassShape.CUSTOM_TEST_CASE))),
                },

                // The SuiteMethodBuilder can handle classes that have a suite() method.
                {
                        runJunit3Builder()
                                .runTest(RunTests.runWithBuilder(new SuiteMethodBuilder()))
                                .filter(createFilterBy(EnumSet.of(TestClassShape.SUITE_METHOD)))
                                .initializationErrorStyle(JUNIT4_INITIALIZATION_ERROR),
                },

                // The default RunnerBuilder used by JUnitCore handles classes that are not custom
                // tests and not tests at all. It can handle them it just reports different error
                // messages.
                {
                        runJunit3Builder()
                                .runTest(RunTests.runWithJUnitCore(RunnerParams.emptyParams()))
                                .filter(DOES_NOT_SUPPORT_CUSTOM_TEST_OR_NOT_TEST),
                },

                // Make sure that the default RunnerBuilder passes the RunnerParams through so
                // that the tests use JUnit4 style initialization errors.
                {
                        runJunit3Builder()
                                .runTest(RunTests.runWithJUnitCore(
                                        RunnerParams.builder()
                                                .put(Keys.JUNIT3_INITIALIZATION_ERROR_STYLE_KEY,
                                                        JUNIT4_INITIALIZATION_ERROR)
                                                .build()))
                                .filter(DOES_NOT_SUPPORT_CUSTOM_TEST_OR_NOT_TEST)
                                .initializationErrorStyle(JUNIT4_INITIALIZATION_ERROR),
                },

                // Apply global rule.
                {
                        runJunit3Builder()
                                .runTest(RunTests.runWithJUnitCore(
                                        RunnerParams.builder()
                                                .put(Keys.TARGETED_TEST_RULE_KEY,
                                                        new LoggingTargetedTestRule())
                                                .build()))
                                .filter(DOES_NOT_SUPPORT_CUSTOM_TEST_OR_NOT_TEST
                                        // Exclude these tests because they contain custom tests
                                        // that aren't supported with global rules.
                                        .intersect(new ExcludeParameterizedMethodsByName(
                                                "suiteMethodWithCustomTests",
                                                "testCaseOverrideRun")))
                                .appenderRule(new LoggingAppenderRule()),
                },
        });
    }

    public static Builder<Class<?>> runJunit3Builder() {
        return RunOutputRule.<Class<?>>builder().initializationErrorStyle(JUNIT3_WARNING);
    }

    public JUnit3CompatibilityTest(Builder<Class<?>> builder) {
        super(builder);
    }

    protected static abstract class RunTestList extends RunTest<Class<?>> {
        protected void runTests(Class<?> testClass, List<junit.framework.Test> tests,
                                Comparator<Description> comparator) {
            Collections.sort(tests, RunOutputRule.getTestComparator(comparator));
            TestSuite suite = new TestSuite(testClass.getName());
            for (junit.framework.Test test : tests) {
                suite.addTest(test);
            }

            runTest(suite);
        }

        protected void runTest(Test test) {
            TestResult result = new TestResult();
            result.addListener(RunOutputRule.getPrintingTestListener());
            test.run(result);
        }
    }
}
