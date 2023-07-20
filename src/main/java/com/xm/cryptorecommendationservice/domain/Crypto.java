package com.xm.cryptorecommendationservice.domain;

import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.util.Assert.notNull;

import com.xm.cryptorecommendationservice.application.service.NoStatisticsFoundForCryptoException;
import java.util.Collections;
import java.util.Set;
import org.springframework.lang.Nullable;

public record Crypto(CryptoSymbol symbol, Set<CryptoPriceSnapshot> priceSnapshots) {

    public Crypto(CryptoSymbol symbol, @Nullable Set<CryptoPriceSnapshot> priceSnapshots) {
        notNull(symbol, "Symbol cannot be null");

        this.symbol = symbol;
        this.priceSnapshots = requireNonNullElseGet(priceSnapshots, Collections::emptySet);
    }

    public CryptoWithNormalizedRange toCryptoWithNormalizedRange() {
        return CryptoStatistics.calculateStatisticsForCrypto(this)
            .flatMap(CryptoStatistics::calculateNormalizedRange)
            .map(normalizedRange -> new CryptoWithNormalizedRange(symbol, normalizedRange))
            .orElseThrow(NoStatisticsFoundForCryptoException::new);
    }
}
