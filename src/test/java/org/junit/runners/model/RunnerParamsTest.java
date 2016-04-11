package org.junit.runners.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.model.RunnerParams.Key;

/**
 * Tests for {@link RunnerParams}.
 */
@RunWith(JUnit4.class)
public class RunnerParamsTest {

    @Test
    public void testPutAndGet() throws Exception {
        Key<CharSequence> labelKey = Key.of(CharSequence.class, "label");
        Key<CharSequence> otherKey = Key.of(CharSequence.class, "other");
        Key<Boolean> booleanKey = Key.of(Boolean.class, "booleanKey");
        Key<Boolean> keyWithDefault = Key.of(Boolean.class, "keyWithDefault", true);
        String label = "Fred";
        RunnerParams params = RunnerParams.builder()
                .put(labelKey, label)
                .build();

        CharSequence sequence = params.get(labelKey);
        assertSame("existing key", label, sequence);

        sequence = params.get(labelKey, "defaultLabel");
        assertSame("existing key, ignore default", label, sequence);

        sequence = params.get(otherKey);
        assertNull("missing key", sequence);

        String defaultValue = "default";
        sequence = params.get(otherKey, defaultValue);
        assertSame("missing key use default", defaultValue, sequence);

        Boolean result;

        result = params.get(booleanKey);
        assertNull("missing boolean key", result);

        result = params.get(keyWithDefault);
        assertEquals("boolean key with default", true, result);
    }
}
    