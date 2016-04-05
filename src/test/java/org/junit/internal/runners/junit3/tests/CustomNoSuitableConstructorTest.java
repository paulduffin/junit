package org.junit.internal.runners.junit3.tests;

import junit.framework.Test;
import junit.framework.TestResult;

public class CustomNoSuitableConstructorTest implements Test {

    private final int i;

    public CustomNoSuitableConstructorTest(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }

    @Override
    public int countTestCases() {
        return 1;
    }

    @Override
    public void run(TestResult result) {
        result.startTest(this);
        result.endTest(this);
    }
}
