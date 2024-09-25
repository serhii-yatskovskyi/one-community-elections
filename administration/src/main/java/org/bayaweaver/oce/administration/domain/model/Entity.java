package org.bayaweaver.oce.administration.domain.model;

public interface Entity {

    void updateOn(Object event, EntitySynchronization sync);
}
