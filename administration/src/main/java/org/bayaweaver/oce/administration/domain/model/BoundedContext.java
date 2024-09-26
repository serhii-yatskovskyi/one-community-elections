package org.bayaweaver.oce.administration.domain.model;

import org.bayaweaver.oce.administration.util.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BoundedContext {
    private final Collection<Community> communities;
    private final Collection<Election> elections;
    private final Set<Community> electionInitiators;

    BoundedContext() {
        this.communities = new ArrayList<>();
        this.elections = new ArrayList<>();
        this.electionInitiators = new HashSet<>();
    }

    public Community community(CommunityId id) {
        for (Community community : this.communities) {
            if (community.id().equals(id)) {
                return community;
            }
        }
        throw new IllegalArgumentException("Community '" + id + "' is absent.");
    }

    public void registerCommunity(CommunityId id) {
        this.communities.add(new Community(id));
    }

    public void dissolveCommunity(CommunityId id) {
        Community community = community(id);
        this.communities.remove(community);
        for (Election election : this.elections) {
            if (election.community().equals(community)) {
                election.cancel();
            }
        }
        returnAvailabilityToInitiateElections(community);
    }

    public Iterable<Election> elections() {
        return elections;
    }

    public void initiateElection(ElectionId id, CommunityId communityId) {
        Community community = community(communityId);
        if (this.electionInitiators.contains(community)) {
            throw new IllegalArgumentException("Only one election can be initiated per community.");
        }
        suppressAvailabilityToInitiateElections(community);
        this.elections.add(new Election(id, community));
    }

    private void suppressAvailabilityToInitiateElections(Community community) {
        this.electionInitiators.add(community);
    }

    private void returnAvailabilityToInitiateElections(Community community) {
        this.electionInitiators.remove(community);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public class Community {
        private final CommunityId id;
        private final Set<MemberId> members;

        private Community(CommunityId id) {
            this.id = id;
            this.members = new HashSet<>();
        }

        public CommunityId id() {
            return id;
        }

        public Iterable<MemberId> members() {
            return members;
        }

        public void registerMember(MemberId member) {
            for (Community community : communities) {
                if (community.equals(this)) {
                    continue;
                }
                for (MemberId alreadyRegistered : community.members) {
                    if (alreadyRegistered.equals(member)) {
                        throw new IllegalArgumentException("A member can be registered in only one community.");
                    }
                }
            }
            this.members.add(member);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Community that = (Community) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public class Election {
        private final ElectionId id;
        private final Community initiator;
        private boolean canceled;
        private final Set<MemberId> electedMembers;

        private Election(ElectionId id, Community initiator) {
            this.id = id;
            this.initiator = initiator;
            this.canceled = false;
            this.electedMembers = new HashSet<>();
        }

        public ElectionId id() {
            return id;
        }

        public Community community() {
            return initiator;
        }

        public boolean canceled() {
            return canceled;
        }

        public void complete(Iterable<MemberId> electedMembers, CommunityId communityId) {
            if (this.canceled) {
                throw new IllegalArgumentException("A canceled election can not be completed.");
            }
            if (!this.electedMembers.isEmpty()) {
                throw new IllegalArgumentException("An election can be completed only once.");
            }
            Community community = BoundedContext.this.community(communityId);
            if (!community.equals(initiator)) {
                throw new IllegalArgumentException("Only the community that initiated the election can complete it.");
            }
            if (!Iterables.containsAll(community.members(), electedMembers)) {
                throw new IllegalArgumentException("Only members of a community can be elected.");
            }
            for (MemberId member : electedMembers) {
                this.electedMembers.add(member);
            }
        }

        private void cancel() {
            canceled = true;
            BoundedContext.this.returnAvailabilityToInitiateElections(initiator);
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
