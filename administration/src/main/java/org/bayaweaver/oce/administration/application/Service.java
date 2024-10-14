package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.community.Community;
import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.community.MemberId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectoralRegulation;

import java.time.Clock;
import java.time.Duration;

public class Service {
    private final Clock clock;
    private final ElectoralRegulation electoralRegulation;
    private final Community community;

    public Service() {
        clock = Clock.systemUTC();
        electoralRegulation = new ElectoralRegulation();
        community = new Community();
        electoralRegulation.addObserver(community);
        community.addObserver(electoralRegulation);
    }

    public void registerCongregation(CongregationId congregationId, Iterable<MemberId> members) {
        community.registerCongregation(congregationId, members);
    }

    public void dissolveCongregation(CongregationId congregationId) {
        community.dissolveCongregation(congregationId);
    }

    public void changeMinimalElectionDuration(Duration duration) {
        electoralRegulation.changeMinimalElectionDuration(duration);
    }

    public void initiateElection(ElectionId electionId, CongregationId initiator) {
        electoralRegulation.initiateElection(electionId, initiator, clock);
    }

    public void completeElection(ElectionId electionId, CongregationId congregationId, Iterable<MemberId> electedMembers) {
        ElectoralRegulation.Election election = electoralRegulation.election(electionId).orElseThrow();
        election.complete(congregationId, electedMembers, clock);
    }
}