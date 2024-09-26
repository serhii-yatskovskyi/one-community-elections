package org.bayaweaver.oce.administration.domain.model;

public final class ElectionCanceledEvent {
    private final ElectionId election;
    private final CommunityId community;

    ElectionCanceledEvent(ElectionId election, CommunityId community) {
        this.election = election;
        this.community = community;
    }

    ElectionId election() {
        return election;
    }

    CommunityId community() {
        return community;
    }
}
