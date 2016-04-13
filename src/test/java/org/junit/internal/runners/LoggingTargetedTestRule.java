package org.junit.internal.runners;

import org.junit.rules.TargetedTestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 */
public class LoggingTargetedTestRule implements TargetedTestRule {
    @Override
    public Statement apply(Statement base, Description description, Object target) {
        return new LoggingStatement(description, target, base);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private static class LoggingStatement extends Statement {
        private final Description description;
        private final Object target;
        private final Statement base;

        public LoggingStatement(Description description, Object target, Statement base) {
            this.description = description;
            this.target = target;
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            System.out.println("Before " + description.getMethodName());
            try {
                base.evaluate();
            } finally {
                System.out.println("After " + description.getMethodName());
            }
        }
    }
}
