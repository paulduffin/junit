package org.junit.runner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.RunOutputRule;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link StatementRunner}.
 */
@RunWith(JUnit4.class)
public class StatementRunnerTest {

    @Rule
    public RunOutputRule<Runner> runOutputRule = RunOutputRule.<Runner>builder()
            .runTest(RunTests.runRunner())
            .build();

    @Test
    public void run() {
        String testMethod = "testMethod";
        Description description =
                Description.createTestDescription(getClass(), testMethod);
        StatementRunner runner = new StatementRunner(new DescribableStatement(description) {
            @Override
            public void evaluate() throws Throwable {
                throw new Exception("Failed");
            }
        });

        runOutputRule.forClass(getClass())
                .test(testMethod).error("java.lang.Exception: Failed")
                .check(runner);
    }
}
    