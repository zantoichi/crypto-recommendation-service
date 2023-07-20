package com.xm.cryptorecommendationservice.infrastructure;

import com.xm.cryptorecommendationservice.domain.Crypto;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import java.util.Optional;
import java.util.stream.Stream;

public interface CryptoCurrencyRepository {

    Stream<Crypto> loadCryptoCurrencyPrices();

    Optional<Crypto> loadCryptoCurrencyPricesForCrypto(CryptoSymbol symbol);
}
