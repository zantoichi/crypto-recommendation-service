package com.xm.cryptorecommendationservice.infrastructure.csv;

import static org.springframework.util.Assert.notNull;

import com.xm.cryptorecommendationservice.domain.Crypto;
import com.xm.cryptorecommendationservice.domain.CryptoPriceSnapshot;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import com.xm.cryptorecommendationservice.infrastructure.CryptoCurrencyRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class CryptoCsvRepository implements CryptoCurrencyRepository {

    private final CsvReader csvReader;

    @Override
    public Stream<Crypto> loadCryptoCurrencyPrices() {
        var cryptoCsvEntries = csvReader.loadAllCryptoPricesFromCsvFiles();

        Map<CryptoSymbol, List<CryptoCsvEntry>> entriesBySymbol = groupEntriesByCryptoSymbol(cryptoCsvEntries);

        return createCryptoInstancesForEachSymbol(entriesBySymbol);
    }

    @Override
    public Optional<Crypto> loadCryptoCurrencyPricesForCrypto(CryptoSymbol cryptoSymbol) {
        notNull(cryptoSymbol, "cryptoSymbol must not be null");

        var cryptoCsvEntriesForCryptoSymbol = csvReader.loadCryptoPricesForSpecificCryptoFromCsvFiles(
            cryptoSymbol);

        if (!cryptoCsvEntriesForCryptoSymbol.isEmpty()) {
            var priceSnapshots = cryptoCsvEntriesForCryptoSymbol.stream()
                .map(CryptoCsvEntry::toCryptoCurrencyPriceSnapshot)
                .collect(Collectors.toSet());

            return Optional.of(new Crypto(cryptoSymbol, priceSnapshots));
        }

        return Optional.empty();
    }

    private Map<CryptoSymbol, List<CryptoCsvEntry>> groupEntriesByCryptoSymbol(
        Collection<CryptoCsvEntry> cryptoCsvEntries) {
        return cryptoCsvEntries.stream()
            .collect(Collectors.groupingBy(CryptoCsvEntry::cryptoSymbol));
    }

    private Stream<Crypto> createCryptoInstancesForEachSymbol(Map<CryptoSymbol, List<CryptoCsvEntry>> entriesBySymbol) {
        return entriesBySymbol.entrySet().stream()
            .map(entry -> createCryptoCurrency(entry.getKey(), entry.getValue()));
    }

    private Crypto createCryptoCurrency(CryptoSymbol symbol, List<CryptoCsvEntry> entries) {
        Set<CryptoPriceSnapshot> snapshots = entries.stream()
            .map(CryptoCsvEntry::toCryptoCurrencyPriceSnapshot)
            .collect(Collectors.toSet());

        return new Crypto(symbol, snapshots);
    }
}
