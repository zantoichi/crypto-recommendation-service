package com.xm.cryptorecommendationservice.domain;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CryptoStatisticsTest {

    @Nested
    @DisplayName("calculateStatisticsForCrypto Tests")
    class CalculateStatisticsFromCryptoTests {

        @Test
        @DisplayName("""
            Given valid Crypto with price snapshots, when calculateStatisticsForCrypto,
            then return CryptoStatistics""")
        void givenValidCryptoWithPriceSnapshots_whenCalculateStatisticsFromCrypto_thenReturnCryptoStatistics() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var oldestSnapshot = new CryptoPriceSnapshot(LocalDateTime.now().minusDays(2), BigDecimal.valueOf(50000));
            var newestSnapshot = new CryptoPriceSnapshot(LocalDateTime.now(), BigDecimal.valueOf(60000));
            var snapshotWithMinPrice = new CryptoPriceSnapshot(LocalDateTime.now().minusDays(1),
                BigDecimal.valueOf(40000));
            var snapshotWithMaxPrice = new CryptoPriceSnapshot(LocalDateTime.now().minusHours(12),
                BigDecimal.valueOf(70000));

            var crypto = new Crypto(btc,
                Set.of(oldestSnapshot, newestSnapshot, snapshotWithMinPrice, snapshotWithMaxPrice));

            // When
            Optional<CryptoStatistics> result = CryptoStatistics.calculateStatisticsForCrypto(crypto);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get().symbol()).isEqualTo(btc);
            assertThat(result.get().oldestSnapshot()).isEqualTo(oldestSnapshot);
            assertThat(result.get().newestSnapshot()).isEqualTo(newestSnapshot);
            assertThat(result.get().snapshotWithMinPrice()).isEqualTo(snapshotWithMinPrice);
            assertThat(result.get().snapshotOfMaxPrice()).isEqualTo(snapshotWithMaxPrice);
        }

        @Test
        @DisplayName("Given null Crypto, when calculateStatisticsForCrypto, then throw IllegalArgumentException")
        void givenNullCrypto_whenCalculateStatisticsFromCrypto_thenThrowIllegalArgumentException() {
            // When/Then
            assertThatIllegalArgumentException().isThrownBy(() -> CryptoStatistics.calculateStatisticsForCrypto(null));
        }

        @Test
        @DisplayName("""
            Given Crypto with no price snapshots, when calculateStatisticsForCrypto,
            then throw IllegalArgumentException""")
        void givenCryptoWithNoPriceSnapshots_whenCalculateStatisticsFromCrypto_thenThrowIllegalArgumentException() {
            // Given
            var crypto = new Crypto(new CryptoSymbol("BTC"), emptySet());

            // When/Then
            assertThatIllegalArgumentException().isThrownBy(
                () -> CryptoStatistics.calculateStatisticsForCrypto(crypto));
        }
    }

    @Nested
    @DisplayName("calculateStatisticsForCryptoWithinTimeframe Tests")
    class CalculateStatisticsFromCryptoWithinRangeTests {

        @Test
        @DisplayName("""
            Given Crypto with price snapshots within range, when calculateStatisticsForCryptoWithinTimeframe,
            then return CryptoStatistics""")
        void givenCryptoWithSnapshotsWithinRange_whenCalculateStatisticsFromCryptoWithinRange_thenReturnCryptoStatistics() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var priceSnapshot1 = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 1, 0, 0),
                BigDecimal.valueOf(100));
            var priceSnapshot2 = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 5, 0, 0),
                BigDecimal.valueOf(150));
            var priceSnapshot3 = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 10, 0, 0),
                BigDecimal.valueOf(200));
            var priceSnapshotOutsideRange = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 20, 0, 0),
                BigDecimal.valueOf(200));

            var crypto = new Crypto(btc,
                Set.of(priceSnapshot1, priceSnapshot2, priceSnapshot3, priceSnapshotOutsideRange));

            var startRange = LocalDateTime.of(2022, 1, 2, 0, 0);
            var endRange = LocalDateTime.of(2022, 1, 12, 0, 0);

            // When
            Optional<CryptoStatistics> statistics = CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(
                crypto,
                startRange, endRange);

            // Then
            assertThat(statistics).isNotEmpty();
            var cryptoStatistics = statistics.get();
            assertThat(cryptoStatistics.symbol()).isEqualTo(btc);
            assertThat(cryptoStatistics.oldestSnapshot()).isEqualTo(priceSnapshot2);
            assertThat(cryptoStatistics.newestSnapshot()).isEqualTo(priceSnapshot3);
            assertThat(cryptoStatistics.snapshotWithMinPrice()).isEqualTo(priceSnapshot2);
            assertThat(cryptoStatistics.snapshotOfMaxPrice()).isEqualTo(priceSnapshot3);
        }

        @Test
        @DisplayName("""
            Given Crypto with no price snapshots within range, when calculateStatisticsForCryptoWithinTimeframe,
            then return empty Optional"""
        )
        void givenCryptoWithNoSnapshotsWithinRange_whenCalculateStatisticsFromCryptoWithinRange_thenReturnEmptyOptional() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var priceSnapshot1 = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 1, 0, 0),
                BigDecimal.valueOf(100));
            var priceSnapshot2 = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 5, 0, 0),
                BigDecimal.valueOf(150));
            var priceSnapshot3 = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 10, 0, 0),
                BigDecimal.valueOf(200));

            var crypto = new Crypto(btc, Set.of(priceSnapshot1, priceSnapshot2, priceSnapshot3));

            var startRange = LocalDateTime.of(2022, 1, 11, 0, 0);
            var endRange = LocalDateTime.of(2022, 1, 15, 0, 0);

            // When
            Optional<CryptoStatistics> statistics = CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(
                crypto,
                startRange, endRange);

            // Then
            assertThat(statistics).isEmpty();
        }

        @Test
        @DisplayName("Given null Crypto, when calculateStatisticsForCryptoWithinTimeframe, then throw IllegalArgumentException")
        void givenNullCrypto_whenCalculateStatisticsFromCryptoWithinRange_thenThrowIllegalArgumentException() {
            // Given
            var startRange = LocalDateTime.of(2022, 1, 1, 0, 0);
            var endRange = LocalDateTime.of(2022, 1, 5, 0, 0);

            // Then
            assertThatIllegalArgumentException().isThrownBy(
                () -> CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(null, startRange, endRange));
        }

        @Test
        @DisplayName("""
            Given Crypto with no price snapshots, when calculateStatisticsForCryptoWithinTimeframe,
            then throw IllegalArgumentException""")
        void givenCryptoWithNoPriceSnapshots_whenCalculateStatisticsFromCryptoWithinRange_thenThrowIllegalArgumentException() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var crypto = new Crypto(btc, Set.of());
            var startRange = LocalDateTime.of(2022, 1, 1, 0, 0);
            var endRange = LocalDateTime.of(2022, 1, 5, 0, 0);

            // Then
            assertThatIllegalArgumentException().isThrownBy(
                () -> CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(crypto, startRange, endRange));
        }

        @Test
        @DisplayName("""
            Given endRange before startRange, when calculateStatisticsForCryptoWithinTimeframe,
            then throw IllegalArgumentException""")
        void givenEndRangeBeforeStartRange_whenCalculateStatisticsFromCryptoWithinRange_thenThrowIllegalArgumentException() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var priceSnapshot = new CryptoPriceSnapshot(LocalDateTime.of(2022, 1, 1, 0, 0),
                BigDecimal.valueOf(100));
            var crypto = new Crypto(btc, Set.of(priceSnapshot));
            var startRange = LocalDateTime.of(2022, 1, 5, 0, 0);
            var endRange = LocalDateTime.of(2022, 1, 1, 0, 0);

            // Then
            assertThatIllegalArgumentException().isThrownBy(
                () -> CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(crypto, startRange, endRange));
        }
    }

    @Nested
    @DisplayName("calculateNormalizedRange Tests")
    class CalculateNormalizedRangeTests {

        @Test
        @DisplayName("Given valid CryptoStatistics with non-zero min price, when calculateNormalizedRange, then return normalized range")
        void givenValidCryptoStatisticsWithNonZeroMinPrice_whenCalculateNormalizedRange_thenReturnNormalizedRange() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var minPrice = new CryptoPriceSnapshot(LocalDateTime.now(), new BigDecimal("100"));
            var maxPrice = new CryptoPriceSnapshot(LocalDateTime.now(), new BigDecimal("200"));
            var cryptoStatistics = new CryptoStatistics(btc, null, null, minPrice, maxPrice);

            // When
            Optional<Double> normalizedRange = cryptoStatistics.calculateNormalizedRange();

            // Then
            assertThat(normalizedRange).contains(1.0);
        }

        @Test
        @DisplayName("Given valid CryptoStatistics with zero min price, when calculateNormalizedRange, then return empty Optional")
        void givenValidCryptoStatisticsWithZeroMinPrice_whenCalculateNormalizedRange_thenReturnEmptyOptional() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var minPrice = new CryptoPriceSnapshot(LocalDateTime.now(), BigDecimal.ZERO);
            var maxPrice = new CryptoPriceSnapshot(LocalDateTime.now(), new BigDecimal("200"));
            var cryptoStatistics = new CryptoStatistics(btc, null, null, minPrice, maxPrice);

            // When
            Optional<Double> normalizedRange = cryptoStatistics.calculateNormalizedRange();

            // Then
            assertThat(normalizedRange).isEmpty();
        }
    }
}
