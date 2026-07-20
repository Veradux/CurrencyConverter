package com.example.currencyconverter.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CurrencyCodeTest {

    @Test
    fun valid3LetterCodesPassValidation() {
        val usd = CurrencyCode("USD")
        assertEquals("USD", usd.value)

        val eur = CurrencyCode("EUR")
        assertEquals("EUR", eur.value)

        val gbp = CurrencyCode("GBP")
        assertEquals("GBP", gbp.value)
    }

    @Test
    fun tooShortCodeThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("US")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("A")
        }
    }

    @Test
    fun tooLongCodeThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("USDE")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("ABCDEF")
        }
    }

    @Test
    fun codeWithNumbersThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("US1")
        }
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("123")
        }
    }

    @Test
    fun codeWithSpecialCharactersThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("U#D")
        }
    }

    @Test
    fun lowercaseCodesAccepted() {
        val eur = CurrencyCode("eur")
        assertEquals("eur", eur.value)

        val usd = CurrencyCode("usd")
        assertEquals("usd", usd.value)
    }

    @Test
    fun emptyStringThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode("")
        }
    }

    @Test
    fun toStringReturnsValue() {
        assertEquals("USD", CurrencyCode("USD").toString())
    }
}
