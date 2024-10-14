package org.bayaweaver.oce.administration.domain.model;

import org.bayaweaver.oce.administration.util.Iterables;

import java.time.Clock;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BoundedContext {
    private final Collection<Election> elections;
    private final Set<Congregation> congregations;

    public BoundedContext() {
        this.elections = new ArrayList<>();
        this.congregations = new HashSet<>();
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
                .anyMatch(e -> e.initiator.equals(congregation))) {
            throw new IllegalArgumentException("Выборы могут быть инициированы общиной только один раз в год.");
        }
        Election e = new Election(id, currentYear, congregation);
        elections.add(e);
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
        for (Election election : elections) {
            if (election.initiator.equals(congregation)) {
                election.close();
            }
        }
    }

    public Optional<Election> election(ElectionId id) {
        return elections.stream()
                .filter(e -> e.id.equals(id))
                .findFirst();
    }

    public class Election {
        private final ElectionId id;
        private final Year year;
        private final Congregation initiator;
        private final Set<MemberId> electedMembers;
        private boolean closed;

        private Election(ElectionId id, Year year, Congregation initiator) {
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
            Congregation congregation = BoundedContext.this.congregations.stream()
                    .filter(c -> c.id.equals(congregationId))
                    .findFirst()
                    .orElse(null);
            if (congregation == null || !congregation.equals(initiator)) {
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
