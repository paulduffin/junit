package org.junit.runners.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        FrameworkFieldTest.class,
        FrameworkMethodTest.class,
        RunnerParamsTest.class,
        TestClassTest.class,
        TypeLiteralTest.class
})
public class AllModelTests {
}
