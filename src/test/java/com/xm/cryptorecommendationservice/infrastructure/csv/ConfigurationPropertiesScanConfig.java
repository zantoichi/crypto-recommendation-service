package com.xm.cryptorecommendationservice.infrastructure.csv;

import com.xm.cryptorecommendationservice.configuration.clock.ClockAdapter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableConfigurationProperties(CsvReaderProperties.class)
@ComponentScan(basePackageClasses = {CsvReader.class, ClockAdapter.class})
class ConfigurationPropertiesScanConfig {

}
