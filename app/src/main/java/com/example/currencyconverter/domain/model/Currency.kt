package com.example.currencyconverter.domain.model

import java.io.Serializable

data class Currency(
    val code: CurrencyCode,
    val displayName: String,
    val flagEmoji: String
) : Serializable
