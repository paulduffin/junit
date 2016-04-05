package org.junit.internal.runners.junit3.tests;

import junit.framework.Test;

public class WithBrokenSuiteMethod {

    public static Test suite() {
        throw new IllegalStateException("Broken");
    }
}
