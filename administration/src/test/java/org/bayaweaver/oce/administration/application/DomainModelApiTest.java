package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.BoundedContext;
import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.MemberId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainModelApiTest {

    @Test
    public void electionCanceled() {
        BoundedContext context = BoundedContext.instance();
        var communityId = new CommunityId("A");
        context.registerCommunity(communityId);
        var community = context.community(communityId);
        community.registerMember(new MemberId(1));
        community.registerMember(new MemberId(2));
        community.registerMember(new MemberId(3));
        var electionId = new ElectionId(1);
        context.initiateElection(electionId, communityId);
        var election = context.election(electionId);
        election.complete(List.of(new MemberId(1)), communityId);
        context.dissolveCommunity(communityId);
        assertTrue(election.canceled());
    }

    @Test
    public void completeElectionWithWrongMember() {
        BoundedContext context = BoundedContext.instance();
        var communityId = new CommunityId("A");
        context.registerCommunity(communityId);
        var community = context.community(communityId);
        community.registerMember(new MemberId(1));
        community.registerMember(new MemberId(2));
        community.registerMember(new MemberId(3));
        var electionId = new ElectionId(1);
        context.initiateElection(electionId, communityId);
        var election = context.election(electionId);
        assertThrows(
                IllegalArgumentException.class,
                () -> election.complete(List.of(new MemberId(4)), communityId));
    }

    /*@Test
    public void compilationErrors() {
        BoundedContext context = BoundedContext.instance();
        var communityId = new CommunityId("A");
        var community = context.new Community(communityId);  // The Community can not be instantiated directly
        var electionId = new ElectionId(1);
        context.new Election(electionId, community);         // The Election can not be instantiated directly
        context.election(electionId).cancel();               // The Election can not be canceled by direct method call
    }*/
}
