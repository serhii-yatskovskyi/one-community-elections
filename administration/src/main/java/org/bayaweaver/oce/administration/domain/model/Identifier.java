package org.bayaweaver.oce.administration.domain.model;

abstract class Identifier {
    private final long value;

    Identifier(long value) {
        this.value = value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Identifier that = (Identifier) o;
        return value == that.value;
    }

    @Override
    public final int hashCode() {
        return (int) value;
    }
}
