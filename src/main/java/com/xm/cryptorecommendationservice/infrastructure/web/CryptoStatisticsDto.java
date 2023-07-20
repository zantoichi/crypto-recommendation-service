package com.xm.cryptorecommendationservice.infrastructure.web;

import com.xm.cryptorecommendationservice.domain.CryptoStatistics;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;

record CryptoStatisticsDto(CryptoSymbol symbol,
                           CryptoPriceSnapshotDto oldestPriceSnapshot,
                           CryptoPriceSnapshotDto newestPriceSnapshot,
                           CryptoPriceSnapshotDto minPriceSnapshot,
                           CryptoPriceSnapshotDto maxPriceSnapshot) {

    public static CryptoStatisticsDto fromDomain(CryptoStatistics cryptoStatistics) {
        return new CryptoStatisticsDto(
            cryptoStatistics.symbol(),
            CryptoPriceSnapshotDto.fromDomain(cryptoStatistics.oldestSnapshot()),
            CryptoPriceSnapshotDto.fromDomain(cryptoStatistics.newestSnapshot()),
            CryptoPriceSnapshotDto.fromDomain(cryptoStatistics.snapshotWithMinPrice()),
            CryptoPriceSnapshotDto.fromDomain(cryptoStatistics.snapshotOfMaxPrice())
        );
    }
}
