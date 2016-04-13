package org.junit.internal.runners.junit4;

import static org.junit.internal.runners.InitializationErrorStyle.JUNIT4_INITIALIZATION_ERROR;
import static org.junit.runners.model.InitializationValidation.CLASS_ONLY;

import org.junit.internal.runners.LoggingAppenderRule;
import org.junit.internal.runners.LoggingTargetedTestRule;
import org.junit.internal.runners.RunOutputRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerParams;

import java.util.Arrays;

/**
 */
@RunWith(Parameterized.class)
public class JUnit4CompatibilityTest extends AbstractJUnit4CompatibilityTest {

    public JUnit4CompatibilityTest(RunOutputRule.Builder<Class<?>> builder, RunnerParams runnerParams) {
        super(builder, runnerParams);
    }

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        RunOutputRule.<Class<?>>builder()
                                .initializationErrorStyle(JUNIT4_INITIALIZATION_ERROR),
                        RunnerParams.emptyParams(),
                },
                {
                        RunOutputRule.<Class<?>>builder()
                                .initializationErrorStyle(JUNIT4_INITIALIZATION_ERROR),
                        RunnerParams.builder()
                                .put(Keys.INITIALIZATION_VALIDATION_KEY, CLASS_ONLY)
                                .build(),
                },
                {
                        RunOutputRule.<Class<?>>builder()
                                .initializationErrorStyle(JUNIT4_INITIALIZATION_ERROR)
                                .appenderRule(new LoggingAppenderRule()),
                        RunnerParams.builder()
                                .put(Keys.TARGETED_TEST_RULE_KEY, new LoggingTargetedTestRule())
                                .build(),
                },
        });
    }
}
