package com.xm.cryptorecommendationservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.xm.cryptorecommendationservice.configuration.clock.ClockAdapter;
import com.xm.cryptorecommendationservice.domain.Crypto;
import com.xm.cryptorecommendationservice.domain.CryptoPriceSnapshot;
import com.xm.cryptorecommendationservice.domain.CryptoStatistics;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import com.xm.cryptorecommendationservice.infrastructure.CryptoCurrencyRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CryptoStatisticsServiceTest {

    @Mock
    private ClockAdapter clock;
    @Mock
    private CryptoCurrencyRepository cryptoCurrencyRepository;

    @InjectMocks
    private CryptoStatisticsService serviceUnderTest;

    @Nested
    @DisplayName("calculateStatisticsForCurrentMonthForAllCrypto Tests")
    class CalculateStatisticsForCurrentMonthTests {

        @Test
        @DisplayName("Given cryptos with price snapshots within current month, when calculateStatisticsForCurrentMonthForAllCrypto, then return CryptoStatistics set")
        void givenCryptosWithPriceSnapshotsWithinCurrentMonth_whenCalculateStatisticsForCurrentMonth_thenReturnCryptoStatisticsSet() {
            // Given
            var startOfCurrentMonth = LocalDateTime.of(2023, 7, 1, 0, 0);
            var endOfCurrentMonth = LocalDateTime.of(2023, 7, 31, 23, 59);

            var btc = new CryptoSymbol("BTC");
            var eth = new CryptoSymbol("ETH");

            var crypto1 = createCryptoWithPriceSnapshots(btc, startOfCurrentMonth.plusDays(1),
                endOfCurrentMonth.minusDays(1));
            var crypto2 = createCryptoWithPriceSnapshots(eth, startOfCurrentMonth.plusHours(6),
                endOfCurrentMonth.minusHours(6));

            when(clock.startOfCurrentMonth()).thenReturn(startOfCurrentMonth);
            when(clock.endOfCurrentMonth()).thenReturn(endOfCurrentMonth);
            when(cryptoCurrencyRepository.loadCryptoCurrencyPrices()).thenReturn(Stream.of(crypto1, crypto2));

            // When
            var cryptoStatistics = serviceUnderTest.calculateStatisticsForCurrentMonthForAllCrypto();

            // Then
            assertThat(cryptoStatistics).hasSize(2);
            assertThat(cryptoStatistics).extracting(CryptoStatistics::symbol).containsExactlyInAnyOrder(btc, eth);
            assertThat(cryptoStatistics).allSatisfy(stat -> {
                assertThat(stat.oldestSnapshot().timestamp()).isAfterOrEqualTo(startOfCurrentMonth);
                assertThat(stat.newestSnapshot().timestamp()).isBeforeOrEqualTo(endOfCurrentMonth);
                assertThat(stat.snapshotWithMinPrice().price()).isNotNull();
                assertThat(stat.snapshotOfMaxPrice().price()).isNotNull();
            });
        }

        @Test
        @DisplayName("Given cryptos with no price snapshots within current month, when calculateStatisticsForCurrentMonthForAllCrypto, then return empty set")
        void givenCryptosWithNoPriceSnapshotsWithinCurrentMonth_whenCalculateStatisticsForCurrentMonth_thenReturnEmptySet() {
            // Given
            var startOfCurrentMonth = LocalDateTime.of(2023, 7, 1, 0, 0);
            var endOfCurrentMonth = LocalDateTime.of(2023, 7, 31, 23, 59);

            var btc = new CryptoSymbol("BTC");
            var eth = new CryptoSymbol("ETH");

            var crypto1 = createCryptoWithPriceSnapshots(btc, startOfCurrentMonth.minusDays(1),
                startOfCurrentMonth.minusHours(1));
            var crypto2 = createCryptoWithPriceSnapshots(eth, endOfCurrentMonth.plusHours(1),
                endOfCurrentMonth.plusDays(1));

            when(clock.startOfCurrentMonth()).thenReturn(startOfCurrentMonth);
            when(clock.endOfCurrentMonth()).thenReturn(endOfCurrentMonth);
            when(cryptoCurrencyRepository.loadCryptoCurrencyPrices()).thenReturn(Stream.of(crypto1, crypto2));

            // When
            var cryptoStatistics = serviceUnderTest.calculateStatisticsForCurrentMonthForAllCrypto();

            // Then
            assertThat(cryptoStatistics).isEmpty();
        }

        private Crypto createCryptoWithPriceSnapshots(CryptoSymbol symbol, LocalDateTime start, LocalDateTime end) {
            var snapshot1 = new CryptoPriceSnapshot(start, BigDecimal.valueOf(100));
            var snapshot2 = new CryptoPriceSnapshot(end, BigDecimal.valueOf(200));

            return new Crypto(symbol, Set.of(snapshot1, snapshot2));
        }
    }
}
