package org.junit.internal.runners.junit3;

import junit.framework.TestCase;
import org.junit.runner.DescribableStatement;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

/**
 * A {@link RunnerBuilder} that will scan a {@link TestClassShape#STANDARD_TEST_CASE} and construct
 * a {@link Runner}.
 *
 * @since 4.13
 */
public class StandardTestCaseBuilder extends RunnerBuilder {

    private final TestScanner<TestCase, Runner, DescribableStatement> scanner;

    public StandardTestCaseBuilder() {
        TestCaseRunnerFactory factory = new TestCaseRunnerFactory();
        scanner = new TestScanner<TestCase, Runner, DescribableStatement>(factory);
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        TestClassShape shape = TestShapeRecognizer.standardRecognizer().examineTestShape(testClass);
        if (shape == TestClassShape.STANDARD_TEST_CASE) {
            return scanner.createSuite(testClass.asSubclass(TestCase.class));
        }

        return null;
    }
}
