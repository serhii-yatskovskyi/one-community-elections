package org.bayaweaver.oce.administration.domain.model;

public final class ElectionId {
    private final long value;

    public ElectionId(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ElectionId that = (ElectionId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
