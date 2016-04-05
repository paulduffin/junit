package org.junit.runner;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.junit.runners.JUnit4;

import java.util.Arrays;

/**
 * Tests for {@link DescriptionComparators}.
 */
@RunWith(JUnit4.class)
public class DescriptionComparatorsTest {

    private static final Description CLASS_A_METHOD_B =
            Description.createTestDescription("ClassA", "methodB");
    private static final Description CLASS_B_METHOD_A =
            Description.createTestDescription("ClassB", "methodA");
    private static final Description CLASS_A_METHOD_A =
            Description.createTestDescription("ClassA", "methodA");
    private static final Description CLASS_B_METHOD_B =
            Description.createTestDescription("ClassB", "methodB");

    private static final Description[] UNSORTED = new Description[] {
            CLASS_A_METHOD_B,
            CLASS_B_METHOD_A,
            CLASS_A_METHOD_A,
            CLASS_B_METHOD_B,
    };

    private static final Description[] BY_CLASS_FIRST = new Description[]{
            CLASS_A_METHOD_A,
            CLASS_A_METHOD_B,
            CLASS_B_METHOD_A,
            CLASS_B_METHOD_B,
    };

    private static final Description[] BY_DISPLAY_NAME = new Description[]{
            CLASS_A_METHOD_A,
            CLASS_B_METHOD_A,
            CLASS_A_METHOD_B,
            CLASS_B_METHOD_B,
    };

    private Description[] unsortedCopy() {
        return Arrays.copyOf(UNSORTED, UNSORTED.length);
    }

    private Describable[] wrap(Description[] descriptions) {
        int count = descriptions.length;
        Describable[] describables = new Describable[count];
        for (int i = 0; i < count; i++) {
            Description description = descriptions[i];
            describables[i] = wrap(description);
        }
        return describables;
    }

    private Describable wrap(final Description description) {
        return new DescribableImpl(description);
    }

    @Test
    public void descriptionByClassFirst() {
        Description[] descriptions = unsortedCopy();
        Arrays.sort(descriptions, DescriptionComparators.DESCRIPTION_BY_CLASS_FIRST);
        assertArrayEquals(BY_CLASS_FIRST, descriptions);
    }

    @Test
    public void describablesByClassFirst() {
        Describable[] describables = wrap(UNSORTED);
        Arrays.sort(describables, DescriptionComparators.DESCRIBABLE_BY_CLASS_FIRST);
        assertArrayEquals(wrap(BY_CLASS_FIRST), describables);
    }


    @Test
    public void descriptionByDisplayName() {
        Description[] descriptions = unsortedCopy();
        Arrays.sort(descriptions, DescriptionComparators.DESCRIPTION_BY_DISPLAY_NAME);
        assertArrayEquals(BY_DISPLAY_NAME, descriptions);
    }

    @Test
    public void describablesByDisplayName() {
        Describable[] describables = wrap(UNSORTED);
        Arrays.sort(describables, DescriptionComparators.DESCRIBABLE_BY_DISPLAY_NAME);
        assertArrayEquals(wrap(BY_DISPLAY_NAME), describables);
    }

    private static class DescribableImpl implements Describable {
        private final Description description;

        public DescribableImpl(Description description) {
            this.description = description;
        }

        @Override
        public Description getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DescribableImpl that = (DescribableImpl) o;

            return description.equals(that.description);
        }

        @Override
        public int hashCode() {
            return description.hashCode();
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}
    