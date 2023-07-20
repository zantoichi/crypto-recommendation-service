package com.xm.cryptorecommendationservice.infrastructure.csv;

import static org.springframework.util.Assert.notNull;

import com.xm.cryptorecommendationservice.domain.CryptoPriceSnapshot;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import java.math.BigDecimal;
import java.time.LocalDateTime;

record CryptoCsvEntry(CryptoSymbol cryptoSymbol, LocalDateTime timestamp,
                      BigDecimal price) {

    CryptoCsvEntry {
        notNull(cryptoSymbol, "Crypto getSymbol cannot be null");
        notNull(timestamp, "Timestamp cannot be null");
        notNull(price, "Price cannot be null");
    }

    CryptoPriceSnapshot toCryptoCurrencyPriceSnapshot() {
        return new CryptoPriceSnapshot(timestamp, price);
    }
}
