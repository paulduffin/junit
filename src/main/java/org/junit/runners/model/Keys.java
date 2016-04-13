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
}
