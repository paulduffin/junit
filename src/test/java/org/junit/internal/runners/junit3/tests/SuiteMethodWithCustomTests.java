package org.junit.internal.runners.junit3.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.tests.framework.OneTestCase;
import junit.tests.framework.ThreeTestCases;

public class SuiteMethodWithCustomTests {

    public static Test suite() {
        TestSuite testSuite = new TestSuite(SuiteMethodWithCustomTests.class.getName());
        testSuite.addTestSuite(OneTestCase.class);
        testSuite.addTest(new CustomTest("test1"));
        testSuite.addTest(createNestedSuite());
        return testSuite;
    }

    public static TestSuite createNestedSuite() {
        TestSuite suite = new TestSuiteOverrideRun(null);
        TestCase test = new ThreeTestCases();
        test.setName("testCase");
        suite.addTest(test);

        test = new ThreeTestCases();
        test.setName("testCase2");
        suite.addTest(test);

        return suite;
    }
}
