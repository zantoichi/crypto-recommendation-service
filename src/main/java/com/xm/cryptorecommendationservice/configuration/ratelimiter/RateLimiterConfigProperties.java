package com.xm.cryptorecommendationservice.configuration.ratelimiter;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiter")
record RateLimiterConfigProperties(Duration refreshPeriod, Duration timeoutDuration, int limit) {

}
