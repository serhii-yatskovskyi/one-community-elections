package org.bayaweaver.oce.administration.util;

public abstract class Iterables {

    private Iterables() {}

    public static boolean containsAll(final Iterable<?> iterable, final Iterable<?> elements) {
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
}
