package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.CommunityId;
import org.bayaweaver.oce.administration.domain.model.CommunityProvider;
import org.bayaweaver.oce.administration.domain.model.CommunityRegistry;
import org.bayaweaver.oce.administration.domain.model.CommunityRegistryRepository;
import org.bayaweaver.oce.administration.domain.model.Election;
import org.bayaweaver.oce.administration.domain.model.ElectionCreator;
import org.bayaweaver.oce.administration.domain.model.ElectionId;
import org.bayaweaver.oce.administration.domain.model.ElectionRepository;
import org.bayaweaver.oce.administration.domain.model.ElectoralRegulation;
import org.bayaweaver.oce.administration.domain.model.ElectoralRegulationRepository;
import org.bayaweaver.oce.administration.domain.model.MemberId;
import org.bayaweaver.oce.administration.domain.model.Entity;
import org.bayaweaver.oce.administration.domain.model.EntitySynchronization;

import java.util.Observable;

public class Application {
    private EntitySynchronization sync;
    private ElectoralRegulationRepository electoralRegulationRepository;
    private CommunityRegistryRepository communityRegistryRepository;
    private ElectionRepository electionRepository;

    public Application() {}

    public void init() {
        sync = new SimpleSynchronization();
        electoralRegulationRepository = new ElectoralRegulationRepository();
        sync.involve(electoralRegulationRepository.get());
        communityRegistryRepository = new CommunityRegistryRepository();
        electionRepository = new ElectionRepository();
        sync.involve(new ElectionCreator(electionRepository));
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
        registry.dissolveCommunity(communityId, sync);
    }

    public void initiateElection(ElectionId electionId, CommunityId communityId) {
        ElectoralRegulation regulation = electoralRegulationRepository.get();
        regulation.initiateElection(electionId, communityId, sync);
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

    public static class SimpleSynchronization extends Observable implements EntitySynchronization {

        private SimpleSynchronization() {}

        @Override
        public void trigger(Object event) {
            setChanged();
            notifyObservers(event);
        }

        @Override
        public void involve(Entity entity) {
            addObserver((observable, event) -> entity.updateOn(event, (SimpleSynchronization) observable));
        }
    }
}
