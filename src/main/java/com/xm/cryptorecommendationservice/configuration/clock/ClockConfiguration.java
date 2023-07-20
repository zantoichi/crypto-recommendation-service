package com.xm.cryptorecommendationservice.configuration.clock;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ClockConfiguration {

    @Bean
    ClockAdapter clockAdapter() {
        return new ClockAdapter(Clock.systemUTC());
    }
}
