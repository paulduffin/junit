package org.junit.internal.runners.junit4.tests;

import org.junit.Test;

public class InvalidTestMethod {

    @Test
    public int invalidMethod() {
        return -1;
    }

    @Test
    public void validMethod() {
    }
}
