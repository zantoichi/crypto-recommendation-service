package com.xm.cryptorecommendationservice.infrastructure.web;

import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "app")
@Getter(AccessLevel.NONE)
class AllowedCryptoProperties {

    private final Set<CryptoSymbol> allowedCrypto;

    void validate(CryptoSymbol cryptoSymbol) {
        if (!allowedCrypto.contains(cryptoSymbol)) {
            throw new CryptoNotSupportedException(
                "Crypto not currently supported: %s".formatted(cryptoSymbol.symbol()));
        }
    }
}
