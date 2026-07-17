package com.example.currencyconverter.domain.usecase

import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.repository.ExchangeRatesRepository

class GetSupportedCurrenciesUseCase(private val repository: ExchangeRatesRepository) {
    suspend operator fun invoke(forceRefresh: Boolean = false): CurrencyResult<List<Currency>> =
        repository.getSupportedCurrencies(forceRefresh)
}
