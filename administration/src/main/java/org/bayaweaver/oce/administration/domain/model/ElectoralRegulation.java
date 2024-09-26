package org.bayaweaver.oce.administration.domain.model;

import java.util.HashSet;
import java.util.Set;

public class ElectoralRegulation implements Entity {
    private final Set<CommunityId> electionInitiators;

    ElectoralRegulation() {
        this.electionInitiators = new HashSet<>();
    }

    public ElectionInitiatedEvent initiateElection(ElectionId id, CommunityId communityId) {
        if (this.electionInitiators.contains(communityId)) {
            throw new IllegalArgumentException("Only one election can be initiated per community.");
        }
        suppressAvailabilityToInitiateElections(communityId);
        return new ElectionInitiatedEvent(id, communityId);
    }

    private void suppressAvailabilityToInitiateElections(CommunityId community) {
        this.electionInitiators.add(community);
    }

    private void returnAvailabilityToInitiateElections(CommunityId community) {
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

    @Override
    public Object updateOn(Object generalEvent) {
        if (generalEvent instanceof ElectionCanceledEvent event) {
            returnAvailabilityToInitiateElections(event.community());
        }
        return null;
    }
}
