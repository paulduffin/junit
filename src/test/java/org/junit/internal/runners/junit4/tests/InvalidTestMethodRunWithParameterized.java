package org.junit.internal.runners.junit4.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class InvalidTestMethodRunWithParameterized {

    private final int value;

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-1},
                {-2},
        });
    }

    public InvalidTestMethodRunWithParameterized(int value) {
        this.value = value;
    }

    @Test
    public int invalidMethod() {
        return value;
    }

    @Test
    public void validMethod() {
    }
}
