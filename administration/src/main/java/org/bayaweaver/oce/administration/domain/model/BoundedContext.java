package org.bayaweaver.oce.administration.domain.model;

import java.time.Clock;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;

public class BoundedContext {
    private final Collection<Election> elections;

    public BoundedContext() {
        this.elections = new ArrayList<>();
    }

    public void initiateElection(ElectionId id, CongregationId congregation, Clock clock) {
        Year currentYear = Year.now(clock);
        if (elections.stream()
                .filter(e -> e.year.equals(currentYear))
                .anyMatch(e -> e.initiator.equals(congregation))) {
            throw new IllegalArgumentException("Выборы могут быть инициированы общиной только один раз в год.");
        }
        Election e = new Election(id, currentYear, congregation);
        elections.add(e);
    }

    public class Election {
        private final ElectionId id;
        private final Year year;
        private final CongregationId initiator;

        private Election(ElectionId id, Year year, CongregationId initiator) {
            this.id = id;
            this.year = year;
            this.initiator = initiator;
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
