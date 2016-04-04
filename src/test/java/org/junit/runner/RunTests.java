package org.junit.runner;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.runners.RunOutputRule;
import org.junit.internal.runners.RunOutputRule.RunTest;
import org.junit.runner.manipulation.Sorter;
import org.junit.runners.model.RunnerBuilder;

import java.util.Comparator;

/**
 * Common implementations of {@link RunTest}.
 */
public class RunTests {

    public static RunTest<Class<?>> runWithBuilder(RunnerBuilder runnerBuilder) {
        return new RunClassWithBuilder(runnerBuilder);
    }

    public static RunTest<Class<?>> runWithJUnitCore() {
        return new RunClassWithCore();
    }

    public static RunTest<Runner> runRunner() {
        return new RunRunner();
    }

    private static void runRunner(Runner runner, Comparator<Description> comparator) {
        new Sorter(comparator).apply(runner);

        JUnitCore core = new JUnitCore();
        core.addListener(RunOutputRule.getPrintingRunListener());
        core.run(runner);
    }

    private static class RunClassWithBuilder extends RunTest<Class<?>> {

        private final RunnerBuilder runnerBuilder;

        public RunClassWithBuilder(RunnerBuilder runnerBuilder) {
            this.runnerBuilder = runnerBuilder;
        }

        @Override
        public void runTest(Class<?> testClass, Comparator<Description> comparator) {
            Runner runner = runnerBuilder.safeRunnerForClass(testClass);
            if (runner == null) {
                throw new IllegalStateException("No runner for " + testClass);
            }
            runRunner(runner, comparator);
        }

        @Override
        public String toString() {
            return runnerBuilder.getClass().getName();
        }
    }

    private static class RunRunner extends RunTest<Runner> {
        @Override
        public void runTest(Runner runner, Comparator<Description> comparator) {
            runRunner(runner, comparator);
        }
    }

    /**
     * Run a test specified as a {@link Class} using a {@link RunnerBuilder}.
     */
    private static class RunClassWithCore extends RunTest<Class<?>> {

        @Override
        public void runTest(Class<?> test, Comparator<Description> comparator) {
            JUnitCore core = new JUnitCore();
            core.addListener(RunOutputRule.getPrintingRunListener());

            Request classes = Request.classes(Computer.serial(), test);
            Sorter sorter = new Sorter(comparator);
            Runner runner = classes.getRunner();
            sorter.apply(runner);

            core.run(runner);
        }

        @Override
        public String toString() {
            return AllDefaultPossibilitiesBuilder.class.getName();
        }
    }
}
