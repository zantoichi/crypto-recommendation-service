package com.xm.cryptorecommendationservice.configuration.web;

import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

class CryptoSymbolConverter implements Converter<String, CryptoSymbol> {

    @Override
    public CryptoSymbol convert(@NonNull String source) {
        return new CryptoSymbol(source);
    }
}
