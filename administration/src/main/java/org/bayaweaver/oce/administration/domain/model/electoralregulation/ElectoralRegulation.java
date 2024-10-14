package org.bayaweaver.oce.administration.domain.model.electoralregulation;

import org.bayaweaver.oce.administration.domain.model.community.CongregationDissolvedEvent;
import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.community.MemberId;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;

public class ElectoralRegulation extends Observable implements Observer {
    private Duration minElectionDuration;
    private final Collection<Election> elections;

    public ElectoralRegulation() {
        this.minElectionDuration = Duration.ZERO;
        this.elections = new ArrayList<>();
    }

    public void changeMinimalElectionDuration(Duration duration) {
        this.minElectionDuration = duration;
    }

    public void initiateElection(ElectionId id, CongregationId congregationId, Clock clock) {
        Year currentYear = Year.now(clock);
        if (elections.stream()
                .filter(e -> e.year.equals(currentYear))
                .anyMatch(e -> e.initiator.equals(congregationId))) {
            throw new IllegalArgumentException("Выборы могут быть инициированы общиной только один раз в год.");
        }
        Election e = new Election(id, currentYear, congregationId, Instant.now(clock));
        elections.add(e);
        notifyObservers(new ElectionInitiatedEvent(congregationId));
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
        for (Election election : elections) {
            if (election.initiator.equals(event.congregation())) {
                election.close();
            }
        }
    }

    public class Election {
        private final ElectionId id;
        private final Year year;
        private final Instant start;
        private final CongregationId initiator;
        private final Set<MemberId> electedMembers;
        private boolean closed;

        private Election(ElectionId id, Year year, CongregationId initiator, Instant start) {
            this.id = id;
            this.year = year;
            this.initiator = initiator;
            this.start = start;
            this.electedMembers = new HashSet<>();
            this.closed = false;
        }

        public void complete(CongregationId congregationId, Iterable<MemberId> electedMembers, Clock clock) {
            if (closed) {
                throw new IllegalArgumentException("Закрытые выборы не могут быть завершены.");
            }
            Instant now = Instant.now(clock);
            if (start.plus(minElectionDuration).isAfter(now)) {
                throw new IllegalArgumentException(
                        "Выборы могут быть завершены не ранее, чем через установленное количество времени.");
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
