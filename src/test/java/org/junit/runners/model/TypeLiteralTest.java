package org.junit.runners.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

/**
 * Tests for {@link TypeLiteral}.
 */
@RunWith(JUnit4.class)
public class TypeLiteralTest {

    @Test
    public void testExtension_GetType() throws Exception {
        TypeLiteral<List<String>> literal = new TypeLiteral<List<String>>() {};

        assertEquals("java.util.List<java.lang.String>", literal.getType().toString());
    }

    @Test
    public void testNoExtension_GetType() throws Exception {
        try {
            new TypeLiteral<List<String>>();
            fail("Did not detect error");
        } catch (IllegalStateException e) {
            assertEquals("Must extend the class, e.g. new TypeLiteral<T>() {}", e.getMessage());
        }
    }

    @Test
    public void testExtensionNoType_GetType() throws Exception {
        try {
            new TypeLiteral() {};
            fail("Did not detect error");
        } catch (IllegalStateException e) {
            assertEquals("Must specify a type parameter, e.g. new TypeLiteral<T>() {}",
                    e.getMessage());
        }
    }

    @Test
    public void testEquality_OfClass() throws Exception {
        TypeLiteral<String> stringTypeLiteral1 = TypeLiteral.of(String.class);
        TypeLiteral<String> stringTypeLiteral2 = TypeLiteral.of(String.class);

        assertEquals(stringTypeLiteral1, stringTypeLiteral2);
    }

    @Test
    public void testEquality_Extension() throws Exception {
        TypeLiteral<List<String>> listStringLiteral1 = new TypeLiteral<List<String>>() {};
        TypeLiteral<List<String>> listStringLiteral2 = new TypeLiteral<List<String>>() {};

        assertEquals(listStringLiteral1, listStringLiteral2);
    }
}
    