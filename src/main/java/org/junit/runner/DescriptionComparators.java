package org.junit.runner;

import java.util.Comparator;

/**
 * Provides some helpful {@link Comparator} for comparing {@link Description} and
 * {@link Describable} objects.
 *
 * @since 4.13
 */
public class DescriptionComparators {

    private DescriptionComparators() {
    }

    /**
     * Order by {@link Description#getClassName()} first and then by
     * {@link Description#getDisplayName()}.
     */
    public static final Comparator<Description> DESCRIPTION_BY_CLASS_FIRST =
            new Comparator<Description>() {
                @Override
                public int compare(Description d1, Description d2) {
                    String c1 = d1.getClassName();
                    String c2 = d2.getClassName();
                    int result = c1.compareTo(c2);
                    if (result != 0) {
                        return result;
                    }

                    return d1.getDisplayName().compareTo(d2.getDisplayName());
                }
            };

    /**
     * Order by {@link Description#getDisplayName()}.
     */
    public static final Comparator<Description> DESCRIPTION_BY_DISPLAY_NAME =
            new Comparator<Description>() {
                @Override
                public int compare(Description d1, Description d2) {
                    return d1.getDisplayName().compareTo(d2.getDisplayName());
                }
            };

    /**
     * As {@link #DESCRIPTION_BY_CLASS_FIRST} on the result of calling
     * {@link Describable#getDescription()}.
     */
    public static final Comparator<Describable> DESCRIBABLE_BY_CLASS_FIRST =
            describableComparator(DESCRIPTION_BY_CLASS_FIRST);

    /**
     * As {@link #DESCRIPTION_BY_DISPLAY_NAME} on the result of calling
     * {@link Describable#getDescription()}.
     */
    public static final Comparator<Describable> DESCRIBABLE_BY_DISPLAY_NAME =
            describableComparator(DESCRIPTION_BY_DISPLAY_NAME);

    /**
     * Construct a {@link Comparator} for {@link Describable} from one for {@link Description}.
     * @param descriptionComparator the {@link Comparator} for {@link Description}
     * @return the {@link Comparator} for {@link Describable}
     */
    public static Comparator<Describable> describableComparator(
            final Comparator<Description> descriptionComparator) {
        return new Comparator<Describable>() {
            @Override
            public int compare(Describable d1, Describable d2) {
                return descriptionComparator.compare(d1.getDescription(), d2.getDescription());
            }
        };
    }
}
