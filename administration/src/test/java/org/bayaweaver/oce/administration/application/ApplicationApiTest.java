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
    private Application app;

    @BeforeEach
    public void init() {
        this.app = new Application();
    }

    @Test
    public void electionCanceled() {
        var communityId = new CommunityId(100);
        app.registerCommunity(communityId);
        app.registerMember(new MemberId(1), communityId);
        app.registerMember(new MemberId(2), communityId);
        app.registerMember(new MemberId(3), communityId);
        var electionId = new ElectionId(1);
        app.initiateElection(electionId, communityId);
        app.completeElection(communityId, List.of(new MemberId(1), new MemberId(2)));
        app.dissolveCommunity(communityId);
        assertTrue(app.electionCanceled(electionId));
    }

    @Test
    public void completeElectionWithWrongMember() {
        var communityId = new CommunityId(100);
        app.registerCommunity(communityId);
        app.registerMember(new MemberId(1), communityId);
        app.registerMember(new MemberId(2), communityId);
        app.registerMember(new MemberId(3), communityId);
        var electionId = new ElectionId(1);
        app.initiateElection(electionId, communityId);
        assertThrows(
                IllegalArgumentException.class,
                () -> app.completeElection(communityId, List.of(new MemberId(4))));
    }
}
