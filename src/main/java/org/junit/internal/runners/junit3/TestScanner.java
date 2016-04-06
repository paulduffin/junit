package org.junit.internal.runners.junit3;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.internal.MethodSorter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans a {@link Test} based test and delegates construction of a representation to a factory.
 *
 * <p>This delegates the work of constructing tests and a suite (collection of tests) to a
 * {@link TestFactory}.
 *
 * <p>Separates the scanning of the {@link TestCase} derived class from the construction of the
 * objects representing the 'suite' and 'test' allowing it to be more easily reused.
 *
 * @param <C> the base class of test classes this can handle
 * @param <S> the type of the 'suite' component
 * @param <T> the type of the 'test' component
 *
 * @since 4.13
 */
public class TestScanner<C, S, T> {

    private final TestFactory<C, S, T> factory;

    public TestScanner(TestFactory<C, S, T> factory) {
        this.factory = factory;
    }

    public S createSuite(Class<? extends C> testClass) {
        List<T> tests = testsFromTestCase(testClass);
        return factory.createSuite(testClass, tests);
    }

    private List<T> testsFromTestCase(final Class<? extends C> testClass) {
        List<T> tests = new ArrayList<T>();

        // Check as much as possible in advance to avoid generating multiple error messages for the
        // same failure.
        try {
            validateTestClass(testClass);
        } catch (AssertionFailedError e) {
            tests.add(factory.createTestForInitializationError(testClass, "warning",
                    testClass.getAnnotations(), e.getMessage()));
            return tests;
        }

        Class<?> superClass = testClass;
        List<String> names = new ArrayList<String>();
        while (Test.class.isAssignableFrom(superClass)) {
            for (Method each : MethodSorter.getDeclaredMethods(superClass)) {
                addTestMethod(tests, each, names, testClass);
            }
            superClass= superClass.getSuperclass();
        }

        if (tests.size() == 0) {
            tests.add(factory.createTestForInitializationError(testClass, null,
                    testClass.getAnnotations(), "No tests found in " + testClass.getName()));
        }

        return tests;
    }

    private void addTestMethod(
            List<T> tests, Method m, List<String> names, Class<? extends C> theClass) {
        String name= m.getName();
        if (names.contains(name)) {
            return;
        }
        if (isTestMethod(m)) {
            T test;
            if (Modifier.isPublic(m.getModifiers())) {
                names.add(name);
                test = factory.createTest(theClass, name, m.getAnnotations());
            } else {
                test = factory.createTestForInitializationError(theClass, m.getName(),
                        m.getAnnotations(), "Test method isn't public: " + m.getName()
                                + "(" + theClass.getCanonicalName() + ")");
            }
            tests.add(test);
        }
    }

    private static boolean isTestMethod(Method m) {
        return m.getParameterTypes().length == 0
                && m.getName().startsWith("test")
                && m.getReturnType().equals(Void.TYPE);
    }

    public static void validateTestClass(Class<?> testClass) {
        try {
            TestSuite.getTestConstructor(testClass);
        } catch (NoSuchMethodException e) {
            throw new AssertionFailedError(
                    "Class " + testClass.getName()
                            + " has no public constructor TestCase(String name) or TestCase()");
        }

        if (!Modifier.isPublic(testClass.getModifiers())) {
                throw new AssertionFailedError("Class " + testClass.getName() + " is not public");
        }
    }
}
