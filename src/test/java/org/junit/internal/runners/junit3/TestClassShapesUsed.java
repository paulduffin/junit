package org.junit.internal.runners.junit3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply to a test method to indicate what {@link TestClassShape} of test classes it uses.
 *
 * <p>Allows a test runner to filter the tests that it cannot support.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TestClassShapesUsed {
    TestClassShape[] value() default TestClassShape.STANDARD_TEST_CASE;
}
