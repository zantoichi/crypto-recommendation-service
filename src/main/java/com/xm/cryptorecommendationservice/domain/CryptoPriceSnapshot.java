package com.xm.cryptorecommendationservice.domain;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CryptoPriceSnapshot(LocalDateTime timestamp, BigDecimal price) {

    public CryptoPriceSnapshot {
        notNull(price, "Price cannot be null");
        notNull(timestamp, "Timestamp cannot be null");
    }
}
