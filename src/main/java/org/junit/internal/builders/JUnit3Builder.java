package org.junit.internal.builders;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.internal.runners.junit3.StandardTestCaseBuilder;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class JUnit3Builder extends RunnerBuilder {
    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        // Try and see if it is a standard TestCase first before falling back to using
        // JUnit38ClassRunner.
        Runner runner = new StandardTestCaseBuilder().runnerForClass(testClass);
        if (runner != null) {
            return runner;
        }

        if (isPre4Test(testClass)) {
            return new JUnit38ClassRunner(testClass);
        }
        return null;
    }

    boolean isPre4Test(Class<?> testClass) {
        return junit.framework.TestCase.class.isAssignableFrom(testClass);
    }
}
