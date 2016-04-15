package org.junit.internal.builders;

import org.junit.internal.runners.SuiteMethod;
import org.junit.internal.runners.junit3.TestHierarchyRunnerFactory;
import org.junit.internal.runners.junit3.TestHierarchyTransformer;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerParams;

public class SuiteMethodBuilder extends RunnerBuilder {
    private final RunnerParams runnerParams;

    public SuiteMethodBuilder() {
        this(RunnerParams.emptyParams());
    }

    /**
     * @since 4.13
     */
    public SuiteMethodBuilder(RunnerParams runnerParams) {
        this.runnerParams = runnerParams;
    }

    @Override
    public Runner runnerForClass(Class<?> each) throws Throwable {
        if (hasSuiteMethod(each)) {
            TestHierarchyTransformer<Runner> transformer =
                    new TestHierarchyTransformer<Runner>(new TestHierarchyRunnerFactory(runnerParams));
            return transformer.transform(each, SuiteMethod.testFromSuiteMethod(each));
        }
        return null;
    }

    public boolean hasSuiteMethod(Class<?> testClass) {
        try {
            testClass.getMethod("suite");
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }
}