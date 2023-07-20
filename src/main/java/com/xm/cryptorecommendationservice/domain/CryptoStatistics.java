package com.xm.cryptorecommendationservice.domain;

import static java.util.Comparator.comparing;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import com.xm.cryptorecommendationservice.application.service.NoStatisticsFoundForCryptoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CryptoStatistics {

    private final CryptoSymbol symbol;
    private final CryptoPriceSnapshot oldestSnapshot;
    private final CryptoPriceSnapshot newestSnapshot;
    private final CryptoPriceSnapshot snapshotWithMinPrice;
    private final CryptoPriceSnapshot snapshotOfMaxPrice;

    public static Optional<CryptoStatistics> calculateStatisticsForCrypto(Crypto crypto) {
        notNull(crypto, "Crypto cannot be null");
        isTrue(!crypto.priceSnapshots().isEmpty(), "Crypto must have at least one price snapshot");

        var oldestPrice = crypto.priceSnapshots().stream()
            .min(comparing(CryptoPriceSnapshot::timestamp))
            .orElseThrow();

        var newestPrice = crypto.priceSnapshots().stream()
            .max(comparing(CryptoPriceSnapshot::timestamp))
            .orElseThrow();

        var minimumPrice = crypto.priceSnapshots().stream()
            .min(comparing(CryptoPriceSnapshot::price))
            .orElseThrow();

        var maximumPrice = crypto.priceSnapshots().stream()
            .max(comparing(CryptoPriceSnapshot::price))
            .orElseThrow();

        return Optional.of(
            new CryptoStatistics(crypto.symbol(), oldestPrice, newestPrice, minimumPrice,
                maximumPrice));
    }

    public static Optional<CryptoStatistics> calculateStatisticsForCryptoWithinTimeframe(Crypto crypto,
        LocalDateTime startDate,
        LocalDateTime endDate) {
        notNull(crypto, "Crypto cannot be null");
        notNull(startDate, "Start date cannot be null");
        notNull(endDate, "End date cannot be null");
        isTrue(!crypto.priceSnapshots().isEmpty(), "Crypto must have at least one price snapshot");
        isTrue(startDate.isBefore(endDate), "Start date must be before end date");

        var filteredSnapshots = crypto.priceSnapshots().stream()
            .filter(snapshot -> snapshotIsWithingGivenRange(startDate, endDate, snapshot))
            .toList();

        if (filteredSnapshots.isEmpty()) {
            return Optional.empty();
        }

        var oldestPrice = filteredSnapshots.stream()
            .min(comparing(CryptoPriceSnapshot::timestamp))
            .orElseThrow();

        var newestPrice = filteredSnapshots.stream()
            .max(comparing(CryptoPriceSnapshot::timestamp))
            .orElseThrow();

        var minimumPrice = filteredSnapshots.stream()
            .min(comparing(CryptoPriceSnapshot::price))
            .orElseThrow();

        var maximumPrice = filteredSnapshots.stream()
            .max(comparing(CryptoPriceSnapshot::price))
            .orElseThrow();

        return Optional.of(new CryptoStatistics(crypto.symbol(), oldestPrice,
            newestPrice, minimumPrice, maximumPrice));
    }

    private static boolean snapshotIsWithingGivenRange(LocalDateTime startRange, LocalDateTime endRange,
        CryptoPriceSnapshot snapshot) {
        return snapshot.timestamp().isAfter(startRange) && snapshot.timestamp().isBefore(endRange);
    }

    public CryptoWithNormalizedRange toCryptoWithNormalizedRange() {
        return this.calculateNormalizedRange()
            .map(normalizedRange -> new CryptoWithNormalizedRange(symbol, normalizedRange))
            .orElseThrow(NoStatisticsFoundForCryptoException::new);
    }

    public Optional<Double> calculateNormalizedRange() {
        var minPriceValue = snapshotWithMinPrice.price();
        var maxPriceValue = snapshotOfMaxPrice.price();

        if (minPriceValue.equals(BigDecimal.ZERO)) {
            return Optional.empty();
        }

        var range = maxPriceValue.subtract(minPriceValue);
        var normalizedRange = range.divide(minPriceValue, 2, RoundingMode.HALF_UP);
        return Optional.of(normalizedRange.doubleValue());
    }
}
