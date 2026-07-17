package com.example.currencyconverter.domain.model

import java.math.BigDecimal

data class ConversionQuote(
    val fromCurrency: CurrencyCode,
    val toCurrency: CurrencyCode,
    val sourceAmount: BigDecimal,
    val convertedAmount: BigDecimal,
    val conversionRate: BigDecimal,
    val inverseRate: BigDecimal
)
