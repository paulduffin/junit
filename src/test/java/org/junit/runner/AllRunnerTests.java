package org.junit.runner;

import org.junit.runner.notification.AllNotificationTests;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AllNotificationTests.class,
        DescriptionComparatorsTest.class,
        FilterFactoriesTest.class,
        FilterOptionIntegrationTest.class,
        JUnitCommandLineParseResultTest.class,
        JUnitCoreTest.class,
        ParentStatementRunnerTest.class,
        StatementRunnerTest.class
})
public class AllRunnerTests {
}
