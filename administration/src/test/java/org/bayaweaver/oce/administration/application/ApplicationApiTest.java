package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationApiTest {
    private ApplicationInterface api;

    @BeforeEach
    public void init() {
        var app = new Application();
        this.api = app.api();
    }

    @Test
    public void electionCanceled() {
        var communityId = new CommunityId(100);
        api.registerCommunity(communityId);
        api.registerMember(new MemberId(1), communityId);
        api.registerMember(new MemberId(2), communityId);
        api.registerMember(new MemberId(3), communityId);
        var electionId = new ElectionId(1);
        api.initiateElection(electionId, communityId);
        api.completeElection(communityId, List.of(new MemberId(1), new MemberId(2)));
        api.dissolveCommunity(communityId);
        assertTrue(api.electionCanceled(electionId));
    }

    @Test
    public void completeElectionWithWrongMember() {
        var communityId = new CommunityId(100);
        api.registerCommunity(communityId);
        api.registerMember(new MemberId(1), communityId);
        api.registerMember(new MemberId(2), communityId);
        api.registerMember(new MemberId(3), communityId);
        var electionId = new ElectionId(1);
        api.initiateElection(electionId, communityId);
        assertThrows(
                IllegalArgumentException.class,
                () -> api.completeElection(communityId, List.of(new MemberId(4))));
    }
}
