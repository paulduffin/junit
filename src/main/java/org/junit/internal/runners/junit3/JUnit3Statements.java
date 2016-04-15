package org.junit.internal.runners.junit3;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.DescribableStatement;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerParams;
import org.junit.runners.model.Statement;

/**
 * Factory methods for {@link Statement} useful for integration with JUnit3 classes.
 */
public class JUnit3Statements {

    private JUnit3Statements() {
    }

    public static Statement runTestCase(TestCase test) {
        return new RunTestCaseStatement(test);
    }

    public static DescribableStatement createAndRunTestCase(
            RunnerParams runnerParams, Description description, Class<? extends TestCase> testClass,
            String methodName) {
        return new CreateAndRunTestCaseStatement(runnerParams, description, testClass, methodName);
    }

    public static Statement deferApplyGlobalRules(
            RunnerParams runnerParams, Statement statement, Description description,
            TestCase test) {
        return new ApplyGlobalRulesStatement(runnerParams, statement, description, test);
    }

    /**
     * Runs a {@link TestCase}.
     *
     * <p>This does not need to implement {@link Filterable} or {@link Sortable} as there is only
     * a single item to be run.
     */
    private static class RunTestCaseStatement extends Statement {
        private TestCase testCase;

        public RunTestCaseStatement(TestCase testCase) {
            this.testCase = testCase;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                testCase.runBare();
            } finally {
                // Discard the TestCase instance.
                testCase = null;
            }
        }
    }

    /**
     * Creates a {@link TestCase} instance and runs a method in it.
     */
    private static class CreateAndRunTestCaseStatement extends DescribableStatement {
        private final RunnerParams runnerParams;
        private final Class<? extends TestCase> testClass;
        private final String name;

        public CreateAndRunTestCaseStatement(
                RunnerParams runnerParams, Description description,
                Class<? extends TestCase> testClass, String name) {
            super(description);
            this.runnerParams = runnerParams;
            this.testClass = testClass;
            this.name = name;
        }

        @Override
        public void evaluate() throws Throwable {
            // Validate the class just before running the test.
            TestScanner.validateTestClass(testClass);

            // The cast is safe because the method either creates an instance of the supplied
            // testClass or it creates a TestCase warning.
            TestCase test = (TestCase) TestSuite.createTest(testClass, name);

            Statement statement = runTestCase(test);
            statement = deferApplyGlobalRules(runnerParams, statement, this.getDescription(), test);
            statement.evaluate();
        }
    }

    private static class ApplyGlobalRulesStatement extends Statement {
        private final RunnerParams runnerParams;
        private final Statement base;
        private final Description description;
        private final Object test;

        public ApplyGlobalRulesStatement(
                RunnerParams runnerParams, Statement base, Description description, Object test) {
            this.runnerParams = runnerParams;
            this.base = base;
            this.description = description;
            this.test = test;
        }

        @Override
        public void evaluate() throws Throwable {
            Statement statement = runnerParams.get(Keys.TARGETED_TEST_RULE_KEY)
                    .apply(base, description, test);
            statement.evaluate();
        }
    }
}
