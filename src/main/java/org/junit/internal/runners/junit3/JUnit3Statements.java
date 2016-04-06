package org.junit.internal.runners.junit3;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.DescribableStatement;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
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
            Description description, Class<? extends TestCase> testClass, String methodName) {
        return new CreateAndRunTestCaseStatement(description, testClass, methodName);
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
        private final Class<? extends TestCase> testClass;
        private final String name;

        public CreateAndRunTestCaseStatement(
                Description description, Class<? extends TestCase> testClass, String name) {
            super(description);
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
            statement.evaluate();
        }
    }
}
