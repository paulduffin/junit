package org.junit.internal.builders;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerParams;

public class JUnit4Builder extends RunnerBuilder {

    private final RunnerParams runnerParams;

    public JUnit4Builder() {
        this(RunnerParams.emptyParams());
    }

    /**
     * @since 4.13
     */
    public JUnit4Builder(RunnerParams runnerParams) {
        this.runnerParams = runnerParams;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        return new BlockJUnit4ClassRunner(runnerParams, testClass);
    }
}