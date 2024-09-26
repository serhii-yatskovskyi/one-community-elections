package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.BoundedContextRepository;

public class Application {
    private final ApplicationInterface api;

    public Application() {
        this.api = new ApplicationInterface(new BoundedContextRepository());
    }

    public ApplicationInterface api() {
        return api;
    }
}
