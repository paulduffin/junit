package org.junit.runner;

import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.Statement;

/**
 * Runs a {@link Statement}.
 *
 * <p>This is a generally useful form of {@link Runner} that can easily be customized.
 *
 * @since 4.13
 */
public class StatementRunner extends Runner implements Sortable {

    private final Description description;
    private final Statement statement;

    public StatementRunner(Description description, Statement statement) {
        this.description = description;
        this.statement = statement;
    }

    public StatementRunner(DescribableStatement statement) {
        this(statement.getDescription(), statement);
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        runStatement(statement, description, notifier);
    }

    @Override
    public void sort(Sorter sorter) {
        // Nothing to do.
    }
}
