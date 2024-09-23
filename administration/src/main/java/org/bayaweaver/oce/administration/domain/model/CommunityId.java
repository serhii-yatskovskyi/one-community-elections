package org.bayaweaver.oce.administration.domain.model;

public final class CommunityId {
    private final long value;

    public CommunityId(long value) {
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
        CommunityId that = (CommunityId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
}
