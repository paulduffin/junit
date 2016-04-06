package org.junit.internal.runners.junit3;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;
import junit.tests.framework.OneTestCase;
import junit.tests.framework.ThreeTestCases;
import org.junit.Test;
import org.junit.internal.runners.junit3.tests.CustomTest;
import org.junit.internal.runners.junit3.tests.SuiteMethodWithCustomTests;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

/**
 * Tests for {@link TestHierarchyTransformer}.
 */
@RunWith(JUnit4.class)
public class TestHierarchyTransformerTest {

    @Test
    public void testWithSuiteMethod() throws Exception {
        TestHierarchyTransformer<String> transformer = new TestHierarchyTransformer<String>(new TestHierarchyFactory<String>() {
            @Override
            public String createSuite(String name, List<String> children) {
                StringBuilder builder = new StringBuilder();
                builder.append("Suite: ").append(name).append("\n{\n");
                for (String child : children) {
                    builder.append(child.replaceAll("([^\n]*)\n", "  $1\n"));
                }
                builder.append("}\n");
                return builder.toString();
            }

            @Override
            public String createTestCase(TestCase testCase, Description description) {
                return "{TestCase: " + description.getDisplayName() + "}\n";
            }

            @Override
            public String createCustomTest(junit.framework.Test test, Description description) {
                return "{Test: " + description.getDisplayName() + "}\n";
            }
        });

        assertEquals(""
                        + "Suite: " + SuiteMethodWithCustomTests.class.getName() + "\n"
                        + "{\n"
                        + "  Suite: " + OneTestCase.class.getName() + "\n"
                        + "  {\n"
                        + "    {TestCase: testCase(" + OneTestCase.class.getName() + ")}\n"
                        + "  }\n"
                        + "  {Test: " + CustomTest.class.getName() + "}\n"
                        + "  {Test: TestSuite with 2 tests "
                        + "[example: testCase(" + ThreeTestCases.class.getName() + ")]}\n"
                        + "}\n",
                transformer.transform(SuiteMethodWithCustomTests.class,
                        SuiteMethodWithCustomTests.suite()));

    }
}
    