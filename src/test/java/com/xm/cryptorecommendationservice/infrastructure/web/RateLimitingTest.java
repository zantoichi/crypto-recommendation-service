package com.xm.cryptorecommendationservice.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.xm.cryptorecommendationservice.application.service.CryptoStatisticsService;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CryptoController.class)
class RateLimitingTest {

    private static final int NUMBER_OF_ALLOWED_REQUESTS_BEFORE_RATE_LIMITING = 5;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoStatisticsService cryptoStatisticsService;
    @MockBean
    private AllowedCryptoProperties allowedCryptoProperties;

    @Test
    void testRateLimiting() throws Exception {

        performTheAllowedAmountOfRequestsBeforeRateLimitingTheIP();

        mockMvc.perform(get("/cryptos/sorted-by-normalized-range"))
            .andExpect(status().isTooManyRequests());
    }

    private void performTheAllowedAmountOfRequestsBeforeRateLimitingTheIP() throws Exception {
        for (int i = 0; i < NUMBER_OF_ALLOWED_REQUESTS_BEFORE_RATE_LIMITING; i++) {
            mockMvc.perform(get("/cryptos/sorted-by-normalized-range"))
                .andExpect(status().isOk());
        }
    }

    @TestConfiguration
    static class RateLimiterTestConfig {

        @Bean
        public RateLimiterRegistry rateLimiterRegistry() {

            var config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(5))
                .limitForPeriod(NUMBER_OF_ALLOWED_REQUESTS_BEFORE_RATE_LIMITING)
                .timeoutDuration(Duration.ofSeconds(0))
                .build();
            return RateLimiterRegistry.of(config);
        }
    }
}
