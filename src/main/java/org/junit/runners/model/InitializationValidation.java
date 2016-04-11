package org.junit.runners.model;

import org.junit.runner.Runner;

/**
 * Specifies what validation is done during the initialization phase when the {@link Runner}
 * hierarchy is created.
 *
 * @since 4.13
 */
public enum InitializationValidation {
    /**
     * Only validate class level requirements, i.e. those that affect all test methods.
     */
    CLASS_ONLY,

    /**
     * Validate both class and individual test method requirements.
     *
     * <p>With this option an invalid test method will prevent any tests from the affected class
     * from being run.
     */
    CLASS_AND_TEST_METHODS;
}
