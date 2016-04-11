package org.junit.runners.model;

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
}
