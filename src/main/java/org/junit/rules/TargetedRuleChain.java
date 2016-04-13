package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The TargetedRuleChain rule allows ordering of TargetedTestRules.
 *
 * <p>You create a {@code TargetedRuleChain} with {@link #outerRule(TargetedTestRule)} and
 * subsequent calls of {@link #around(TargetedTestRule)}:
 *
 * @since 4.13
 */
public class TargetedRuleChain implements TargetedTestRule {

    private static final TargetedRuleChain EMPTY_CHAIN = new TargetedRuleChain(
            Collections.<TargetedTestRule>emptyList());

    private List<TargetedTestRule> rulesStartingWithInnerMost;

    /**
     * Returns a {@code TargetedRuleChain} without a {@link TargetedTestRule}. This method may
     * be the starting point of a {@code TargetedRuleChain}.
     *
     * @return a {@code TargetedRuleChain} without a {@link TargetedTestRule}.
     */
    public static TargetedRuleChain emptyRuleChain() {
        return EMPTY_CHAIN;
    }

    /**
     * Returns a {@code TargetedRuleChain} with a single {@link TargetedTestRule}. This method
     * is the usual starting point of a {@code TargetedRuleChain}.
     *
     * @param outerRule the outer rule of the {@code TargetedRuleChain}.
     * @return a {@code TargetedRuleChain} with a single {@link TargetedTestRule}.
     */
    public static TargetedRuleChain outerRule(TargetedTestRule outerRule) {
        return emptyRuleChain().around(outerRule);
    }

    /**
     * Returns a {@code TargetedRuleChain} with a single {@link TestRule}. This method
     * is the usual starting point of a {@code TargetedRuleChain}.
     *
     * @param outerRule the outer rule of the {@code TargetedRuleChain}.
     * @return a {@code TargetedRuleChain} with a single {@link TestRule}.
     */
    public static TargetedRuleChain outerRule(TestRule outerRule) {
        return outerRule(wrap(outerRule));
    }

    private TargetedRuleChain(List<TargetedTestRule> rules) {
        this.rulesStartingWithInnerMost = rules;
    }

    /**
     * Create a new {@code TargetedRuleChain}, which encloses the given {@link TargetedTestRule} with
     * the rules of the current {@code TargetedRuleChain}.
     *
     * @param enclosedRule the rule to enclose.
     * @return a new {@code TargetedRuleChain}.
     */
    public TargetedRuleChain around(TargetedTestRule enclosedRule) {
        List<TargetedTestRule> rulesOfNewChain = new ArrayList<TargetedTestRule>();
        rulesOfNewChain.add(enclosedRule);
        rulesOfNewChain.addAll(rulesStartingWithInnerMost);
        return new TargetedRuleChain(rulesOfNewChain);
    }

    /**
     * Create a new {@code TargetedRuleChain}, which encloses the given {@link TestRule} with
     * the rules of the current {@code TargetedRuleChain}.
     *
     * @param enclosedRule the rule to enclose.
     * @return a new {@code TargetedRuleChain}.
     */
    public TargetedRuleChain around(TestRule enclosedRule) {
        return around(wrap(enclosedRule));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement apply(Statement base, Description description, Object target) {
        for (TargetedTestRule each : rulesStartingWithInnerMost) {
            base = each.apply(base, description, target);
        }
        return base;
    }

    public static TargetedTestRule wrap(final TestRule testRule) {
        return new TargetedTestRule() {
            @Override
            public Statement apply(Statement base, Description description, Object target) {
                return testRule.apply(base, description);
            }
        };
    }

    @Override
    public String toString() {
        return "TargetedRuleChain{" + rulesStartingWithInnerMost + '}';
    }
}
