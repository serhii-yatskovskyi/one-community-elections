package org.bayaweaver.oce.administration.domain.model;

public class BoundedContextRepository {
    private final BoundedContext singleEntity;

    public BoundedContextRepository() {
        singleEntity = new BoundedContext();
    }

    public BoundedContext get() {
        return singleEntity;
    }
}
