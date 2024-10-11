package org.bayaweaver.oce.administration.domain.model.community;

import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionCompletedEvent;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionInitiatedEvent;
import org.bayaweaver.oce.administration.util.Iterables;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class Community extends Observable implements Observer {
    private final Collection<Congregation> congregations;

    public Community() {
        this.congregations = new HashSet<>();
    }

    public void registerCongregation(CongregationId id, Iterable<MemberId> members) {
        Congregation congregation = new Congregation(id, members);
        if (!congregations.add(congregation)) {
            throw new IllegalArgumentException("Не может существовать нескольких общин с одинаковым идентификатором.");
        }
    }

    public void dissolveCongregation(CongregationId id) {
        Congregation congregation = congregations.stream()
                .filter(c -> c.id.equals(id))
                .findFirst()
                .orElse(null);
        if (congregation == null) {
            return;
        }
        congregations.remove(congregation);
        notifyObservers(new CongregationDissolvedEvent(id));
    }

    @Override
    public void notifyObservers(Object event) {
        setChanged();
        super.notifyObservers(event);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ElectionInitiatedEvent event) {
            handle(event);
        } else if (arg instanceof ElectionCompletedEvent event) {
            handle(event);
        }
    }

    private void handle(ElectionInitiatedEvent event) {
        CongregationId congregationId = event.initiator();
        Community.Congregation congregation = congregations.stream()
                .filter(c -> c.id.equals(congregationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Выборы может инициировать только зарегистрированная община."));
    }

    private void handle(ElectionCompletedEvent event) {
        Community.Congregation congregation = congregations.stream()
                .filter(c -> c.id.equals(event.completedBy()))
                .findFirst()
                .orElse(null);
        if (congregation == null) {
            throw new IllegalArgumentException("Завершить выборы может только та община, которая их инициировала.");
        }
        if (!Iterables.containsAll(congregation.members, event.electedMembers())) {
            throw new IllegalArgumentException("На выборах, инициированных определенной общиной,"
                    + " могут быть выбраны только члены этой общины.");
        }
    }

    public class Congregation {
        private final CongregationId id;
        private final Set<MemberId> members;

        private Congregation(CongregationId id, Iterable<MemberId> members) {
            this.id = id;
            this.members = new HashSet<>();
            for (MemberId member : members) {
                this.members.add(member);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Congregation that = (Congregation) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
