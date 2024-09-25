package org.bayaweaver.oce.administration.domain.model;

public final class ElectionCanceledEvent {
    private final Election election;

    ElectionCanceledEvent(Election election) {
        this.election = election;
    }

    Election election() {
        return election;
    }
}
