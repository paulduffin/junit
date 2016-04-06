package org.junit.internal.runners.junit3;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.StatementRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Transforms a hierarchy of {@link Test} objects into a hierarchy of {@link Runner} objects.
 *
 * <p>This provides some of the functionality provided by {@link JUnit38ClassRunner}; see
 * {@link TestCaseRunnerFactory} for an overview of that class.
 *
 * <p>Just like {@link TestCaseRunnerFactory} this converts the JUnit3 classes into JUnit4
 * structures and then runs them rather than run them as JUnit3 and adapt the events back into
 * JUnit4 event model. This traverses the hierarchy of {@link Test} and converts them directly into
 * {@link Runner} classes.
 *
 * <ol>
 * <li>A {@link TestClassShape#STANDARD_TEST_SUITE standard} {@link TestSuite} is converted into a
 * {@link Suite}.</li>
 * <li>A {@link TestClassShape#STANDARD_TEST_CASE standard} {@link TestCase} is converted into a
 * {@link StatementRunner}.</li>
 * <li>The remaining {@link Test} classes are supported. They could be but they would require
 * something like {@link JUnit38ClassRunner}.</li>
 * </ol>
 *
 * @since 4.13
 */
public class TestHierarchyRunnerFactory implements TestHierarchyFactory<Runner> {

    @Override
    public Runner createSuite(String name, List<Runner> children) {
        try {
            return new Suite(name, children);
        } catch (InitializationError e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Runner createTestCase(final TestCase testCase, Description description) {
        return new StatementRunner(description, JUnit3Statements.runTestCase(testCase));
    }

    @Override
    public Runner createCustomTest(final Test test, final Description description) {
        return new JUnit38ClassRunner(test);
    }
}
