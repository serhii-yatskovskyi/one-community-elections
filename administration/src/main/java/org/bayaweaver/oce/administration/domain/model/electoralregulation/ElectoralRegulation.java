package org.bayaweaver.oce.administration.domain.model.electoralregulation;

import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.community.MemberId;
import org.bayaweaver.oce.administration.util.Iterables;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ElectoralRegulation {
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
        Congregation congregation = congregations.stream()
                .filter(c -> c.id.equals(congregationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Выборы может инициировать только зарегистрированная община."));
        if (elections.stream()
                .filter(e -> e.year.equals(currentYear))
                .anyMatch(e -> e.initiator.equals(congregationId))) {
            throw new IllegalArgumentException("Выборы могут быть инициированы общиной только один раз в год.");
        }
        Election e = new Election(id, currentYear, congregationId, Instant.now(clock));
        elections.add(e);
    }

    public Optional<Election> election(ElectionId id) {
        return elections.stream()
                .filter(e -> e.id.equals(id))
                .findFirst();
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
            Congregation congregation = BoundedContext.this.congregations.stream()
                    .filter(c -> c.id.equals(congregationId))
                    .findFirst()
                    .orElse(null);
            if (congregation == null || !congregationId.equals(initiator)) {
                throw new IllegalArgumentException("Завершить выборы может только та община, которая их инициировала.");
            }
            if (!Iterables.containsAll(congregation.members, electedMembers)) {
                throw new IllegalArgumentException("На выборах, инициированных определенной общиной,"
                        + " могут быть выбраны только члены этой общины.");
            }
            for (MemberId member : electedMembers) {
                this.electedMembers.add(member);
            }
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
