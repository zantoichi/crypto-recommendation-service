package com.xm.cryptorecommendationservice.infrastructure.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.xm.cryptorecommendationservice.configuration.clock.ClockAdapter;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import java.nio.file.Paths;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class CsvReaderTest {

    private final ClockAdapter clock = new ClockAdapter(Clock.systemUTC());
    private final CsvReaderProperties csvReaderProperties = new CsvReaderProperties(
        Paths.get("src/test/resources/prices/"),
        "_values.csv",
        "timestamp",
        "symbol",
        "price");

    private final CsvReader csvReader = new CsvReader(csvReaderProperties, clock);

    @Test
    void testLoadCryptoCurrencyPrices(CapturedOutput logOutput) {
        // given
        var numberOfCsvFiles = 2;
        var numberOfValidEntriesInEachCsvFile = 10;

        // when
        var prices = csvReader.loadAllCryptoPricesFromCsvFiles();

        // then
        assertThat(prices).hasSize(numberOfCsvFiles * numberOfValidEntriesInEachCsvFile);
        assertThat(logOutput).contains(
            "Failed to parse CSV record. Skipping entry: CSVRecord [comment='null', recordNumber=11, values=[null, BTC, 47336.98]]");
    }

    @Test
    void testLoadCryptoCurrencyPricesForCrypto() {
        //given
        var numberOfValidEntriesInEachCsvFile = 10;

        // when
        var prices = csvReader.loadCryptoPricesForSpecificCryptoFromCsvFiles(new CryptoSymbol("BTC"));

        // then
        assertThat(prices).hasSize(numberOfValidEntriesInEachCsvFile);
    }
}
