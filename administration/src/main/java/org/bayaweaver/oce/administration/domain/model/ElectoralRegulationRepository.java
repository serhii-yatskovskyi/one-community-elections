package org.bayaweaver.oce.administration.domain.model;

public class ElectoralRegulationRepository {
    private final ElectoralRegulation singleEntity;

    public ElectoralRegulationRepository() {
        singleEntity = new ElectoralRegulation();
    }

    public ElectoralRegulation get() {
        return singleEntity;
    }
}
