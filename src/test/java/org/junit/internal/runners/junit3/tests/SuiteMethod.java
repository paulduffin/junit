package org.junit.internal.runners.junit3.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.tests.framework.OneTestCase;

public class SuiteMethod {

    public static Test suite() {
        return new TestSuite(OneTestCase.class);
    }
}
