package org.junit.rules;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.rules.TargetedRuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class TargetedRuleChainTest {
    private static final List<String> LOG = new ArrayList<String>();

    private static class LoggingRule extends TestWatcher {
        private final String label;

        public LoggingRule(String label) {
            this.label = label;
        }

        @Override
        protected void starting(Description description) {
            LOG.add("starting " + label);
        }

        @Override
        protected void finished(Description description) {
            LOG.add("finished " + label);
        }
    }

    private static class TargetedLoggingRule implements TargetedTestRule {
        private final String label;

        public TargetedLoggingRule(String label) {
            this.label = label;
        }

        @Override
        public Statement apply(final Statement base, Description description, final Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    LOG.add("starting " + label + " on " + target);
                    try {
                        base.evaluate();
                    } finally {
                        LOG.add("finished " + label + " on " + target);
                    }
                }
            };
        }
    }

    public static class UseTargetedRuleChain {
        @Rule
        public final TargetedRuleChain chain = outerRule(new LoggingRule("outer rule"))
                .around(new TargetedLoggingRule("middle rule"))
                .around(new LoggingRule("inner rule"));

        @Test
        public void example() {
            assertTrue(true);
        }

        @Override
        public String toString() {
            return "UseRuleChain@1";
        }
    }

    @Test
    public void executeRulesInCorrectOrder() throws Exception {
        PrintableResult result = testResult(UseTargetedRuleChain.class);
        if (result.failureCount() != 0) {
            throw new Exception(result.toString());
        }
        List<String> expectedLog = asList("starting outer rule",
                "starting middle rule on UseRuleChain@1", "starting inner rule",
                "finished inner rule", "finished middle rule on UseRuleChain@1",
                "finished outer rule");
        assertEquals(expectedLog, LOG);
    }
}