package org.bayaweaver.oce.administration.domain.model;

public final class ElectionInitiatedEvent {
    private final ElectionId election;
    private final CommunityId initiator;

    ElectionInitiatedEvent(ElectionId election, CommunityId initiator) {
        this.election = election;
        this.initiator = initiator;
    }

    ElectionId election() {
        return election;
    }

    CommunityId initiator() {
        return initiator;
    }
}
