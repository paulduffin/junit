package org.junit.runner;

import org.junit.internal.runners.statements.Fail;
import org.junit.runners.model.Statement;

/**
 * Common implementations of {@link Statement}.
 */
public class Statements {
    private Statements() {
    }

    public static Statement throwing(Throwable throwable) {
        return new Fail(throwable);
    }
}
