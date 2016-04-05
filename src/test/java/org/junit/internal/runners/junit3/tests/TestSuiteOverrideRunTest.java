package org.junit.internal.runners.junit3.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Overrides {@link #runTest(Test, TestResult)} to do some custom processing before and after.
 */
public class TestSuiteOverrideRunTest extends TestSuite {

    @Override
    public void runTest(Test test, TestResult result) {
        try {
            System.out.println("Before Test");
            super.run(result);
        } finally {
            System.out.println("After Test");
        }
    }
}
