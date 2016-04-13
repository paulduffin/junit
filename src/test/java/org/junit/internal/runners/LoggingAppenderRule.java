package org.junit.internal.runners;

import org.junit.internal.runners.RunOutputRule.Appender;
import org.junit.internal.runners.RunOutputRule.AppenderRule;
import org.junit.runner.Description;

/**
 */
public class LoggingAppenderRule implements AppenderRule {
    @Override
    public Appender apply(Appender base, Description description, String target) {
        return new LoggingAppender(description, target, base);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private static class LoggingAppender implements Appender {
        private final Description description;
        private final String target;
        private final Appender base;

        public LoggingAppender(Description description, String target, Appender base) {
            this.description = description;
            this.target = target;
            this.base = base;
        }

        @Override
        public void append(StringBuilder builder) {
            builder.append("Before ").append(description.getMethodName()).append("\n");
            base.append(builder);
            builder.append("After ").append(description.getMethodName()).append("\n");
        }
    }
}
