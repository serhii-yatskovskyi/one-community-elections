package org.bayaweaver.oce.administration.domain.model;

public class ElectionFactory {

    public Election create(ElectionInitiatedEvent event) {
        return new Election(event.election(), event.initiator());
    }
}
