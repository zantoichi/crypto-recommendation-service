package com.xm.cryptorecommendationservice.infrastructure.web;

import static com.xm.cryptorecommendationservice.infrastructure.web.CryptoStatisticsDto.fromDomain;

import com.xm.cryptorecommendationservice.application.service.CryptoStatisticsService;
import com.xm.cryptorecommendationservice.application.service.NoStatisticsFoundForCryptoException;
import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cryptos")
class CryptoController implements CryptoControllerDocumentation {

    private final CryptoStatisticsService cryptoStatisticsService;
    private final AllowedCryptoProperties allowedCryptoProperties;

    @GetMapping("/sorted-by-normalized-range")
    public List<CryptoWithNormalizedRangeDto> getCryptosOrderedByNormalizedRangeDescending() {
        return cryptoStatisticsService.getAllCryptoSortedByNormalizedRangeInDescendingOrder()
            .map(CryptoWithNormalizedRangeDto::fromDomain)
            .toList();
    }

    @GetMapping("/highest-normalized-range/{day}")
    public CryptoWithNormalizedRangeDto getCryptoWithMaxNormalizedRangeForDay(
        @PathVariable LocalDate day) {
        return cryptoStatisticsService.getCryptoWithHighestNormalizedRangeForDay(day)
            .map(CryptoWithNormalizedRangeDto::fromDomain)
            .orElseThrow(() -> new NoStatisticsFoundForCryptoException(
                "No crypto data found for the specified day %s".formatted(day)));
    }

    @GetMapping("/{cryptoSymbol}/statistics")
    public CryptoStatisticsDto getCryptoStatistics(@PathVariable CryptoSymbol cryptoSymbol) {
        allowedCryptoProperties.validate(cryptoSymbol);

        return fromDomain(cryptoStatisticsService.calculateStatisticsForCrypto(cryptoSymbol));
    }
}
