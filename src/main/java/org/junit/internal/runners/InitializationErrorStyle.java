package org.junit.internal.runners;

import org.junit.runner.Description;

import java.lang.annotation.Annotation;

/**
 * The style of description produced for initialization errors.
 *
 * @since 4.13
 */
public enum InitializationErrorStyle {

    /**
     * Only uses a fixed description that has a method name of "warning" and a class name of
     * "junit.framework.TestSuite$1".
     *
     * <p>That's a consequence of the JUnit3/JUnit4 adaptation layer and while it's not very
     * helpful it is part of it's default behavior.
     */
    JUNIT3_WARNING {
        @Override
        public Description descriptionFor(String className, String name, Annotation[] annotations) {
            return Description.createTestDescription("junit.framework.TestSuite$1", "warning");
        }
    },

    /**
     * Uses a fixed method name of "initializationError".
     */
    JUNIT4_INITIALIZATION_ERROR {
        @Override
        public Description descriptionFor(String className, String name, Annotation[] annotations) {
            return Description.createTestDescription(className, "initializationError", annotations);
        }
    };

    public abstract Description descriptionFor(
            String className, String name, Annotation[] annotations);
}
