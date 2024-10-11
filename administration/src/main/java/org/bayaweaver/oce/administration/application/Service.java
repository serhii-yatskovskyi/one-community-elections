package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.community.Community;
import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionRegulation;

import java.time.Clock;

public class Service {
    private final Clock clock;
    private final ElectionRegulation electionRegulation;
    private final Community community;

    public Service() {
        clock = Clock.systemUTC();
        electionRegulation = new ElectionRegulation();
        community = new Community();
        electionRegulation.addObserver(community);
    }

    public void initiateElection(ElectionId electionId, CongregationId initiator) {
        electionRegulation.initiateElection(electionId, initiator, clock);
    }
}
