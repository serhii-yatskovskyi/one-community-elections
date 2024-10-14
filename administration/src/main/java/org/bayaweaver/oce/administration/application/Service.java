package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.community.Community;
import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectoralRegulation;

import java.time.Clock;

public class Service {
    private final Clock clock;
    private final ElectoralRegulation electoralRegulation;
    private final Community community;

    public Service() {
        clock = Clock.systemUTC();
        electoralRegulation = new ElectoralRegulation();
        community = new Community();
        electoralRegulation.addObserver(community);
    }

    public void initiateElection(ElectionId electionId, CongregationId initiator) {
        electoralRegulation.initiateElection(electionId, initiator, clock);
    }
}