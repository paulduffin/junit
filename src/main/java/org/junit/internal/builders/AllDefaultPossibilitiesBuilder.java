package org.junit.internal.builders;

import java.util.Arrays;
import java.util.List;
import org.junit.runner.Runner;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerParams;

public class AllDefaultPossibilitiesBuilder extends RunnerBuilder {
    private final RunnerParams runnerParams;
    private final boolean canUseSuiteMethod;

    /**
     * @since 4.13
     */
    public AllDefaultPossibilitiesBuilder(RunnerParams runnerParams) {
        this.runnerParams = runnerParams;
        this.canUseSuiteMethod = runnerParams.get(Keys.USE_SUITE_METHOD);
    }

    /**
     * @deprecated Since 4.13. Use {@link #AllDefaultPossibilitiesBuilder(RunnerParams)} and
     * {@link Keys#USE_SUITE_METHOD}.
     */
    @Deprecated
    public AllDefaultPossibilitiesBuilder(boolean canUseSuiteMethod) {
        this(RunnerParams.builder().put(Keys.USE_SUITE_METHOD, canUseSuiteMethod).build());
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        List<RunnerBuilder> builders = Arrays.asList(
                ignoredBuilder(),
                annotatedBuilder(),
                suiteMethodBuilder(),
                junit3Builder(),
                junit4Builder());

        for (RunnerBuilder each : builders) {
            Runner runner = each.safeRunnerForClass(testClass);
            if (runner != null) {
                return runner;
            }
        }
        return null;
    }

    public RunnerParams getRunnerParams() {
        return runnerParams;
    }

    protected JUnit4Builder junit4Builder() {
        return new JUnit4Builder(getRunnerParams());
    }

    protected JUnit3Builder junit3Builder() {
        return new JUnit3Builder(getRunnerParams());
    }

    protected AnnotatedBuilder annotatedBuilder() {
        return new AnnotatedBuilder(getRunnerParams(), this);
    }

    protected IgnoredBuilder ignoredBuilder() {
        return new IgnoredBuilder();
    }

    protected RunnerBuilder suiteMethodBuilder() {
        if (canUseSuiteMethod) {
            return new SuiteMethodBuilder(getRunnerParams());
        }
        return new NullBuilder();
    }
}