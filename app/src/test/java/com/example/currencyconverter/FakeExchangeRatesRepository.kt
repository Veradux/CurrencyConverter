package com.example.currencyconverter

import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.repository.ExchangeRatesRepository
import java.math.BigDecimal
import kotlin.time.Duration.Companion.milliseconds

class FakeExchangeRatesRepository : ExchangeRatesRepository {
    var currencies: List<Currency> = emptyList()
    var conversionResult: CurrencyResult<ConversionQuote> = CurrencyResult.Error(DomainError.UnknownError())
    var delayMs: Long = 0
    var shouldThrowNetworkError: Boolean = false
    var shouldThrowApiError: Boolean = false
    var getSupportedCurrenciesCallCount = 0
    var convertCurrencyCallCount = 0

    override suspend fun getSupportedCurrencies(forceRefresh: Boolean): CurrencyResult<List<Currency>> {
        getSupportedCurrenciesCallCount++
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs.milliseconds)
        if (shouldThrowNetworkError) return CurrencyResult.Error(DomainError.NetworkError())
        if (shouldThrowApiError) return CurrencyResult.Error(DomainError.ApiError("test-error"))
        return CurrencyResult.Success(currencies)
    }

    override suspend fun convertCurrency(
        from: CurrencyCode,
        to: CurrencyCode,
        amount: BigDecimal
    ): CurrencyResult<ConversionQuote> {
        convertCurrencyCallCount++
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs.milliseconds)
        if (shouldThrowNetworkError) return CurrencyResult.Error(DomainError.NetworkError())
        if (shouldThrowApiError) return CurrencyResult.Error(DomainError.ApiError("test-error"))
        return conversionResult
    }
}
