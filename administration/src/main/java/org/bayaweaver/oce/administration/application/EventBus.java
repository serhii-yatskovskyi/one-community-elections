package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.Entity;
import org.bayaweaver.oce.administration.domain.model.EventEmitter;

import java.util.Observable;

class EventBus extends Observable implements EventEmitter {

    EventBus() {}

    void subscribe(Entity entity) {
        addObserver((o, event) -> {
            entity.updateOn(event, this);
        });
    }

    void subscribe(EventHandler handler) {
        addObserver((o, event) -> handler.handle(event));
    }

    @Override
    public void emit(Object event) {
        setChanged();
        notifyObservers(event);
    }
}
