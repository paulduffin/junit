package org.junit.runners.model;

import org.junit.internal.runners.InitializationErrorStyle;
import org.junit.rules.TargetedRuleChain;
import org.junit.rules.TargetedTestRule;
import org.junit.runner.Description;
import org.junit.runners.model.RunnerParams.Key;

/**
 * Contains keys for {@link RunnerParams}.
 *
 * @since 4.13
 */
public class Keys {

    /**
     * Controls whether classes with a {@code Test suite()} method are processed.
     *
     * <p>Defaults to {@code true}.
     */
    public static final Key<Boolean> USE_SUITE_METHOD =
            Key.of(Boolean.class, "org.junit.runners.model.UseSuiteMethod", true);

    /**
     * Controls what validation is done during initialization phase.
     *
     * <p>Defaults to {@link InitializationValidation#CLASS_AND_TEST_METHODS}.
     */
    public static final Key<InitializationValidation> INITIALIZATION_VALIDATION_KEY =
            Key.of(InitializationValidation.class, InitializationValidation.class.getName(),
                    InitializationValidation.CLASS_AND_TEST_METHODS);

    /**
     * Controls what {@link InitializationErrorStyle} is used for JUnit3 errors.
     *
     * <p>By default JUnit3 produces pretty meaningless {@link Description descriptions} for error
     * messages that arise during initialization phase, i.e. junit.framework.TestSuite$1. This
     * allows that behavior to be switched to the JUnit4 style which includes the class name.
     */
    public static final Key<InitializationErrorStyle> JUNIT3_INITIALIZATION_ERROR_STYLE_KEY =
            Key.of(InitializationErrorStyle.class,
                    "org.junit.runners.model.JUnit3InitializationErrorStyle",
                    InitializationErrorStyle.JUNIT3_WARNING);

    /**
     * Provides rules that are applied to all tests.
     */
    public static final Key<TargetedTestRule> TARGETED_TEST_RULE_KEY = Key.of(
            TargetedTestRule.class, TargetedTestRule.class.getName(),
            TargetedRuleChain.emptyRuleChain());
}
