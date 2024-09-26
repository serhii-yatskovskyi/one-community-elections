package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.BoundedContext;
import org.bayaweaver.oce.administration.domain.model.BoundedContextRepository;
import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.MemberId;
import org.bayaweaver.oce.administration.util.Iterables;

public class ApplicationInterface {
    private final BoundedContextRepository boundedContextRepository;

    ApplicationInterface(BoundedContextRepository boundedContextRepository) {
        this.boundedContextRepository = boundedContextRepository;
    }

    public void registerCommunity(CommunityId communityId) {
        BoundedContext context = boundedContextRepository.get();
        context.registerCommunity(communityId);
    }

    public void registerMember(MemberId memberId, CommunityId communityId) {
        BoundedContext context = boundedContextRepository.get();
        BoundedContext.Community community = context.community(communityId);
        community.registerMember(memberId);
    }

    public void dissolveCommunity(CommunityId communityId) {
        BoundedContext context = boundedContextRepository.get();
        context.dissolveCommunity(communityId);
    }

    public void initiateElection(ElectionId electionId, CommunityId communityId) {
        BoundedContext context = boundedContextRepository.get();
        context.initiateElection(electionId, communityId);
    }

    public void completeElection(CommunityId communityId, Iterable<MemberId> electedMembers) {
        BoundedContext context = boundedContextRepository.get();
        BoundedContext.Election election = Iterables.stream(context.elections())
                .filter(e -> e.community().id().equals(communityId))
                .findFirst()
                .orElseThrow();
        election.complete(electedMembers, communityId);
    }

    public boolean electionCanceled(ElectionId electionId) {
        BoundedContext context = boundedContextRepository.get();
        BoundedContext.Election election = Iterables.stream(context.elections())
                .filter(e -> e.id().equals(electionId))
                .findFirst()
                .orElseThrow();
        return election.canceled();
    }
}
