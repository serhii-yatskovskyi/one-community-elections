package org.bayaweaver.oce.administration.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Iterables {

    private Iterables() {}

    public static <T> boolean containsAll(final Iterable<T> iterable, final Iterable<T> elements) {
        for (Object e2 : elements) {
            boolean contains = false;
            for (Object e1 : iterable) {
                if (e1.equals(e2)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    public static <T> Stream<T> stream(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
