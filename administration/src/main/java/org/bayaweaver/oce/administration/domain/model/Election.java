package org.bayaweaver.oce.administration.domain.model;

import org.bayaweaver.oce.administration.util.Iterables;

import java.util.HashSet;
import java.util.Set;

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

    ElectionId id() {
        return id;
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
