package org.junit.internal.runners;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Excludes parameterized test methods by their base name, i.e. the name of the method in the code
 * that does not include the parameters.
 */
public class ExcludeParameterizedMethodsByName extends Filter {

    private final Set<String> exclusions;

    public ExcludeParameterizedMethodsByName(String... exclusions) {
        this.exclusions = new TreeSet<String>(Arrays.asList(exclusions));
    }

    @Override
    public boolean shouldRun(Description description) {
        String displayName = description.getDisplayName();
        int index = displayName.indexOf('[');
        if (index == -1) {
            throw new IllegalStateException(
                    "Not parameterized method description: " + displayName);
        }
        String methodName = displayName.substring(0, index);
        return !exclusions.contains(methodName);
    }

    @Override
    public String describe() {
        return getClass().getName() + exclusions;
    }
}
