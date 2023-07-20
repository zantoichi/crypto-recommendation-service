package com.xm.cryptorecommendationservice.infrastructure.web;

import com.xm.cryptorecommendationservice.domain.CryptoPriceSnapshot;
import java.math.BigDecimal;
import java.time.LocalDateTime;

record CryptoPriceSnapshotDto(LocalDateTime timestamp, BigDecimal price) {

    static CryptoPriceSnapshotDto fromDomain(CryptoPriceSnapshot cryptoPriceSnapshot) {
        return new CryptoPriceSnapshotDto(
            cryptoPriceSnapshot.timestamp(),
            cryptoPriceSnapshot.price()
        );
    }
}
