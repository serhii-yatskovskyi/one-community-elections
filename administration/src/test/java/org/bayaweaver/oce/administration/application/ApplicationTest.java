package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {
    private Application application;

    @BeforeEach
    public void init() {
        application = new Application();
        application.init();
    }

    @Test
    public void electionCanceled() {
        var communityId = new CommunityId(100);
        application.registerCommunity(communityId);
        application.registerMember(new MemberId(1), communityId);
        application.registerMember(new MemberId(2), communityId);
        application.registerMember(new MemberId(3), communityId);
        var electionId = new ElectionId(1);
        application.initiateElection(electionId, communityId);
        application.completeElection(communityId, List.of(new MemberId(1), new MemberId(2)));
        application.dissolveCommunity(communityId);
        assertTrue(application.electionCanceled(electionId));
    }

    @Test
    public void completeElectionWithWrongMember() {
        var communityId = new CommunityId(100);
        application.registerCommunity(communityId);
        application.registerMember(new MemberId(1), communityId);
        application.registerMember(new MemberId(2), communityId);
        application.registerMember(new MemberId(3), communityId);
        var electionId = new ElectionId(1);
        application.initiateElection(electionId, communityId);
        assertThrows(
                IllegalArgumentException.class,
                () -> application.completeElection(communityId, List.of(new MemberId(4))));
    }
}
