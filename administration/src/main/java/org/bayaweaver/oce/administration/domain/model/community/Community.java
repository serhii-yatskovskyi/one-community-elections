package org.bayaweaver.oce.administration.domain.model.community;

import java.util.HashSet;
import java.util.Set;

public class Community {
    private final Set<Congregation> congregations;

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
        for (Election election : elections) {
            if (election.initiator.equals(congregation)) {
                election.close();
            }
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
