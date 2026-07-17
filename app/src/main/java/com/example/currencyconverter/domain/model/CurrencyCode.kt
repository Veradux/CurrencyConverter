package com.example.currencyconverter.domain.model

import java.io.Serializable

data class CurrencyCode(val value: String) : Serializable {
    init {
        require(value.length == 3 && value.all { it.isLetter() }) {
            "Currency code must be exactly 3 letters"
        }
    }

    override fun toString(): String = value
}
