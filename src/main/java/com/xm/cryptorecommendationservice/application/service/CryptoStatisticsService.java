package com.xm.cryptorecommendationservice.application.service;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import com.xm.cryptorecommendationservice.configuration.clock.ClockAdapter;
import com.xm.cryptorecommendationservice.domain.Crypto;
import com.xm.cryptorecommendationservice.domain.CryptoStatistics;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import com.xm.cryptorecommendationservice.domain.CryptoWithNormalizedRange;
import com.xm.cryptorecommendationservice.infrastructure.CryptoCurrencyRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CryptoStatisticsService {

    private final ClockAdapter clock;
    private final CryptoCurrencyRepository cryptoCurrencyRepository;

    public Stream<CryptoWithNormalizedRange> getAllCryptoSortedByNormalizedRangeInDescendingOrder() {
        return cryptoCurrencyRepository.loadCryptoCurrencyPrices()
            .map(Crypto::toCryptoWithNormalizedRange)
            .sorted(Comparator.comparingDouble(CryptoWithNormalizedRange::normalizedRange).reversed());
    }

    public Optional<CryptoWithNormalizedRange> getCryptoWithHighestNormalizedRangeForDay(LocalDate day) {

        return cryptoCurrencyRepository.loadCryptoCurrencyPrices()
            .map(crypto -> CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(
                crypto, clock.startOfDay(day), clock.endOfDay(day)))
            .flatMap(Optional::stream)
            .map(CryptoStatistics::toCryptoWithNormalizedRange)
            .max(Comparator.comparingDouble(CryptoWithNormalizedRange::normalizedRange));
    }

    public CryptoStatistics calculateStatisticsForCrypto(CryptoSymbol cryptoSymbol) {
        notNull(cryptoSymbol, "cryptoSymbol cannot be null");

        return cryptoCurrencyRepository
            .loadCryptoCurrencyPricesForCrypto(cryptoSymbol)
            .flatMap(CryptoStatistics::calculateStatisticsForCrypto)
            .orElseThrow(NoStatisticsFoundForCryptoException::new);
    }

    public Collection<CryptoStatistics> calculateStatisticsForCurrentMonthForAllCrypto() {

        return cryptoCurrencyRepository.loadCryptoCurrencyPrices()
            .map(crypto -> CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(
                crypto,
                clock.startOfCurrentMonth(),
                clock.endOfCurrentMonth()))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
    }

    public Collection<CryptoStatistics> calculateStatisticsForPastPeriodForAllCrypto(Period period) {
        notNull(period, "period cannot be null");
        isTrue(!period.isNegative(), "period cannot be negative");
        isTrue(!period.isZero(), "period cannot be zero");

        var startOfPeriod = clock.now().minus(period);
        var endOfPeriod = clock.now();

        return cryptoCurrencyRepository.loadCryptoCurrencyPrices()
            .map(crypto -> CryptoStatistics.calculateStatisticsForCryptoWithinTimeframe(
                crypto,
                startOfPeriod,
                endOfPeriod))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
    }
}

