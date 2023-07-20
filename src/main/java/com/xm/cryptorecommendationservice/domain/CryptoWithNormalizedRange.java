package com.xm.cryptorecommendationservice.domain;

import static org.springframework.util.Assert.notNull;

public record CryptoWithNormalizedRange(CryptoSymbol symbol, Double normalizedRange) {

    public CryptoWithNormalizedRange {
        notNull(symbol, "Symbol cannot be null");
        notNull(normalizedRange, "Normalized range cannot be null");
    }
}
