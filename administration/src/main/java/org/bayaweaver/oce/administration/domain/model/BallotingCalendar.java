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
            if (election.id.equals(id)) {
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

    public class Election {
        private final ElectionId id;
        private final CommunityId initiator;
        private boolean canceled;
        private final Set<MemberId> electedMembers;

        private Election(ElectionId id, CommunityId initiator) {
            this.id = id;
            this.initiator = initiator;
            this.canceled = false;
            this.electedMembers = new HashSet<>();
        }

        CommunityId community() {
            return initiator;
        }

        public boolean canceled() {
            return canceled;
        }

        public void complete(Iterable<MemberId> electedMembers, CommunityId communityId, CommunityProvider communityProvider) {
            if (this.canceled) {
                throw new IllegalArgumentException("A canceled election can not be completed.");
            }
            if (!this.electedMembers.isEmpty()) {
                throw new IllegalArgumentException("An election can be completed only once.");
            }
            if (!communityId.equals(initiator)) {
                throw new IllegalArgumentException("Only the community that initiated the election can complete it.");
            }
            CommunityRegistry.Community community = communityProvider.community(communityId);
            if (!Iterables.containsAll(community.members(), electedMembers)) {
                throw new IllegalArgumentException("Only members of a community can be elected.");
            }
            for (MemberId member : electedMembers) {
                this.electedMembers.add(member);
            }
        }

        void cancel() {
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
