package org.junit.internal.runners.junit4.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InvalidTestMethodRunWithJUnit4 {

    @Test
    public int invalidMethod() {
        return -1;
    }

    @Test
    public void validMethod() {
    }
}
