package org.bayaweaver.oce.administration.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;

public class BallotingCalendar {
    private static final BallotingCalendar instance = new BallotingCalendar();

    private final Collection<Election> elections;
    private final Set<CommunityRegistry.Community> electionRequesters;

    private BallotingCalendar() {
        this.communities = new ArrayList<>();
        this.elections = new ArrayList<>();
        this.electionRequesters = new HashSet<>();
    }

    public static BallotingCalendar instance() {
        return instance;
    }

    public Election election(ElectionId id) {
        for (Election election : this.elections) {
            if (election.id.equals(id)) {
                return election;
            }
        }
        throw new IllegalArgumentException("Election '" + id + "' is absent.");
    }

    public void initiateElection(ElectionId id, CommunityId communityId) {
        CommunityRegistry.Community community = community(communityId);
        if (this.electionRequesters.contains(community)) {
            throw new IllegalArgumentException("Only one election can be initiated per community.");
        }
        this.elections.add(new Election(id, community));
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public class Election {
        private final ElectionId id;
        private final CommunityRegistry.Community initiator;
        private boolean canceled;
        private final Set<MemberId> electedMembers;

        private Election(ElectionId id, CommunityRegistry.Community initiator) {
            this.id = id;
            this.initiator = initiator;
            this.canceled = false;
            this.electedMembers = new HashSet<>();
        }

        private CommunityRegistry.Community community() {
            return initiator;
        }

        public void complete(Iterable<MemberId> electedMembers, CommunityId communityId) {
            if (this.canceled) {
                throw new IllegalArgumentException("A canceled election can not be completed.");
            }
            if (!this.electedMembers.isEmpty()) {
                throw new IllegalArgumentException("An election can be completed only once.");
            }
            CommunityRegistry.Community community = BallotingCalendar.this.community(communityId);
            if (!community.equals(initiator)) {
                throw new IllegalArgumentException("Only the community that initiated the election can complete it.");
            }
            if (!community.members.containsAll(StreamSupport.stream(electedMembers.spliterator(), false).toList())) {
                throw new IllegalArgumentException("Only members of a community can be elected.");
            }
            for (MemberId member : electedMembers) {
                this.electedMembers.add(member);
            }
        }

        public boolean canceled() {
            return canceled;
        }

        private void cancel() {
            BallotingCalendar.this.electionRequesters.remove(this.initiator);
            canceled = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Election that = (Election) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
