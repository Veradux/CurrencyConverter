package com.example.currencyconverter.service.mapper

import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.util.CurrencyFlagProvider
import com.example.currencyconverter.service.dto.PairConversionResponse
import com.example.currencyconverter.service.dto.SupportedCodesResponse
import java.math.BigDecimal

/**
 * Maps DTOs from the Exchange Rate API to domain models.
 */
object DomainMappers {

    /**
     * Maps a [SupportedCodesResponse] to a list of [Currency] domain models.
     */
    fun SupportedCodesResponse.toCurrencies(): List<Currency> {
        return supportedCodes.map { (code, name) ->
            Currency(
                code = CurrencyCode(code),
                displayName = name,
                flagEmoji = CurrencyFlagProvider.flagFor(code)
            )
        }
    }

    /**
     * Maps a [PairConversionResponse] to a [ConversionQuote] domain model.
     */
    fun PairConversionResponse.toConversionQuote(amount: BigDecimal): ConversionQuote {
        val rate = BigDecimal.valueOf(conversionRate)
        val result = BigDecimal.valueOf(conversionResult)
        val inverseRate = if (rate.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal.ONE.divide(rate, 10, java.math.RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        return ConversionQuote(
            fromCurrency = CurrencyCode(baseCode),
            toCurrency = CurrencyCode(targetCode),
            sourceAmount = amount,
            convertedAmount = result,
            conversionRate = rate,
            inverseRate = inverseRate
        )
    }

    /**
     * Maps an error-type string from the API response to a [DomainError].
     */
    fun mapErrorType(errorType: String?): DomainError {
        return when (errorType?.lowercase()) {
            "unsupported-code" -> DomainError.UnsupportedCurrency()
            "malformed-request" -> DomainError.ApiError("malformed-request", "The request was malformed.")
            "invalid-key" -> DomainError.InvalidApiKey()
            "inactive-account" -> DomainError.InvalidApiKey("The API account is inactive.")
            "quota-reached" -> DomainError.QuotaReached()
            "plan-upgrade-required" -> DomainError.QuotaReached("Plan upgrade required to access this feature.")
            else -> DomainError.UnknownError(errorType ?: "An unexpected error occurred.")
        }
    }
}
