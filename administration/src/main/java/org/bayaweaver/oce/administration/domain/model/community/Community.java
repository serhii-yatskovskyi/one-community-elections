package org.bayaweaver.oce.administration.domain.model.community;

import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionInitiatedEvent;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectoralRegulation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class Community implements Observer {
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
        for (ElectoralRegulation.Election election : elections) {
            if (election.initiator.equals(congregation)) {
                election.close();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ElectionInitiatedEvent event) {
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
