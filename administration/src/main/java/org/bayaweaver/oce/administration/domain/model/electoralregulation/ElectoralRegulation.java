package org.bayaweaver.oce.administration.domain.model.electoralregulation;

import org.bayaweaver.oce.administration.domain.model.community.Community;
import org.bayaweaver.oce.administration.domain.model.community.CongregationDissolvedEvent;
import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.community.MemberId;

import java.time.Clock;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;

public class ElectoralRegulation extends Observable implements Observer {
    private final Collection<Election> elections;

    public ElectoralRegulation() {
        this.elections = new ArrayList<>();
    }

    public void initiateElection(ElectionId id, CongregationId congregationId, Clock clock) {
        Year currentYear = Year.now(clock);
        Community.Congregation congregation = congregations.stream()
                .filter(c -> c.id.equals(congregationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Выборы может инициировать только зарегистрированная община."));
        if (elections.stream()
                .filter(e -> e.year.equals(currentYear))
                .anyMatch(e -> e.initiator.equals(congregation))) {

            throw new IllegalArgumentException("Выборы могут быть инициированы общиной только один раз в год.");
        }
        Election e = new Election(id, currentYear, congregationId);
        elections.add(e);
    }

    public Optional<Election> election(ElectionId id) {
        return elections.stream()
                .filter(e -> e.id.equals(id))
                .findFirst();
    }

    @Override
    public void notifyObservers(Object event) {
        setChanged();
        super.notifyObservers(event);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof CongregationDissolvedEvent event) {
            handle(event);
        }
    }

    private void handle(CongregationDissolvedEvent event) {
        for (ElectoralRegulation.Election election : elections) {
            if (election.initiator.equals(event.congregation())) {
                election.close();
            }
        }
    }

    public class Election {
        private final ElectionId id;
        private final Year year;
        private final CongregationId initiator;
        private final Set<MemberId> electedMembers;
        private boolean closed;

        private Election(ElectionId id, Year year, CongregationId initiator) {
            this.id = id;
            this.year = year;
            this.initiator = initiator;
            this.electedMembers = new HashSet<>();
            this.closed = false;
        }

        public void complete(CongregationId congregationId, Iterable<MemberId> electedMembers) {
            if (closed) {
                throw new IllegalArgumentException("Закрытые выборы не могут быть завершены.");
            }
            if (!this.electedMembers.isEmpty()) {
                throw new IllegalArgumentException("Завершенные выборы не могут быть завершены повторно.");
            }
            if (!congregationId.equals(initiator)) {
                throw new IllegalArgumentException("Завершить выборы может только та община, которая их инициировала.");
            }
            for (MemberId member : electedMembers) {
                this.electedMembers.add(member);
            }
            ElectoralRegulation.this.notifyObservers(new ElectionCompletedEvent(congregationId, electedMembers));
        }

        private void close() {
            closed = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Election that = (Election) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
