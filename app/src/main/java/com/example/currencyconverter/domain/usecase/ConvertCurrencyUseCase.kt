package com.example.currencyconverter.domain.usecase

import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.repository.ExchangeRatesRepository
import java.math.BigDecimal

class ConvertCurrencyUseCase(private val repository: ExchangeRatesRepository) {
    suspend operator fun invoke(from: CurrencyCode, to: CurrencyCode, amount: BigDecimal): CurrencyResult<ConversionQuote> =
        repository.convertCurrency(from, to, amount)
}
