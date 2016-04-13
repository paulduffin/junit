package org.junit.rules;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A generic rule (like {@link TestRule}) but which has access to the target test object (like
 * {@link MethodRule}).
 *
 * <p>This can be used with the {@link Rule @Rule} annotation but not the
 * {@link ClassRule @ClassRule} annotation.
 *
 * @since 4.13
 */
public interface TargetedTestRule {
    /**
     * Modifies the method-running {@link Statement} to implement this
     * test-running rule.
     *
     * @param base The {@link Statement} to be modified
     * @param description A {@link Description} of the test implemented in {@code base}
     * @param target The test object to be run.
     * @return a new statement, which may be the same as {@code base},
     *         a wrapper around {@code base}, or a completely new Statement.
     */
    Statement apply(Statement base, Description description, Object target);
}
