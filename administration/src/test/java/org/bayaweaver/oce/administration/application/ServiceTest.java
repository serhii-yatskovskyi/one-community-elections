package org.bayaweaver.oce.administration.application;

import org.bayaweaver.oce.administration.domain.model.community.CongregationId;
import org.bayaweaver.oce.administration.domain.model.community.MemberId;
import org.bayaweaver.oce.administration.domain.model.electoralregulation.ElectionId;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode.TOP_DOWN;

class ServiceTest {

    @Test
    void secondElectionInitiationProhibited() {
        var s = new Service();
        final var congregationId = new CongregationId(100);
        s.registerCongregation(congregationId, List.of());
        s.initiateElection(new ElectionId(1), congregationId);
        assertThrows(
                IllegalArgumentException.class,
                () -> s.initiateElection(new ElectionId(2), congregationId));
    }

    @Test
    void nextYearElectionInitiationAllowed() {
        var s = new Service();
        final var congregationId = new CongregationId(100);
        s.registerCongregation(congregationId, List.of());
        var time1 = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        replaceClock(s, time1);
        s.initiateElection(new ElectionId(1), congregationId);
        var time2 = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), ZoneOffset.UTC);
        replaceClock(s, time2);
        assertDoesNotThrow(() -> s.initiateElection(new ElectionId(2), congregationId));
    }

    @Test
    void electionInitiationByNonRegisteredCongregation() {
        var s = new Service();
        var congregationId1 = new CongregationId(100);
        var congregationId2 = new CongregationId(200);
        s.registerCongregation(congregationId1, List.of());
        var electionId = new ElectionId(1);
        assertThrows(
                IllegalArgumentException.class,
                () -> s.initiateElection(electionId, congregationId2));
    }

    @Test
    void electionCompletion() {
        var s = new Service();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        var member2 = new MemberId(2);
        s.registerCongregation(congregationId, List.of(member1, member2));
        var electionId = new ElectionId(1);
        s.initiateElection(electionId, congregationId);
        assertDoesNotThrow(() -> s.completeElection(electionId, congregationId, List.of(member1)));
    }

    @Test
    void electionCompletionWithWrongCandidates() {
        var s = new Service();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        var member2 = new MemberId(2);
        s.registerCongregation(congregationId, List.of(member1));
        var electionId = new ElectionId(1);
        s.initiateElection(electionId, congregationId);
        assertThrows(
                IllegalArgumentException.class,
                () -> s.completeElection(electionId, congregationId, List.of(member2)));
    }

    @Test
    void electionSecondCompletion() {
        var s = new Service();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        var member2 = new MemberId(2);
        s.registerCongregation(congregationId, List.of(member1, member2));
        var electionId = new ElectionId(1);
        s.initiateElection(electionId, congregationId);
        s.completeElection(electionId, congregationId, List.of(member1));
        assertThrows(
                IllegalArgumentException.class,
                () -> s.completeElection(electionId, congregationId, List.of(member2)));
    }

    @Test
    void closeElectionOnCongregationDissolution() {
        var s = new Service();
        var congregationId = new CongregationId(100);
        var member1 = new MemberId(1);
        s.registerCongregation(congregationId, List.of(member1));
        var electionId = new ElectionId(1);
        s.initiateElection(electionId, congregationId);
        s.dissolveCongregation(congregationId);
        assertThrows(
                IllegalArgumentException.class,
                () -> s.completeElection(electionId, congregationId, List.of(member1)));
    }

    @Test
    void setMinimalElectionDuration() {
        var s = new Service();
        final var congregationId = new CongregationId(100);
        s.registerCongregation(congregationId, List.of());
        s.changeMinimalElectionDuration(Duration.of(10, ChronoUnit.DAYS));
        var time1 = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), ZoneOffset.UTC);
        replaceClock(s, time1);
        var electionId = new ElectionId(1);
        s.initiateElection(electionId, congregationId);
        assertThrows(
                IllegalArgumentException.class,
                () -> s.completeElection(electionId, congregationId, List.of()));
        var time2 = Clock.fixed(Instant.parse("2021-01-11T00:00:00Z"), ZoneOffset.UTC);
        replaceClock(s, time2);
        assertDoesNotThrow(() -> s.completeElection(electionId, congregationId, List.of()));
    }

    private void replaceClock(Service s, Clock clock) {
        try {
            Field field = ReflectionUtils
                    .findFields(Service.class, f -> f.getName().equals("clock"), TOP_DOWN)
                    .getFirst();
            field.setAccessible(true);
            field.set(s, clock);
        } catch (NoSuchElementException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
