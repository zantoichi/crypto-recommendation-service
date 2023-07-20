package com.xm.cryptorecommendationservice.infrastructure.web;

import com.xm.cryptorecommendationservice.domain.CryptoSymbol;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;

interface CryptoControllerDocumentation {

    @Operation(summary = "Get a descending sorted list of all the cryptos",
        description = "Returns a descending sorted list of all cryptos based on their normalized range")
    @ApiResponse(responseCode = "200", description = "Cryptos with normalized range retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CryptoWithNormalizedRangeDto[].class)))
    List<CryptoWithNormalizedRangeDto> getCryptosOrderedByNormalizedRangeDescending();

    @Operation(summary = "Get crypto statistics", description = """
        Returns the oldestPriceSnapshot, newestPriceSnapshot,
        minPriceSnapshot, and maxPriceSnapshot values for a requested crypto""")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Crypto statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CryptoStatisticsDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or crypto getSymbol not found",
            content = @Content)
    })
    CryptoStatisticsDto getCryptoStatistics(
        @Parameter(description = "Crypto getSymbol", example = "BTC", required = true)
        CryptoSymbol cryptoSymbol);

    @Operation(summary = "Get crypto with highest normalized range",
        description = "Returns the crypto with the highest normalized range for a specific day")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Crypto with highest range retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CryptoPriceSnapshotDto.class))),
        @ApiResponse(responseCode = "404", description = "Crypto not found")
    })
    CryptoWithNormalizedRangeDto getCryptoWithMaxNormalizedRangeForDay(
        @Parameter(description = "The specified day. ISO 8601 supported.", example = "2022-01-01", required = true)
        LocalDate day);
}
