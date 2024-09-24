package org.bayaweaver.oce.administration.domain.model;

public final class CommunityDissolvedEvent {
    private final CommunityId community;

    CommunityDissolvedEvent(CommunityId community) {
        this.community = community;
    }

    CommunityId community() {
        return community;
    }
}
