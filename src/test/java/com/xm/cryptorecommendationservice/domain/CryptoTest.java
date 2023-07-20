package com.xm.cryptorecommendationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CryptoTest {

    @Nested
    @DisplayName("toCryptoWithNormalizedRange Tests")
    class ToCryptoWithNormalizedRangeTests {

        @Test
        @DisplayName("Given valid Crypto with statistics, when toCryptoWithNormalizedRange, then return CryptoWithNormalizedRange")
        void givenValidCryptoWithStatistics_whenToCryptoWithNormalizedRange_thenReturnCryptoWithNormalizedRange() {
            // Given
            var btc = new CryptoSymbol("BTC");
            Set<CryptoPriceSnapshot> priceSnapshots = new HashSet<>();
            priceSnapshots.add(new CryptoPriceSnapshot(LocalDateTime.now().minusDays(2), new BigDecimal("100")));
            priceSnapshots.add(new CryptoPriceSnapshot(LocalDateTime.now().minusDays(1), new BigDecimal("200")));
            priceSnapshots.add(new CryptoPriceSnapshot(LocalDateTime.now(), new BigDecimal("150")));

            var crypto = new Crypto(btc, priceSnapshots);

            // When
            CryptoWithNormalizedRange result = crypto.toCryptoWithNormalizedRange();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.symbol()).isEqualTo(btc);
            assertThat(result.normalizedRange()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("Given Crypto with no statistics, when toCryptoWithNormalizedRange, then throw IllegalArgumentException")
        void givenCryptoWithNoStatistics_whenToCryptoWithNormalizedRange_thenThrowNoStatisticsFoundForCryptoException() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var crypto = new Crypto(btc, null);

            // When/Then
            assertThatIllegalArgumentException().isThrownBy(crypto::toCryptoWithNormalizedRange);
        }
    }
}
