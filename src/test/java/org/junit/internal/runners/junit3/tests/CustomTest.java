package org.junit.internal.runners.junit3.tests;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;

public class CustomTest implements Test {

    private final String name;

    public CustomTest(String name) {
        this.name = name;
    }

    @Override
    public int countTestCases() {
        return 1;
    }

    @Override
    public void run(TestResult result) {
        result.startTest(this);
        if (name.equals("test1")) {
            System.out.println("test - " + name);
        } else if (name.equals("test2")) {
            result.addFailure(this, new AssertionFailedError("Failed"));
        } else if (name.equals("test3")) {
            result.addError(this, new Exception("Exception"));
        }
        result.endTest(this);
    }

    public void test1() {
    }

    public void test2() {
    }

    public void test3() {
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}
