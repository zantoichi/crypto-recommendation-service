package com.xm.cryptorecommendationservice.configuration.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class RateLimiterConfiguration {

    private final RateLimiterConfigProperties rateLimiterConfigProperties;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        var config = RateLimiterConfig.custom()
            .limitRefreshPeriod(rateLimiterConfigProperties.refreshPeriod())
            .limitForPeriod(rateLimiterConfigProperties.limit())
            .timeoutDuration(rateLimiterConfigProperties.timeoutDuration())
            .build();
        return RateLimiterRegistry.of(config);
    }
}
