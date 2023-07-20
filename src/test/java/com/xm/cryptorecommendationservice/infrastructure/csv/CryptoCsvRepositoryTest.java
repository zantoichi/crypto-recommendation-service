package com.xm.cryptorecommendationservice.infrastructure.csv;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xm.cryptorecommendationservice.domain.Crypto;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CryptoCsvRepositoryTest {

    private final CsvReader csvReader = mock(CsvReader.class);

    private final CryptoCsvRepository repositoryUnderTest = new CryptoCsvRepository(csvReader);

    @Nested
    @DisplayName("loadCryptoCurrencyPrices Tests")
    class LoadCryptoCurrencyPricesTests {

        @Test
        @DisplayName("Given CryptoCsvEntries exist, when loadCryptoCurrencyPrices, then return Set of Crypto")
        void givenCryptoCsvEntriesExist_whenLoadCryptoCurrencyPrices_thenReturnSetOfCrypto() {
            // Given
            var btc = new CryptoSymbol("BTC");
            var eth = new CryptoSymbol("ETH");
            var csvEntry1 = new CryptoCsvEntry(btc, LocalDateTime.now(), BigDecimal.ONE);
            var csvEntry2 = new CryptoCsvEntry(btc, LocalDateTime.now().minusDays(2), BigDecimal.ZERO);
            var csvEntry3 = new CryptoCsvEntry(eth, LocalDateTime.now(), BigDecimal.ONE);
            var csvEntry4 = new CryptoCsvEntry(eth, LocalDateTime.now().minusDays(2), BigDecimal.ZERO);
            var csvEntries = Set.of(csvEntry1, csvEntry2, csvEntry3, csvEntry4);

            when(csvReader.loadAllCryptoPricesFromCsvFiles()).thenReturn(csvEntries);

            // When
            var alLCryptoPrices = repositoryUnderTest.loadCryptoCurrencyPrices().collect(Collectors.toSet());

            // Then
            assertThat(alLCryptoPrices)
                .hasSize(2)
                .flatExtracting(Crypto::priceSnapshots)
                .hasSize(4);
        }

        @Test
        @DisplayName("Given no CryptoCsvEntries exist, when loadCryptoCurrencyPrices, then return empty Set")
        void givenNoCryptoCsvEntriesExist_whenLoadCryptoCurrencyPrices_thenReturnEmptySet() {
            // Given
            when(csvReader.loadAllCryptoPricesFromCsvFiles()).thenReturn(Collections.emptyList());

            // When
            var allCryptoPrices = repositoryUnderTest.loadCryptoCurrencyPrices().collect(Collectors.toSet());

            // Then
            assertThat(allCryptoPrices).isEmpty();
        }
    }

    @Nested
    @DisplayName("loadCryptoCurrencyPricesForCrypto Tests")
    class LoadCryptoCurrencyPricesForCryptoTests {

        @Test
        @DisplayName("Given entries exist for CryptoSymbol, when loadCryptoCurrencyPricesForCrypto, then return OptionalCrypto")
        void givenEntriesExistForCryptoSymbol_whenLoadCryptoCurrencyPricesForCrypto_thenReturnOptionalCrypto() {
            // Given
            var cryptoSymbol = new CryptoSymbol("BTC");
            var csvEntries = Set.of(
                new CryptoCsvEntry(cryptoSymbol, LocalDateTime.now(), BigDecimal.ONE),
                new CryptoCsvEntry(cryptoSymbol, LocalDateTime.now().minusDays(1), BigDecimal.ZERO)
            );

            when(csvReader.loadCryptoPricesForSpecificCryptoFromCsvFiles(cryptoSymbol)).thenReturn(csvEntries);

            // When
            Optional<Crypto> crypto = repositoryUnderTest.loadCryptoCurrencyPricesForCrypto(cryptoSymbol);

            // Then
            assertThat(crypto)
                .isPresent()
                .get()
                .extracting(Crypto::symbol)
                .isEqualTo(cryptoSymbol);
            assertThat(crypto.get().priceSnapshots()).hasSize(2);
        }

        @Test
        @DisplayName("Given no entries exist for CryptoSymbol, when loadCryptoCurrencyPricesForCrypto, then return empty Optional")
        void givenNoEntriesExistForCryptoSymbol_whenLoadCryptoCurrencyPricesForCrypto_thenReturnEmptyOptional() {
            // Given
            var cryptoSymbol = new CryptoSymbol("BTC");
            when(csvReader.loadCryptoPricesForSpecificCryptoFromCsvFiles(cryptoSymbol)).thenReturn(
                emptySet());

            // When
            Optional<Crypto> result = repositoryUnderTest.loadCryptoCurrencyPricesForCrypto(cryptoSymbol);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
