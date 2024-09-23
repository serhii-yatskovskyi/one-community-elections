package org.bayaweaver.oce.administration.domain.model;

public final class MemberId {
    private final long value;

    public MemberId(long value) {
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
        MemberId that = (MemberId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
