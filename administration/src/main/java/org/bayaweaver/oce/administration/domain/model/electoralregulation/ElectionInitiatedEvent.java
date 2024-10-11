package org.bayaweaver.oce.administration.domain.model.electoralregulation;

import org.bayaweaver.oce.administration.domain.model.community.CongregationId;

public final class ElectionInitiatedEvent {
    private final CongregationId initiator;

    ElectionInitiatedEvent(CongregationId initiator) {
        this.initiator = initiator;
    }

    public CongregationId initiator() {
        return initiator;
    }
}
