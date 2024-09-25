package org.bayaweaver.oce.administration.domain.model;

import org.bayaweaver.oce.administration.util.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BallotingCalendar {
    private static final BallotingCalendar instance = new BallotingCalendar();

    private final Collection<Election> elections;
    private final Set<CommunityId> electionRequesters;

    private BallotingCalendar() {
        this.elections = new ArrayList<>();
        this.electionRequesters = new HashSet<>();
    }

    public static BallotingCalendar instance() {
        return instance;
    }

    public Election election(ElectionId id) {
        for (Election election : this.elections) {
            if (election.id().equals(id)) {
                return election;
            }
        }
        throw new IllegalArgumentException("Election '" + id + "' is absent.");
    }

    Iterable<Election> electionsOf(CommunityId community) {
        Collection<Election> result = new ArrayList<>();
        for (Election election : this.elections) {
            if (election.community().equals(community)) {
                result.add(election);
            }
        }
        return result;
    }

    public void initiateElection(ElectionId id, CommunityId communityId) {
        if (this.electionRequesters.contains(communityId)) {
            throw new IllegalArgumentException("Only one election can be initiated per community.");
        }
        this.elections.add(new Election(id, communityId));
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
