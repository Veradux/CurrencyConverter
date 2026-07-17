package com.example.currencyconverter.domain.repository

import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.ConversionQuote
import java.math.BigDecimal

interface ExchangeRatesRepository {
    suspend fun getSupportedCurrencies(forceRefresh: Boolean = false): CurrencyResult<List<Currency>>
    suspend fun convertCurrency(from: CurrencyCode, to: CurrencyCode, amount: BigDecimal): CurrencyResult<ConversionQuote>
}
