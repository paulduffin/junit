package org.junit.runner;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * A {@link ParentRunner} whose children are {@link DescribableStatement} instances.
 *
 * <p>This is a generally useful form of {@link ParentRunner} that can easily be customized.
 *
 * <p>This is used for JUnit3 based classes which may have multiple constructors (a default and
 * one that takes the test name) so it passes a null class to the parent and overrides the
 * {@link #getName()} method.
 *
 * @since 4.13
 */
public class ParentStatementRunner extends ParentRunner<DescribableStatement> {

    private final Class<?> testClass;
    private final List<DescribableStatement> statements;

    public ParentStatementRunner(Class<?> testClass, List<DescribableStatement> statements)
            throws InitializationError {
        super(null);
        this.testClass = testClass;
        this.statements = statements;
    }

    @Override
    protected String getName() {
        return testClass.getName();
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        return testClass.getAnnotations();
    }

    @Override
    protected List<DescribableStatement> getChildren() {
        return statements;
    }

    @Override
    protected Description describeChild(DescribableStatement child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(final DescribableStatement child, RunNotifier notifier) {
        Description description = describeChild(child);
        runLeaf(child, description, notifier);
    }
}
