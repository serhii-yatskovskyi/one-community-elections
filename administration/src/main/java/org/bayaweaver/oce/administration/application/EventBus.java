package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.Entity;

import java.util.Observable;

class EventBus extends Observable {

    EventBus() {}

    void notifySubscribers(Object event) {
        setChanged();
        notifyObservers(event);
    }

    void subscribe(Entity entity) {
        addObserver((o, event) -> {
            Object result = entity.updateOn(event);
            if (result != null) {
                notifySubscribers(result);
            }
        });
    }

    void subscribe(EventHandler handler) {
        addObserver((o, event) -> handler.handle(event));
    }
}
