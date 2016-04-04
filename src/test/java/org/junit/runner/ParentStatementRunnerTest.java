package org.junit.runner;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.RunOutputRule;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;

/**
 * Tests for {@link StatementRunner}.
 */
@RunWith(JUnit4.class)
public class ParentStatementRunnerTest {

    @Rule
    public RunOutputRule<Runner> runOutputRule = RunOutputRule.<Runner>builder()
            .runTest(RunTests.runRunner())
            .build();

    @Test
    public void run() throws InitializationError {
        ParentStatementRunner runner = new ParentStatementRunner(getClass(),
                Arrays.asList(
                        new DescribableStatement(
                                Description.createTestDescription(getClass(), "test1")) {
                            @Override
                            public void evaluate() throws Throwable {
                                throw new Exception("Failed");
                            }
                        },
                        new DescribableStatement(
                                Description.createTestDescription(getClass(), "test2")) {
                            @Override
                            public void evaluate() throws Throwable {
                                System.out.println("test2");
                            }
                        }
                ));

        runOutputRule.forClass(getClass())
                .test("test1").error("java.lang.Exception: Failed")
                .test("test2").output("test2").passed()
                .check(runner);

        Description description = runner.getDescription();
        RunWith runWith = description.getAnnotation(RunWith.class);
        assertNotNull("RunWith annotation missing from Description", runWith);
    }

}
