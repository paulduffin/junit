package org.junit.internal.runners.junit3.tests;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class FailureTestCase extends TestCase {

    public void testFail() {
        throw new AssertionFailedError("Failed");
    }
}
