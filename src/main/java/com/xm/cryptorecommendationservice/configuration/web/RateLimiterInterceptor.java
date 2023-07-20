package com.xm.cryptorecommendationservice.configuration.web;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * This {@link HandlerInterceptor} handles the rate limiting of requests based on IP address. If a client sends too many
 * requests in a short time period, it returns an {@link HttpStatus#TOO_MANY_REQUESTS} response.
 */
@Component
@RequiredArgsConstructor
@Slf4j
class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiterRegistry rateLimiterRegistry;

    /**
     * Handles the pre-processing of HTTP requests. It retrieves the RateLimiter corresponding to the client IP and
     * checks if the request is rate limited. If the request is rate limited, it sets the response status to
     * {@link HttpStatus#TOO_MANY_REQUESTS} and returns false.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull Object handler) {
        var clientIp = request.getRemoteAddr();
        var rateLimiter = getRateLimiterForIp(clientIp);

        if (isRequestRateLimited(rateLimiter)) {
            log.warn("Client IP {} is rate limited", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
            return false;
        }

        return true;
    }

    private RateLimiter getRateLimiterForIp(String ipAddress) {
        return this.rateLimiterRegistry.rateLimiter(ipAddress);
    }

    private boolean isRequestRateLimited(RateLimiter rateLimiter) {
        return rateLimiter.reservePermission() < 0;
    }
}
