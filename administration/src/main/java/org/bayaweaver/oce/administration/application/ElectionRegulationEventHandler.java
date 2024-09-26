package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.Election;
import org.bayaweaver.oce.administration.domain.model.ElectionFactory;
import org.bayaweaver.oce.administration.domain.model.ElectionInitiatedEvent;
import org.bayaweaver.oce.administration.domain.model.ElectionRepository;

class ElectionRegulationEventHandler implements EventHandler {
    private final EventBus eventBus;
    private final ElectionFactory electionFactory;
    private final ElectionRepository electionRepository;

    ElectionRegulationEventHandler(
            EventBus eventBus,
            ElectionFactory electionFactory,
            ElectionRepository electionRepository) {

        this.eventBus = eventBus;
        this.electionFactory = electionFactory;
        this.electionRepository = electionRepository;
    }

    @Override
    public void handle(Object generalEvent) {
        if (generalEvent instanceof ElectionInitiatedEvent event) {
            Election e = electionFactory.create(event);
            eventBus.subscribe(e);
            electionRepository.add(e);
        }
    }
}
