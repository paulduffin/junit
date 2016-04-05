package org.junit.internal.runners.junit3.tests;

import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Overrides {@link #run(TestResult)} to do some custom processing before and after.
 */
public class TestSuiteOverrideRun extends TestSuite {

    public TestSuiteOverrideRun(String name) {
        super(name);
    }

    @Override
    public void run(TestResult result) {
        try {
            System.out.println("Before Suite");
            super.run(result);
        } finally {
            System.out.println("After Suite");
        }
    }
}
