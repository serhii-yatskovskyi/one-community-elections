package org.bayaweaver.oce.administration.domain.model.electoralregulation;

import org.bayaweaver.oce.administration.domain.model.community.MemberId;
import org.bayaweaver.oce.administration.domain.model.community.CongregationId;

public final class ElectionCompletedEvent {
    private final CongregationId completedBy;
    private final Iterable<MemberId> electedMembers;

    ElectionCompletedEvent(CongregationId completedBy, Iterable<MemberId> electedMembers) {
        this.completedBy = completedBy;
        this.electedMembers = electedMembers;
    }

    public CongregationId completedBy() {
        return completedBy;
    }

    public Iterable<MemberId> electedMembers() {
        return electedMembers;
    }
}
