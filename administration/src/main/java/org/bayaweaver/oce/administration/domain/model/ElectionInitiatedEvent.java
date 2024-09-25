package org.bayaweaver.oce.administration.domain.model;

public final class ElectionInitiatedEvent {
    private final ElectionId id;
    private final CommunityId community;

    ElectionInitiatedEvent(ElectionId id, CommunityId community) {
        this.id = id;
        this.community = community;
    }

    ElectionId id() {
        return id;
    }

    CommunityId community() {
        return community;
    }
}
