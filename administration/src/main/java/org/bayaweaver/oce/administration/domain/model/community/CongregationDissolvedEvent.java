package org.bayaweaver.oce.administration.domain.model.community;

public final class CongregationDissolvedEvent {
    private final CongregationId congregation;

    CongregationDissolvedEvent(CongregationId congregation) {
        this.congregation = congregation;
    }

    public CongregationId congregation() {
        return congregation;
    }
}
