package org.junit.internal.runners.junit3;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms a hierarchy of {@link Test} objects into a hierarchy of other objects.
 *
 * <p>This delegates the work of constructing tests and a suite (collection of tests) to a
 * {@link TestHierarchyFactory}.
 *
 * <p>Separates the traversing of the {@link Test} hierarchy from the construction of the
 * objects representing the 'suite' and 'test' allowing it to be more easily reused.
 *
 * @param <T> the type of the objects being constructed.
 *
 * @since 4.13
 */
public class TestHierarchyTransformer<T> {

    private final TestHierarchyFactory<T> factory;
    private TestShapeRecognizer testShapeRecognizer;

    public TestHierarchyTransformer(TestHierarchyFactory<T> factory) {
        this.factory = factory;
        testShapeRecognizer = TestShapeRecognizer.standardRecognizer();
    }

    public T transform(Class<?> suiteClass, Test test) {
        return transformTest(test, suiteClass.getName());
    }

    private T transformTest(Test test, String defaultName) {
        TestClassShape shape = testShapeRecognizer.examineTestShape(test.getClass());
        Description description = JUnit38ClassRunner.makeDescription(test);

        if (shape == TestClassShape.STANDARD_TEST_CASE) {
            return factory.createTestCase((TestCase) test, description);
        } else if (shape == TestClassShape.STANDARD_TEST_SUITE) {
            return transformSuite((TestSuite) test, defaultName);
        } else {
            return factory.createCustomTest(test, description);
        }
    }

    private T transformSuite(
            TestSuite testSuite, String defaultName) {
        List<T> children = new ArrayList<T>();
        int count = testSuite.testCount();
        String name = testSuite.getName();
        if (name == null) {
            name = defaultName;
        }
        for (int i = 0; i < count; i++) {
            Test test = testSuite.testAt(i);
            children.add(transformTest(test, name + "[" + i + "]"));
        }
        return factory.createSuite(name, children);
    }
}
