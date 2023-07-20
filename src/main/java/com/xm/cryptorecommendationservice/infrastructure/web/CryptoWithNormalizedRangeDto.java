package com.xm.cryptorecommendationservice.infrastructure.web;

import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import com.xm.cryptorecommendationservice.domain.CryptoWithNormalizedRange;

record CryptoWithNormalizedRangeDto(CryptoSymbol symbol, Double normalizedRange) {

    public static CryptoWithNormalizedRangeDto fromDomain(CryptoWithNormalizedRange cryptoWithNormalizedRange) {
        return new CryptoWithNormalizedRangeDto(cryptoWithNormalizedRange.symbol(),
            cryptoWithNormalizedRange.normalizedRange());
    }
}
