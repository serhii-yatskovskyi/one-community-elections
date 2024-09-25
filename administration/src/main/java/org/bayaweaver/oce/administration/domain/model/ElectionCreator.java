package org.bayaweaver.oce.administration.domain.model;

public class ElectionCreator implements Entity {
    private final ElectionRepository repository;

    public ElectionCreator(ElectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void updateOn(Object o, EntitySynchronization sync) {
        if (o instanceof ElectionInitiatedEvent event) {
            Election e = new Election(event.id(), event.community());
            sync.involve(e);
            repository.add(e);
        }
    }
}
