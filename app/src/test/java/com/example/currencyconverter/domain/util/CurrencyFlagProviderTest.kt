package com.example.currencyconverter.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFlagProviderTest {

    @Test
    fun normalMappingsReturnCorrectFlags() {
        assertEquals("\uD83C\uDDEA\uD83C\uDDFA", CurrencyFlagProvider.flagFor("EUR"))
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", CurrencyFlagProvider.flagFor("USD"))
        assertEquals("\uD83C\uDDEC\uD83C\uDDE7", CurrencyFlagProvider.flagFor("GBP"))
        assertEquals("\uD83C\uDDEF\uD83C\uDDF5", CurrencyFlagProvider.flagFor("JPY"))
        assertEquals("\uD83C\uDDE8\uD83C\uDDED", CurrencyFlagProvider.flagFor("CHF"))
        assertEquals("\uD83C\uDDE8\uD83C\uDDE6", CurrencyFlagProvider.flagFor("CAD"))
        assertEquals("\uD83C\uDDE6\uD83C\uDDFA", CurrencyFlagProvider.flagFor("AUD"))
    }

    @Test
    fun specialMappingsVerified() {
        assertEquals("\uD83C\uDDE8\uD83C\uDDF3", CurrencyFlagProvider.flagFor("CNY"))
        assertEquals("\uD83C\uDDF8\uD83C\uDDEA", CurrencyFlagProvider.flagFor("SEK"))
        assertEquals("\uD83C\uDDF3\uD83C\uDDFF", CurrencyFlagProvider.flagFor("NZD"))
        assertEquals("\uD83C\uDDF2\uD83C\uDDFD", CurrencyFlagProvider.flagFor("MXN"))
        assertEquals("\uD83C\uDDEE\uD83C\uDDF3", CurrencyFlagProvider.flagFor("INR"))
    }

    @Test
    fun lowercaseInputNormalized() {
        assertEquals("\uD83C\uDDEA\uD83C\uDDFA", CurrencyFlagProvider.flagFor("eur"))
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", CurrencyFlagProvider.flagFor("usd"))
        assertEquals("\uD83C\uDDEC\uD83C\uDDE7", CurrencyFlagProvider.flagFor("gbp"))
    }

    @Test
    fun unknownCodesReturnGlobeFallback() {
        assertEquals("\uD83C\uDF10", CurrencyFlagProvider.flagFor("XXX"))
        assertEquals("\uD83C\uDF10", CurrencyFlagProvider.flagFor("ABC"))
    }

    @Test
    fun blankOrEmptyInputReturnsGlobeFallback() {
        assertEquals("\uD83C\uDF10", CurrencyFlagProvider.flagFor(""))
    }

    @Test
    fun trimmedWhitespaceHandled() {
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", CurrencyFlagProvider.flagFor(" USD "))
    }
}
