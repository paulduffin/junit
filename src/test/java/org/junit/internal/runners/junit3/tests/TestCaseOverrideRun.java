package org.junit.internal.runners.junit3.tests;

import junit.framework.Protectable;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Overrides {@link #run(TestResult)} to do some custom processing before and after.
 */
public class TestCaseOverrideRun extends TestCase {

    @Override
    public void run(TestResult result) {
        result.startTest(this);
        System.out.println("Before");
        Protectable p = new Protectable() {
            public void protect() throws Throwable {
                runBare();
            }
        };
        result.runProtected(this, p);
        System.out.println("After");
        result.endTest(this);
    }

    public void test1() {
        System.out.println("test1");
    }

    public void test2() {
        System.out.println("test2");
    }
}
