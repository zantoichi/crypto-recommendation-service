package com.xm.cryptorecommendationservice.configuration.clock;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ClockAdapterTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2023-07-01T12:00:00Z");
    private static final long FIXED_TIMESTAMP_MILLIS = FIXED_INSTANT.toEpochMilli();

    private final ClockAdapter clockAdapterUnderTest = new ClockAdapter(Clock.fixed(FIXED_INSTANT,
        ZoneOffset.UTC));

    @Nested
    class Now {

        @Test
        void shouldReturnFixedDateTime() {
            // when
            LocalDateTime currentDateTime = clockAdapterUnderTest.now();

            // then
            assertThat(currentDateTime).isEqualTo(LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC));
        }
    }

    @Nested
    class StartOfMonth {

        @Test
        void shouldReturnStartOfMonthWithTimeSetToMidnight() {
            // when
            LocalDateTime startOfMonth = clockAdapterUnderTest.startOfCurrentMonth();

            // then
            assertThat(startOfMonth).isEqualTo(LocalDateTime.parse("2023-07-01T00:00:00"));
        }
    }

    @Nested
    class EndOfMonth {

        @Test
        void shouldReturnEndOfMonthWithTimeSetToLastMomentOfTheDay() {
            // when
            LocalDateTime endOfMonth = clockAdapterUnderTest.endOfCurrentMonth();

            // then
            assertThat(endOfMonth).isEqualTo(LocalDateTime.parse("2023-07-31T23:59:59.999999999"));
        }
    }

    @Nested
    class OfEpochMilli {

        @Test
        void shouldReturnDateTimeFromEpochMillis() {
            // when
            LocalDateTime dateTime = clockAdapterUnderTest.ofEpochMilli(FIXED_TIMESTAMP_MILLIS);

            // then
            assertThat(dateTime).isEqualTo(LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC));
        }
    }
}
