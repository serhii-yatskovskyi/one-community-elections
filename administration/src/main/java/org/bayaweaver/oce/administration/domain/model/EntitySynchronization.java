package org.bayaweaver.oce.administration.domain.model;

public interface EntitySynchronization {

    void trigger(Object event);
    void involve(Entity entity);
}
