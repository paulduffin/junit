package org.junit.internal.runners;

import static org.junit.Assert.assertEquals;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import org.junit.AssumptionViolatedException;
import org.junit.rules.InterceptOutputStreams;
import org.junit.rules.InterceptOutputStreams.Stream;
import org.junit.rules.TestRule;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.DescriptionComparators;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A {@link TestRule} that supports specifying expectations of the output of the test run, running
 * the test, intercepting the output and checking the expectations.
 *
 * @param <T> the type of test that will be run.
 */
public class RunOutputRule<T> implements TestRule {

    /**
     * Intercepts the output.
     */
    private final InterceptOutputStreams ios = new InterceptOutputStreams(Stream.OUT, Stream.ERR);

    private final RunTest<T> runTest;

    private final Filter filter;

    private final InitializationErrorStyle initializationErrorStyle;

    private final AppenderRule appenderRule;

    /**
     * The currently active {@link ClassExpectationBuilderImpl}
     */
    private ActiveBuilder activeBuilder;

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {

        private RunTest<T> runTest;

        private Filter filter = Filter.ALL;

        private InitializationErrorStyle initializationErrorStyle;

        private AppenderRule appenderRule = new AppenderRule() {
            @Override
            public Appender apply(Appender base, Description description, String target) {
                return base;
            }
        };

        private Builder() {
        }

        public Builder<T> runTest(RunTest<T> runTest) {
            this.runTest = runTest;
            return this;
        }

        public Builder<T> filter(Filter filter) {
            this.filter = this.filter.intersect(filter);
            return this;
        }

        public Builder<T> initializationErrorStyle(InitializationErrorStyle initializationErrorStyle) {
            this.initializationErrorStyle = initializationErrorStyle;
            return this;
        }

        public Builder<T> appenderRule(AppenderRule appenderRule) {
            this.appenderRule = appenderRule;
            return this;
        }

        public RunOutputRule<T> build() {
            return new RunOutputRule<T>(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "runTest=" + runTest +
                    ", filter=" + filter +
                    ", initializationErrorStyle=" + initializationErrorStyle +
                    '}';
        }
    }

    public interface Appender {
        void append(StringBuilder builder);
    }

    public interface AppenderRule {
        Appender apply(Appender base, Description description, String target);
    }

    private RunOutputRule(Builder<T> builder) {
        this.runTest = builder.runTest;
        this.filter = builder.filter;
        this.initializationErrorStyle = builder.initializationErrorStyle;
        this.appenderRule = builder.appenderRule;
    }

    /**
     * Get a JUnit3 listener that prints the events to System.out.
     */
    public static TestListener getPrintingTestListener() {
        return new PrintingTestListener();
    }

    /**
     * Get a comparator that will compare the {@link Description} of {@link Test} objects.
     */
    public static Comparator<Test> getTestComparator(final Comparator<Description> comparator) {
        return new Comparator<Test>() {
            @Override
            public int compare(Test o1, Test o2) {
                return comparator.compare(getDescription(o1), getDescription(o2));
            }
        };
    }

    /**
     * Get a JUnit4 listener that prints the events to System.out.
     */
    public static PrintingRunListener getPrintingRunListener() {
        return new PrintingRunListener();
    }

    @Override
    public Statement apply(Statement base, final Description description) {
        final Statement statement = ios.apply(base, description);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (filter.shouldRun(description)) {
                    statement.evaluate();
                } else {
                    throw new AssumptionViolatedException("Does not match filter: " + filter);
                }

                // If the test passed check to make sure that it didn't forget to check the
                // expectations.
                if (activeBuilder != null) {
                    throw new IllegalStateException
                            ("Expectations not checked, did you call check(...)");
                }
            }
        };
    }

    public SuiteExpectationBuilder<T> forSuite(SuiteExpectations expectations) {
        SuiteExpectationBuilder<T> builder = new SuiteExpectationBuilder<T>(this, expectations.expectations);
        activeBuilder = builder;
        return builder;
    }

    public static class SuiteExpectations {

        private final List<SuiteExpectation> expectations;

        public SuiteExpectations() {
            this.expectations = new ArrayList<SuiteExpectation>();
        }

        public void forSuite(SuiteExpectations expectations) {
            this.expectations.addAll(expectations.expectations);
        }

        public SuiteClassExpectationsBuilder forClass(Class<?> testClass) {
            SuiteClassExpectationsBuilder builder = new SuiteClassExpectationsBuilder(testClass);
            expectations.add(builder);
            return builder;
        }

        public void output(final String message) {
            expectations.add(new SuiteExpectation() {
                @Override
                public void initializeDescription(InitializationErrorStyle initializationErrorStyle) {
                }

                @Override
                public void appendExpectations(AppenderRule appenderRule, StringBuilder builder) {
                    builder.append(message).append("\n");
                }
            });
        }
    }

    /**
     * Runs a test.
     *
     * @param <T> the type of test to run.
     */
    public abstract static class RunTest<T> {
        /**
         * Implement to run the tests.
         *
         * <p>This is responsible for constructing the tests from the supplied object, if
         * necessary, running them and sending the events to System.out. The caller of this will
         * intercept the output and check it to make sure that it matches the expectations.
         *
         * <p>This must also sort the tests by their {@link Description} using the specified
         * comparator. This is to ensure consistency between the order of the expectations and the
         * order of the output without hard coding each one.
         *
         * @param test       the test..
         * @param comparator specifies the order in which the tests should be sorted.
         */
        public abstract void runTest(T test, Comparator<Description> comparator);
    }

    /**
     * Add expectation for a test class.
     */
    public StandaloneClassExpectationsBuilder<T> forClass(Class<?> testClass) {
        if (activeBuilder != null) {
            throw new IllegalStateException("Cannot set expectations for " + testClass
                    + " as the expectations for " + activeBuilder.describe()
                    + " have not yet been checked");
        }
        StandaloneClassExpectationsBuilder<T> builder =
                new StandaloneClassExpectationsBuilder<T>(this, testClass);
        activeBuilder = builder;
        return builder;
    }

    interface ActiveBuilder {
        String describe();
    }

    public static class SuiteExpectationBuilder<T> implements ActiveBuilder {

        private final List<SuiteExpectation> expectations;
        private final RunOutputRule<T> runOutputRule;

        private SuiteExpectationBuilder(
                RunOutputRule<T> runOutputRule, List<SuiteExpectation> expectations) {
            this.runOutputRule = runOutputRule;
            this.expectations = expectations;
        }

        public void check(T test) {
            for (SuiteExpectation expectation : expectations) {
                expectation.initializeDescription(runOutputRule.initializationErrorStyle);
            }

            StringBuilder builder = new StringBuilder();
            for (SuiteExpectation suiteExpectation : expectations) {
                suiteExpectation.appendExpectations(runOutputRule.appenderRule, builder);
            }

            runOutputRule.runTest.runTest(test, DescriptionComparators.DESCRIPTION_BY_CLASS_FIRST);

            runOutputRule.activeBuilder = null;

            String out = runOutputRule.ios.contents(Stream.OUT);
            String expectedOut = builder.toString();
            compareIgnoringStackTrace(expectedOut, out);
        }

        @Override
        public String describe() {
            return "<suite>";
        }
    }

    private interface ClassExpectationBuilder {
    }

    public static class StandaloneClassExpectationsBuilder<T>
            extends ClassExpectationBuilderImpl<StandaloneClassExpectationsBuilder<T>>
            implements ActiveBuilder {

        protected final RunOutputRule<T> runOutputRule;

        private StandaloneClassExpectationsBuilder(RunOutputRule<T> runOutputRule, Class<?> testClass) {
            super(testClass);
            this.runOutputRule = runOutputRule;
        }

        public TestExpectationBuilder<StandaloneClassExpectationsBuilder<T>> test(String name) {
            return testImpl(name, this);
        }

        public void check(T test) {
            checkImpl(test);
        }

        /**
         * Run the supplied test and check to make sure that the output is as expected.
         *
         * @param test the test to run.
         */
        protected void checkImpl(T test) {

            // Before sorting make sure that there's a description to sort by.
            initializeDescription(runOutputRule.initializationErrorStyle);

            Collections.sort(testExpectations, DescriptionComparators.DESCRIBABLE_BY_CLASS_FIRST);

            StringBuilder builder = new StringBuilder();
            appendExpectations(runOutputRule.appenderRule, builder);

            runOutputRule.runTest.runTest(test, DescriptionComparators.DESCRIPTION_BY_CLASS_FIRST);

            runOutputRule.activeBuilder = null;

            String out = runOutputRule.ios.contents(Stream.OUT);
            String expectedOut = builder.toString();
            compareIgnoringStackTrace(expectedOut, out);
        }

        @Override
        public String describe() {
            return testClass.toString();
        }
    }

    public static class SuiteClassExpectationsBuilder
            extends ClassExpectationBuilderImpl<SuiteClassExpectationsBuilder> {

        private SuiteClassExpectationsBuilder(Class<?> testClass) {
            super(testClass);
        }

        public TestExpectationBuilder<SuiteClassExpectationsBuilder> test(String name) {
            return testImpl(name, this);
        }
    }

    private interface SuiteExpectation {
        void initializeDescription(InitializationErrorStyle initializationErrorStyle);

        void appendExpectations(AppenderRule appenderRule, StringBuilder builder);
    }

    /**
     * Builder for expectations for a class.
     */
    private static class ClassExpectationBuilderImpl<CEB extends ClassExpectationBuilder>
            implements ClassExpectationBuilder, SuiteExpectation {

        protected final List<TestExpectation> testExpectations = new ArrayList<TestExpectation>();
        protected final Class<?> testClass;
        private String methodName;

        private ClassExpectationBuilderImpl(Class<?> testClass) {
            this.testClass = testClass;
        }

        /**
         * Begin the expectations for a specific test.
         *
         * @param name the name of the test, may be null if an initialization error.
         */
        TestExpectationBuilder<CEB> testImpl(String name, CEB ceb) {
            this.methodName = name;
            return new TestExpectationBuilder<CEB>(ceb, testExpectations, testClass, methodName);
        }

        @Override
        public void initializeDescription(InitializationErrorStyle initializationErrorStyle) {
            for (TestExpectation testExpectation : testExpectations) {
                testExpectation.initializeDescription(initializationErrorStyle);
            }
        }

        public void appendExpectations(AppenderRule appenderRule, StringBuilder builder) {
            for (TestExpectation testExpectation : testExpectations) {
                testExpectation.appendExpectedOutput(appenderRule, builder);
            }
        }
    }

    private static void compareIgnoringStackTrace(String expected, String actual) {
        String filteredExpected = removeStackTrace(expected);
        String filteredActual = removeStackTrace(actual);

        if (!filteredExpected.equals(filteredActual)) {
            assertEquals("Stack trace does not affect equality", expected, actual);
        }
    }

    private static String removeStackTrace(String actual) {
        return actual.replaceAll("\\t(at[^\\n]+|\\.\\.\\. [0-9]+ more)\\n", "");
    }

    /**
     * Expectation for a single test method.
     */
    public static class TestExpectationBuilder<C extends ClassExpectationBuilder> {

        private final String methodName;
        private final C classExpectationBuilder;
        private final Class<?> testClass;
        private final List<TestExpectation> testExpectations;
        private String output;

        private TestExpectationBuilder(
                C classExpectationBuilder, List<TestExpectation> testExpectations,
                Class<?> testClass, String methodName) {
            this.methodName = methodName;
            this.classExpectationBuilder = classExpectationBuilder;
            this.testClass = testClass;
            this.testExpectations = testExpectations;
        }

        private C addTestResult(String errorMessage, ResultType resultType) {
            testExpectations.add(new TestExpectation(testClass, methodName, output, errorMessage, resultType));
            return classExpectationBuilder;
        }

        public TestExpectationBuilder<C> output(String output) {
            this.output = output;
            return this;
        }

        /**
         * The test is expected to pass.
         */
        public C passed() {
            return addTestResult(null, ResultType.PASS);
        }

        /**
         * The test is expected to fail with an initialization failure.
         *
         * <p>This is distinct from {@link #failure(String)} because the way that the
         * {@link Description} is generated differs based on how the test is run and often differs
         * from how it is generated for normal failures with a test.
         */
        public C initializationFailure(String message) {
            return addTestResult(message, ResultType.INITIALIZATION_FAILURE);
        }

        /**
         * The test is expected to fail with an initialization error.
         *
         * <p>This is distinct from {@link #error(String)} because the way that the
         * {@link Description} is generated differs based on how the test is run and often differs
         * from how it is generated for normal failures with a test.
         */
        public C initializationError(String message) {
            return addTestResult(message, ResultType.INITIALIZATION_ERROR);
        }

        /**
         * The test is expected to fail during execution, i.e. call {@code fail()} or throw an
         * {@link AssertionFailedError} directly.
         */
        public C failure(String message) {
            return addTestResult(message, ResultType.FAILURE);
        }

        /**
         * The test is expected throw an unexpected error, i.e. not a {@link AssertionFailedError}.
         *
         * <p>This is distinct from {@link #failure(String)} because the JUnit3 event model
         * differentiates between them.
         */
        public C error(String message) {
            return addTestResult(message, ResultType.ERROR);
        }
    }

    enum ResultType {
        PASS(false, false),
        ERROR(false, false),
        FAILURE(true, false),
        INITIALIZATION_FAILURE(true, true),
        INITIALIZATION_ERROR(false, true);

        private final boolean failure;
        private final boolean initialization;

        ResultType(boolean failure, boolean initialization) {
            this.failure = failure;
            this.initialization = initialization;
        }

        public boolean isFailure() {
            return failure;
        }

        public boolean isInitialization() {
            return initialization;
        }
    }

    /**
     * Builder of expectations for a leaf test, i.e. one that actually tests something directly.
     */
    private static class TestExpectation implements Describable {

        private final Class<?> testClass;
        private final String methodName;
        private final String output;
        private Description description;
        private final String errorMessage;
        private final ResultType resultType;

        private TestExpectation(Class<?> testClass, String methodName, String output, String errorMessage, ResultType resultType) {
            this.testClass = testClass;
            this.methodName = methodName;
            this.output = output;
            this.description = null;
            this.errorMessage = errorMessage;
            this.resultType = resultType;
        }

        private void initializeDescription(InitializationErrorStyle initializationErrorStyle) {
            if (resultType.isInitialization()) {
                description = initializationErrorStyle.descriptionFor(
                        testClass.getName(), methodName, new Annotation[0]);
            } else {
                description = Description.createTestDescription(testClass, methodName);
            }
        }

        @Override
        public Description getDescription() {
            return description;
        }

        public void appendExpectedOutput(AppenderRule appenderRule, StringBuilder builder) {
            builder.append("start - ").append(description).append("\n");
            String target = testClass.getSimpleName();

            // The test output.
            Appender appender = new Appender() {
                @Override
                public void append(StringBuilder builder) {
                    if (output != null) {
                        builder.append(output).append("\n");
                    }
                }
            };
            if (!resultType.isInitialization()) {
                // Apply any global rules to the test output unless it is an initialization error.
                appender = appenderRule.apply(appender, description, target);
            }
            appender.append(builder);

            if (resultType != ResultType.PASS) {
                appender = new Appender() {
                    @Override
                    public void append(StringBuilder builder) {
                        if (resultType.isFailure()) {
                            builder.append("failure - ");
                        }
                        if (errorMessage != null) {
                            builder.append(errorMessage).append("\n");
                        }
                    }
                };
                appender.append(builder);
            }
            builder.append("end - ").append(description).append("\n");
        }
    }

    /**
     * Get a {@link Description} from a JUnit3 {@link Test} object.
     */
    private static Description getDescription(junit.framework.Test test) {
        String name;
        if (test instanceof TestCase) {
            name = ((TestCase) test).getName();
        } else {
            name = test.toString();
        }
        return Description.createTestDescription(test.getClass(), name);
    }

    private static class PrintingTestListener implements TestListener {
        @Override
        public void addError(junit.framework.Test test, Throwable e) {
            e.printStackTrace(System.out);
        }

        @Override
        public void addFailure(junit.framework.Test test, AssertionFailedError e) {
            System.out.println("failure - " + e.getMessage());
        }

        @Override
        public void endTest(junit.framework.Test test) {
            Description description = getDescription(test);
            System.out.println("end - " + description);
        }

        @Override
        public void startTest(junit.framework.Test test) {
            Description description = getDescription(test);
            System.out.println("start - " + description);
        }
    }

    private static class PrintingRunListener extends RunListener {
        @Override
        public void testFailure(Failure failure) throws Exception {
            Throwable exception = failure.getException();
            if (exception == null || exception instanceof AssertionFailedError) {
                System.out.println("failure - " + failure.getMessage());
            } else {
                exception.printStackTrace(System.out);
            }
        }

        @Override
        public void testFinished(Description description) throws Exception {
            System.out.println("end - " + description);
        }

        @Override
        public void testStarted(Description description) throws Exception {
            System.out.println("start - " + description);
        }
    }
}
