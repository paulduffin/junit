package org.junit.runner;

import org.junit.runners.model.Statement;

/**
 * A {@link Statement} that also provides a {@link Description}
 *
 * @see StatementRunner
 * @see ParentStatementRunner
 *
 * @since 4.13
 */
public abstract class DescribableStatement extends Statement implements Describable {

    private final Description description;

    protected DescribableStatement(Description description) {
        this.description = description;
    }

    @Override
    public Description getDescription() {
        return description;
    }
}
