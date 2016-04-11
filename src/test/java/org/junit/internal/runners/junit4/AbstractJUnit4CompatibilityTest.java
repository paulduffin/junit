package org.junit.internal.runners.junit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.FilterByInitializationValidation;
import org.junit.internal.runners.FilterByInitializationValidation.InitializationValidationRequired;
import org.junit.internal.runners.RunOutputRule;
import org.junit.internal.runners.junit4.tests.InvalidTestMethod;
import org.junit.internal.runners.junit4.tests.InvalidTestMethodRunWithJUnit4;
import org.junit.runner.RunTests;
import org.junit.runners.model.InitializationValidation;
import org.junit.runners.model.RunnerParams;

/**
 */
public class AbstractJUnit4CompatibilityTest {
    @Rule
    public RunOutputRule<Class<?>> runOutputRule;

    public AbstractJUnit4CompatibilityTest(
            RunOutputRule.Builder<Class<?>> builder,
            RunnerParams runnerParams) {
        runOutputRule = builder
                .runTest(RunTests.runWithJUnitCore(runnerParams))
                .filter(new FilterByInitializationValidation(runnerParams))
                .build();
    }

    @InitializationValidationRequired(InitializationValidation.CLASS_AND_TEST_METHODS)
    @Test
    public void validateClassAndTestMethodsDuringInitialization_InvalidTestMethod() {
        runOutputRule.forClass(InvalidTestMethod.class)
                .test("invalidMethod")
                .initializationError("java.lang.Exception: Method invalidMethod() should be void")
                .check(InvalidTestMethod.class);
    }

    @InitializationValidationRequired(InitializationValidation.CLASS_ONLY)
    @Test
    public void validateClassOnlyDuringInitialization_InvalidTestMethod()
            throws Exception {
        runOutputRule.forClass(InvalidTestMethod.class)
                .test("invalidMethod")
                .error("java.lang.Exception: Method invalidMethod() should be void")
                .test("validMethod")
                .passed()
                .check(InvalidTestMethod.class);
    }

    @InitializationValidationRequired(InitializationValidation.CLASS_AND_TEST_METHODS)
    @Test
    public void validateClassAndTestMethodsDuringInitializations_InvalidTestMethodRunWithJUnit4() {
        runOutputRule.forClass(InvalidTestMethodRunWithJUnit4.class)
                .test("invalidMethod")
                .initializationError("java.lang.Exception: Method invalidMethod() should be void")
                .check(InvalidTestMethodRunWithJUnit4.class);
    }

    @InitializationValidationRequired(InitializationValidation.CLASS_ONLY)
    @Test
    public void validateClassOnlyDuringInitialization_InvalidTestMethodRunWithJUnit4()
            throws Exception {
        runOutputRule.forClass(InvalidTestMethodRunWithJUnit4.class)
                .test("invalidMethod")
                .error("java.lang.Exception: Method invalidMethod() should be void")
                .test("validMethod")
                .passed()
                .check(InvalidTestMethodRunWithJUnit4.class);
    }
}
