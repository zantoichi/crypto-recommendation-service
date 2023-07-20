package com.xm.cryptorecommendationservice.infrastructure.csv;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "csv.reader")
record CsvReaderProperties(Path directoryPath, String fileSuffix, String timestampHeader, String symbolHeader,
                           String priceHeader) {

}
