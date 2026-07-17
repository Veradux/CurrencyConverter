package com.example.currencyconverter.domain.model

sealed class CurrencyResult<out T> {
    data class Success<T>(val data: T) : CurrencyResult<T>()
    data class Error(val error: DomainError) : CurrencyResult<Nothing>()
}
