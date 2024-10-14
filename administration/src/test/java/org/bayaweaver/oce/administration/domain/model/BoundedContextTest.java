package org.bayaweaver.oce.administration.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoundedContextTest {

    @Test
    void secondElectionInitiationProhibited() {
        var c = new BoundedContext();
        final var congregationId = new CongregationId(100);
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
        var time1 = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        c.initiateElection(new ElectionId(1), congregationId, time1);
        var time2 = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), ZoneOffset.UTC);
        assertDoesNotThrow(() -> c.initiateElection(new ElectionId(2), congregationId, time2));
    }
}