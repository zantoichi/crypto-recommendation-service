package com.xm.cryptorecommendationservice.configuration.clock;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
@Getter(AccessLevel.NONE)
public class ClockAdapter {

    private final Clock internalClock;

    public LocalDateTime now() {
        return LocalDateTime.now(internalClock);
    }

    public LocalDateTime startOfCurrentMonth() {
        return LocalDateTime.now(internalClock).withDayOfMonth(1).with(LocalTime.MIN);
    }

    public LocalDateTime endOfCurrentMonth() {
        return LocalDateTime.now(internalClock)
            .withDayOfMonth(1)
            .plusMonths(1)
            .minusDays(1)
            .with(LocalTime.MAX);
    }

    public LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    public LocalDateTime ofEpochMilli(long timestampMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), internalClock.getZone());
    }
}
