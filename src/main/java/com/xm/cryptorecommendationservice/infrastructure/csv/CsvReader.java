package com.xm.cryptorecommendationservice.infrastructure.csv;

import static java.lang.Long.parseLong;
import static org.springframework.util.Assert.notNull;

import com.xm.cryptorecommendationservice.configuration.clock.ClockAdapter;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import io.vavr.control.Try;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class CsvReader {

    private final CsvReaderProperties csvReaderProperties;
    private final ClockAdapter clock;

    public Collection<CryptoCsvEntry> loadAllCryptoPricesFromCsvFiles() {
        if (Files.notExists(csvReaderProperties.directoryPath())) {
            return Collections.emptySet();
        }

        try (var paths = Files.walk(csvReaderProperties.directoryPath())) {

            return paths
                .filter(this::fileNameEndsWithRequiredSuffix)
                .map(this::readCryptoPricesFromCsvFile)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        } catch (Exception e) {
            throw new UnableToReadDataFromCsvException(
                "Error while reading CSV files from " + csvReaderProperties.directoryPath(), e);
        }
    }

    public Collection<CryptoCsvEntry> loadCryptoPricesForSpecificCryptoFromCsvFiles(CryptoSymbol currencySymbol) {
        notNull(currencySymbol, "currencySymbol must not be null");
        if (Files.notExists(csvReaderProperties.directoryPath())) {
            return Collections.emptySet();
        }

        try (var paths = Files.walk(csvReaderProperties.directoryPath())) {

            return paths
                .filter(this::fileNameEndsWithRequiredSuffix)
                .filter(filePath -> fileNameStartsWithCryptoSymbol(currencySymbol, filePath))
                .map(this::readCryptoPricesFromCsvFile)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());

        } catch (IOException e) {
            throw new UnableToReadDataFromCsvException(
                "Error while reading CSV files from " + csvReaderProperties.directoryPath(), e);
        }
    }

    private Collection<CryptoCsvEntry> readCryptoPricesFromCsvFile(Path filePath) {
        if (Files.notExists(csvReaderProperties.directoryPath())) {
            return Collections.emptySet();
        }

        try (var reader = Files.newBufferedReader(filePath)) {
            var csvRecords = CSVFormat.DEFAULT
                .builder()
                .setHeader(csvReaderProperties.timestampHeader(), csvReaderProperties.symbolHeader(),
                    csvReaderProperties.priceHeader())
                .setSkipHeaderRecord(true)
                .build()
                .parse(reader);

            Set<CryptoCsvEntry> entries = new HashSet<>();
            csvRecords.forEach(csvRecord ->
                Try.of(() -> entries.add(csvRecordToCryptoCsvEntry(csvRecord)))
                    .onFailure(ex -> log.warn("Failed to parse CSV record. Skipping entry: {}", csvRecord, ex)));

            return Collections.unmodifiableSet(entries);
        } catch (Exception e) {
            throw new UnableToReadDataFromCsvException("Error while reading CSV file " + filePath, e);
        }
    }

    private CryptoCsvEntry csvRecordToCryptoCsvEntry(CSVRecord csvRecord) {

        var timestamp = clock.ofEpochMilli(parseLong(csvRecord.get(csvReaderProperties.timestampHeader())));

        var price = new BigDecimal(csvRecord.get(csvReaderProperties.priceHeader()));

        return new CryptoCsvEntry(
            new CryptoSymbol(csvRecord.get(csvReaderProperties.symbolHeader())),
            timestamp,
            price);
    }

    private boolean fileNameEndsWithRequiredSuffix(Path filePath) {
        return filePath.toString().endsWith(csvReaderProperties.fileSuffix());
    }

    private boolean fileNameStartsWithCryptoSymbol(CryptoSymbol currencySymbol, Path filePath) {
        return filePath.getFileName().toString().startsWith(currencySymbol.symbol());
    }
}
