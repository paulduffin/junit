package org.junit.internal.runners.junit3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        JUnit3CompatibilityTest.class,
        TestShapeRecognizerTest.class
})
public class AllJUnit3Tests {
}
