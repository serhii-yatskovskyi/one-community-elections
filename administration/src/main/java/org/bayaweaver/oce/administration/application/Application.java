package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.CommunityDissolvedEvent;
import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.CommunityProvider;
import org.bayaweaver.oce.administration.domain.model.CommunityRegistry;
import org.bayaweaver.oce.administration.domain.model.CommunityRegistryRepository;
import org.bayaweaver.oce.administration.domain.model.Election;
import org.bayaweaver.oce.administration.domain.model.ElectionFactory;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.ElectionInitiatedEvent;
import org.bayaweaver.oce.administration.domain.model.ElectionRepository;
import org.bayaweaver.oce.administration.domain.model.ElectoralRegulation;
import org.bayaweaver.oce.administration.domain.model.ElectoralRegulationRepository;
import org.bayaweaver.oce.administration.domain.model.MemberId;

public class Application {
    private final EventBus eventBus;
    private final ElectoralRegulationRepository electoralRegulationRepository;
    private final CommunityRegistryRepository communityRegistryRepository;
    private final ElectionRepository electionRepository;

    public Application() {
        eventBus = new EventBus();
        electoralRegulationRepository = new ElectoralRegulationRepository();
        eventBus.subscribe(electoralRegulationRepository.get());
        communityRegistryRepository = new CommunityRegistryRepository();
        electionRepository = new ElectionRepository();
        eventBus.subscribe(new ElectionRegulationEventHandler(
                eventBus,
                new ElectionFactory(),
                electionRepository));
    }

    public void registerCommunity(CommunityId communityId) {
        CommunityRegistry registry = communityRegistryRepository.get();
        registry.registerCommunity(communityId);
    }

    public void registerMember(MemberId memberId, CommunityId communityId) {
        CommunityRegistry registry = communityRegistryRepository.get();
        CommunityRegistry.Community community = registry.community(communityId);
        community.registerMember(memberId);
    }

    public void dissolveCommunity(CommunityId communityId) {
        CommunityRegistry registry = communityRegistryRepository.get();
        CommunityDissolvedEvent result = registry.dissolveCommunity(communityId);
        eventBus.notifySubscribers(result);
    }

    public void initiateElection(ElectionId electionId, CommunityId communityId) {
        ElectoralRegulation regulation = electoralRegulationRepository.get();
        ElectionInitiatedEvent result = regulation.initiateElection(electionId, communityId);
        eventBus.notifySubscribers(result);
    }

    public void completeElection(CommunityId communityId, Iterable<MemberId> electedMembers) {
        Election election = electionRepository.stream()
                .filter(e -> e.community().equals(communityId))
                .findFirst()
                .orElseThrow();
        CommunityProvider communityProvider = communityRegistryRepository.get();
        election.complete(electedMembers, communityId, communityProvider);
    }

    public boolean electionCanceled(ElectionId electionId) {
        Election election = electionRepository.stream()
                .filter(e -> e.id().equals(electionId))
                .findFirst()
                .orElseThrow();
        return election.canceled();
    }
}
