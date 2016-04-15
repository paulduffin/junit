package org.junit.internal.builders;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.internal.runners.junit3.StandardTestCaseBuilder;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerParams;

public class JUnit3Builder extends RunnerBuilder {

    private final RunnerParams runnerParams;

    public JUnit3Builder() {
        this(RunnerParams.emptyParams());
    }

    /**
     * @since 4.13
     */
    public JUnit3Builder(RunnerParams runnerParams) {
        this.runnerParams = runnerParams;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        // Try and see if it is a standard TestCase first before falling back to using
        // JUnit38ClassRunner.
        Runner runner = new StandardTestCaseBuilder(runnerParams).runnerForClass(testClass);
        if (runner != null) {
            return runner;
        }

        if (isPre4Test(testClass)) {
            return new JUnit38ClassRunner(runnerParams, testClass);
        }
        return null;
    }

    boolean isPre4Test(Class<?> testClass) {
        return junit.framework.TestCase.class.isAssignableFrom(testClass);
    }
}
