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

    /**
     * Pair up a {@link Description} and {@link Statement} as a {@link DescribableStatement}.
     */
    public static DescribableStatement pair(Description description, final Statement statement) {
        return new DescribableStatement(description) {
            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();
            }
        };
    }

    private final Description description;

    protected DescribableStatement(Description description) {
        this.description = description;
    }

    @Override
    public Description getDescription() {
        return description;
    }
}
