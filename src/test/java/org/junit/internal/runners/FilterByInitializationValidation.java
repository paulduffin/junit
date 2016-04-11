package org.junit.internal.runners;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.InitializationValidation;
import org.junit.runners.model.Keys;
import org.junit.runners.model.RunnerParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Run tests that either are not annotated by {@link InitializationValidationRequired} or whose
 * {@link InitializationValidationRequired#value()} matches the value of the
 * {@link Keys#INITIALIZATION_VALIDATION_KEY} parameter.
 */
public class FilterByInitializationValidation extends Filter {
    private final RunnerParams runnerParams;

    public FilterByInitializationValidation(RunnerParams runnerParams) {
        this.runnerParams = runnerParams;
    }

    @Override
    public boolean shouldRun(Description description) {
        InitializationValidationRequired defer =
                description.getAnnotation(InitializationValidationRequired.class);
        return defer == null ||
                defer.value() == runnerParams.get(Keys.INITIALIZATION_VALIDATION_KEY);
    }

    @Override
    public String describe() {
        return "filter by " + Keys.INITIALIZATION_VALIDATION_KEY;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface InitializationValidationRequired {
        InitializationValidation value();
    }
}
