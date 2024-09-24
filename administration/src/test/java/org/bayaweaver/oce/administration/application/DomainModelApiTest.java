package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.BallotingCalendar;
import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.CommunityProvider;
import org.bayaweaver.oce.administration.domain.model.CommunityRegistry;
import org.bayaweaver.oce.administration.domain.model.CommunityRegistryEventHandler;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.MemberId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Observable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainModelApiTest {

    @Test
    public void electionCanceled() {
        var eventPublisher = new EventPublisher();
        eventPublisher.addObserver(new CommunityRegistryEventHandler());
        var communityRegistry = CommunityRegistry.instance();
        var communityId = new CommunityId(100);
        communityRegistry.registerCommunity(communityId);
        var community = communityRegistry.community(communityId);
        community.registerMember(new MemberId(1));
        community.registerMember(new MemberId(2));
        community.registerMember(new MemberId(3));
        var ballotingCalendar = BallotingCalendar.instance();
        var electionId = new ElectionId(1);
        ballotingCalendar.initiateElection(electionId, communityId);
        var election = ballotingCalendar.election(electionId);
        var communityProvider = (CommunityProvider) communityRegistry;
        election.complete(List.of(new MemberId(1)), communityId, communityProvider);
        var event = communityRegistry.dissolveCommunity(communityId);
        eventPublisher.publish(event);
        assertTrue(election.canceled());
    }

    @Test
    public void completeElectionWithWrongMember() {
        var communityRegistry = CommunityRegistry.instance();
        var communityId = new CommunityId(100);
        communityRegistry.registerCommunity(communityId);
        var community = communityRegistry.community(communityId);
        community.registerMember(new MemberId(1));
        community.registerMember(new MemberId(2));
        community.registerMember(new MemberId(3));
        var ballotingCalendar = BallotingCalendar.instance();
        var electionId = new ElectionId(1);
        ballotingCalendar.initiateElection(electionId, communityId);
        var election = ballotingCalendar.election(electionId);
        var communityProvider = (CommunityProvider) communityRegistry;
        assertThrows(
                IllegalArgumentException.class,
                () -> election.complete(List.of(new MemberId(4)), communityId, communityProvider));
    }

    /*@Test
    public void compilationErrors() {
        var communityRegistry = CommunityRegistry.instance();
        var communityId = new CommunityId(100);
        var community = communityRegistry.new Community(communityId);  // The Community can not be instantiated directly
        var ballotingCalendar = BallotingCalendar.instance();
        var electionId = new ElectionId(1);
        ballotingCalendar.new Election(electionId, community);         // The Election can not be instantiated directly
        ballotingCalendar.election(electionId).cancel();               // The Election can not be canceled by direct method call
    }*/
}
class EventPublisher extends Observable {

    void publish(Object event) {
        setChanged();
        notifyObservers(event);
    }
}
