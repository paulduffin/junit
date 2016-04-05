package org.junit.internal.runners.junit3.tests;

import junit.framework.TestCase;

public class ErrorTestCase extends TestCase {

    public void testError() {
        System.out.println("Before it breaks");
        throw new IllegalStateException();
    }
}
