package org.junit.internal.runners.junit3;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.internal.runners.InitializationErrorStyle;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.DescribableStatement;
import org.junit.runner.Description;
import org.junit.runner.ParentStatementRunner;
import org.junit.runner.Runner;
import org.junit.runner.StatementRunner;
import org.junit.runner.Statements;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerParams;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Constructs a {@link ParentStatementRunner} from a {@link TestCase} derived class.
 *
 * <p>This does a similar job to the {@link JUnit38ClassRunner} when it is constructed with a
 * {@link Class Class<? extends TestCase>} but takes a very different approach.
 *
 * <p>The JU38CR takes a {@link Test}, runs it using the standard JUnit3 infrastructure and
 * adapts the JUnit3 events ({@link TestResult}) to the JUnit4 event model. If it is given a Class
 * then it will first turn it into a {@link TestSuite} which is a {@link Test} and then process it
 * as normal.
 *
 * <p>When this is given a Class it converts it directly into a {@link ParentStatementRunner} where
 * each child {@link DescribableStatement} simply runs the {@link TestCase#runBare()} method. That
 * may cause a slight difference in behavior if the class overrides
 * {@link TestCase#run(TestResult)} as that method is never called. It's the responsibility of the
 * caller to ensure that the supplied class is a {@link TestClassShape#STANDARD_TEST_CASE}.
 *
 * <p>The advantage of converting JUnit3 style tests into JUnit4 structures is that it makes it
 * easier to treat them all consistently, apply test rules, etc.
 *
 * @since 4.13
 */
public class TestCaseRunnerFactory implements TestFactory<TestCase, Runner, DescribableStatement> {

    private final InitializationErrorStyle initializationErrorStyle;
    private final RunnerParams runnerParams;

    public TestCaseRunnerFactory(RunnerParams runnerParams) {
        this.runnerParams = runnerParams;
        this.initializationErrorStyle =
                runnerParams.get(Keys.JUNIT3_INITIALIZATION_ERROR_STYLE_KEY);
    }

    @Override
    public DescribableStatement createTest(Class<? extends TestCase> testClass, String methodName,
                                           Annotation[] annotations) {
        Description description =
                Description.createTestDescription(testClass, methodName, annotations);
        return JUnit3Statements.createAndRunTestCase(
                runnerParams, description, testClass, methodName);
    }

    @Override
    public DescribableStatement createTestForInitializationError(
            Class<? extends TestCase> testClass, String name, Annotation[] annotations,
            String message) {
        Description description = initializationErrorStyle.descriptionFor(
                testClass.getName(), name, annotations);
        return DescribableStatement.pair(description, new Fail(new AssertionFailedError(message)));
    }

    @Override
    public Runner createSuite(
            Class<? extends TestCase> testClass, List<DescribableStatement> tests) {

        try {
            return new ParentStatementRunner(testClass, tests);
        } catch (InitializationError e) {
            Description description = Description.createTestDescription(
                    testClass, "initializationError", testClass.getAnnotations());
            return new StatementRunner(description, Statements.throwing(e));
        }
    }
}
