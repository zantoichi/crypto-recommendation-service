package com.xm.cryptorecommendationservice.domain;

import static org.springframework.util.Assert.isTrue;

import org.apache.commons.lang3.StringUtils;

public record CryptoSymbol(String symbol) {

    public CryptoSymbol {
        isTrue(StringUtils.isNotBlank(symbol), "Symbol cannot be blank.");
    }
}
