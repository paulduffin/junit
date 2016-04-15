package org.junit.internal.runners;

import junit.extensions.TestDecorator;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerParams;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class JUnit38ClassRunner extends Runner implements Filterable, Sortable {
    private static final class OldTestClassAdaptingListener implements
            TestListener {
        private final RunNotifier notifier;

        private OldTestClassAdaptingListener(RunNotifier notifier) {
            this.notifier = notifier;
        }

        public void endTest(Test test) {
            notifier.fireTestFinished(asDescription(test));
        }

        public void startTest(Test test) {
            notifier.fireTestStarted(asDescription(test));
        }

        // Implement junit.framework.TestListener
        public void addError(Test test, Throwable e) {
            Failure failure = new Failure(asDescription(test), e);
            notifier.fireTestFailure(failure);
        }

        private Description asDescription(Test test) {
            if (test instanceof Describable) {
                Describable facade = (Describable) test;
                return facade.getDescription();
            }
            return Description.createTestDescription(getEffectiveClass(test), getName(test));
        }

        private Class<? extends Test> getEffectiveClass(Test test) {
            return test.getClass();
        }

        private String getName(Test test) {
            if (test instanceof TestCase) {
                return ((TestCase) test).getName();
            } else {
                return test.toString();
            }
        }

        public void addFailure(Test test, AssertionFailedError t) {
            addError(test, t);
        }
    }

    private final RunnerParams runnerParams;

    private volatile Test test;

    /**
     * The description that was saved so that the test object could be discarded after the tests
     * were run.
     */
    private volatile Description savedDescription;

    public JUnit38ClassRunner(Class<?> klass) {
        this(RunnerParams.emptyParams(), klass);
    }

    /**
     * @since 4.13
     */
    public JUnit38ClassRunner(RunnerParams runnerParams, Class<?> klass) {
        this(runnerParams, new TestSuite(klass.asSubclass(TestCase.class)));
    }

    public JUnit38ClassRunner(Test test) {
        this(RunnerParams.emptyParams(), test);
    }

    /**
     * @since 4.13
     */
    public JUnit38ClassRunner(RunnerParams runnerParams, Test test) {
        super();
        this.runnerParams = runnerParams;
        setTest(test);
    }

    @Override
    public void run(final RunNotifier notifier) {
        final Test test = getTest();

        // Treat the code that performs the test as a Statement.
        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                TestResult result = new TestResult();
                result.addListener(createAdaptingListener(notifier));
                test.run(result);
            }
        };

        // Save the description away so that it can be used after the test has been discarded.
        savedDescription = getDescription();

        // Clear the test so that when this method returns it can be GCed.
        setTest(null);

        // Apply the global rules to check if there are any, if there are then fail as they are
        // not supported on custom tests..
        Statement withGlobalRules = runnerParams.get(Keys.TARGETED_TEST_RULE_KEY)
                .apply(statement, savedDescription, test);
        if (withGlobalRules != statement) {
            throw new IllegalStateException("Global rules cannot be applied to " + savedDescription
                    + " as it is a custom test and so the behavior would be undefined.");
        }
        try {
            // This should not fail because test.run(TestResult) should catch all exceptions
            // and report them through the path:
            //     TestResult -> OldTestClassAdaptingListener -> RunNotifier
            // However, there are no guarantees so this will ensure that the exceptions do not get
            // any further and are reported.
            statement.evaluate();
        } catch (Throwable throwable) {
            notifier.fireTestFailure(new Failure(savedDescription, throwable));
        }
    }

    public TestListener createAdaptingListener(final RunNotifier notifier) {
        return new OldTestClassAdaptingListener(notifier);
    }

    @Override
    public Description getDescription() {
        Test test = getTest();
        return test == null ? savedDescription : makeDescription(test);
    }

    public static Description makeDescription(Test test) {
        if (test instanceof TestCase) {
            TestCase tc = (TestCase) test;
            return Description.createTestDescription(tc.getClass(), tc.getName(),
                    getAnnotations(tc));
        } else if (test instanceof TestSuite) {
            TestSuite ts = (TestSuite) test;
            String name = ts.getName() == null ? createSuiteDescription(ts) : ts.getName();
            Description description = Description.createSuiteDescription(name);
            int n = ts.testCount();
            for (int i = 0; i < n; i++) {
                Description made = makeDescription(ts.testAt(i));
                description.addChild(made);
            }
            return description;
        } else if (test instanceof Describable) {
            Describable adapter = (Describable) test;
            return adapter.getDescription();
        } else if (test instanceof TestDecorator) {
            TestDecorator decorator = (TestDecorator) test;
            return makeDescription(decorator.getTest());
        } else {
            // This is the best we can do in this case
            return Description.createSuiteDescription(test.getClass());
        }
    }

    /**
     * Get the annotations associated with given TestCase.
     * @param test the TestCase.
     */
    private static Annotation[] getAnnotations(TestCase test) {
        try {
            Method m = test.getClass().getMethod(test.getName());
            return m.getDeclaredAnnotations();
        } catch (SecurityException ignored) {
        } catch (NoSuchMethodException ignored) {
        }
        return new Annotation[0];
    }

    private static String createSuiteDescription(TestSuite ts) {
        int count = ts.countTestCases();
        String example = count == 0 ? "" : String.format(" [example: %s]", ts.testAt(0));
        return String.format("TestSuite with %s tests%s", count, example);
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        if (getTest() instanceof Filterable) {
            Filterable adapter = (Filterable) getTest();
            adapter.filter(filter);
        }
    }

    public void sort(Sorter sorter) {
        if (getTest() instanceof Sortable) {
            Sortable adapter = (Sortable) getTest();
            adapter.sort(sorter);
        }
    }

    private void setTest(Test test) {
        this.test = test;
    }

    private Test getTest() {
        return test;
    }
}
