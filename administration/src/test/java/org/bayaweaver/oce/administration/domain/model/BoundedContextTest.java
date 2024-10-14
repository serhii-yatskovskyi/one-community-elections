package org.bayaweaver.oce.administration.domain.model;

import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.community.MemberId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoundedContextTest {

    @Test
    void secondElectionInitiationProhibited() {
        var c = new BoundedContext();
        final var congregationId = new CongregationId(100);
        c.registerCongregation(congregationId, List.of());
        var clock = Clock.systemUTC();
        c.initiateElection(new ElectionId(1), congregationId, clock);
        assertThrows(
                IllegalArgumentException.class,
                () -> c.initiateElection(new ElectionId(2), congregationId, clock));
    }

    @Test
    void nextYearElectionInitiationAllowed() {
        var c = new BoundedContext();
        final var congregationId = new CongregationId(100);
        c.registerCongregation(congregationId, List.of());
        var time1 = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        c.initiateElection(new ElectionId(1), congregationId, time1);
        var time2 = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), ZoneOffset.UTC);
        assertDoesNotThrow(() -> c.initiateElection(new ElectionId(2), congregationId, time2));
    }

    @Test
    void electionInitiationByNonRegisteredCongregation() {
        var c = new BoundedContext();
        var congregationId1 = new CongregationId(100);
        var congregationId2 = new CongregationId(200);
        c.registerCongregation(congregationId1, List.of());
        var electionId = new ElectionId(1);
        assertThrows(
                IllegalArgumentException.class,
                () -> c.initiateElection(electionId, congregationId2, Clock.systemUTC()));
    }

    @Test
    void electionCompletion() {
        var c = new BoundedContext();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        var member2 = new MemberId(2);
        c.registerCongregation(congregationId, List.of(member1, member2));
        var electionId = new ElectionId(1);
        c.initiateElection(electionId, congregationId, Clock.systemUTC());
        assertDoesNotThrow(() -> c.election(electionId).get().complete(congregationId, List.of(member1), Clock.systemUTC()));
    }

    @Test
    void electionCompletionWithWrongCandidates() {
        var c = new BoundedContext();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        var member2 = new MemberId(2);
        c.registerCongregation(congregationId, List.of(member1));
        var electionId = new ElectionId(1);
        c.initiateElection(electionId, congregationId, Clock.systemUTC());
        assertThrows(
                IllegalArgumentException.class,
                () -> c.election(electionId).get().complete(congregationId, List.of(member2), Clock.systemUTC()));
    }

    @Test
    void electionSecondCompletion() {
        var c = new BoundedContext();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        var member2 = new MemberId(2);
        c.registerCongregation(congregationId, List.of(member1, member2));
        var electionId = new ElectionId(1);
        c.initiateElection(electionId, congregationId, Clock.systemUTC());
        c.election(electionId).get().complete(congregationId, List.of(member1), Clock.systemUTC());
        assertThrows(
                IllegalArgumentException.class,
                () -> c.election(electionId).get().complete(congregationId, List.of(member2), Clock.systemUTC()));
    }

    @Test
    void closeElectionOnCongregationDissolution() {
        var c = new BoundedContext();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        c.registerCongregation(congregationId, List.of(member1));
        var electionId = new ElectionId(1);
        c.initiateElection(electionId, congregationId, Clock.systemUTC());
        c.dissolveCongregation(congregationId);
        assertThrows(
                IllegalArgumentException.class,
                () -> c.election(electionId).get().complete(congregationId, List.of(member1), Clock.systemUTC()));
    }

    @Test
    void setMinimalElectionDuration() {
        var c = new BoundedContext();
        final var congregationId = new CongregationId(100);
        c.registerCongregation(congregationId, List.of());
        c.changeMinimalElectionDuration(Duration.of(10, ChronoUnit.DAYS));
        var time1 = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), ZoneOffset.UTC);
        var electionId = new ElectionId(1);
        c.initiateElection(electionId, congregationId, time1);
        assertThrows(
                IllegalArgumentException.class,
                () -> c.election(electionId).get().complete(congregationId, List.of(), time1));
        var time2 = Clock.fixed(Instant.parse("2021-01-11T00:00:00Z"), ZoneOffset.UTC);
        assertDoesNotThrow(() -> c.election(electionId).get().complete(congregationId, List.of(), time2));
    }
}