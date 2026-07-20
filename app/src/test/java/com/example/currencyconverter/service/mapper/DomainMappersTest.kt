package com.example.currencyconverter.service.mapper

import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.util.CurrencyFlagProvider
import com.example.currencyconverter.service.dto.PairConversionResponse
import com.example.currencyconverter.service.dto.SupportedCodesResponse
import com.example.currencyconverter.service.mapper.DomainMappers.mapErrorType
import com.example.currencyconverter.service.mapper.DomainMappers.toConversionQuote
import com.example.currencyconverter.service.mapper.DomainMappers.toCurrencies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class DomainMappersTest {

    // ==================== toCurrencies ====================

    @Test
    fun `toCurrencies maps all currency codes to Currency objects with correct fields`() {
        val response = SupportedCodesResponse(
            result = "success",
            supportedCodes = listOf(
                listOf("EUR", "Euro"),
                listOf("USD", "United States Dollar"),
                listOf("GBP", "British Pound Sterling"),
            )
        )

        val currencies = response.toCurrencies()

        assertEquals(3, currencies.size)

        with(currencies[0]) {
            assertEquals(CurrencyCode("EUR"), code)
            assertEquals("Euro", displayName)
            assertEquals(CurrencyFlagProvider.flagFor("EUR"), flagEmoji)
        }

        with(currencies[1]) {
            assertEquals(CurrencyCode("USD"), code)
            assertEquals("United States Dollar", displayName)
            assertEquals(CurrencyFlagProvider.flagFor("USD"), flagEmoji)
        }

        with(currencies[2]) {
            assertEquals(CurrencyCode("GBP"), code)
            assertEquals("British Pound Sterling", displayName)
            assertEquals(CurrencyFlagProvider.flagFor("GBP"), flagEmoji)
        }
    }

    @Test
    fun `toCurrencies maps empty list to empty list`() {
        val response = SupportedCodesResponse(
            result = "success",
            supportedCodes = emptyList()
        )

        val currencies = response.toCurrencies()

        assertTrue(currencies.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toCurrencies throws on invalid currency code`() {
        val response = SupportedCodesResponse(
            result = "success",
            supportedCodes = listOf(listOf("INVALID", "Invalid Currency"))
        )

        response.toCurrencies()
    }

    // ==================== toConversionQuote ====================

    @Test
    fun `toConversionQuote maps valid PairConversionResponse to ConversionQuote with all fields correct`() {
        val response = PairConversionResponse(
            result = "success",
            baseCode = "EUR",
            targetCode = "USD",
            conversionRate = 1.08,
            conversionResult = 108.0
        )
        val amount = BigDecimal("100")

        val quote = response.toConversionQuote(amount)

        assertEquals(CurrencyCode("EUR"), quote.fromCurrency)
        assertEquals(CurrencyCode("USD"), quote.toCurrency)
        assertEquals(BigDecimal("100"), quote.sourceAmount)
        assertEquals(BigDecimal.valueOf(108.0), quote.convertedAmount)
        assertEquals(BigDecimal.valueOf(1.08), quote.conversionRate)
    }

    @Test
    fun `toConversionQuote calculates inverseRate correctly`() {
        val response = PairConversionResponse(
            result = "success",
            baseCode = "EUR",
            targetCode = "USD",
            conversionRate = 1.25,
            conversionResult = 125.0
        )

        val quote = response.toConversionQuote(BigDecimal("100"))

        val expectedInverse = BigDecimal.ONE.divide(
            BigDecimal.valueOf(1.25), 10, java.math.RoundingMode.HALF_UP
        )
        assertEquals(expectedInverse, quote.inverseRate)
    }

    @Test
    fun `toConversionQuote handles zero rate with inverseRate ZERO`() {
        val response = PairConversionResponse(
            result = "success",
            baseCode = "EUR",
            targetCode = "USD",
            conversionRate = 0.0,
            conversionResult = 0.0
        )

        val quote = response.toConversionQuote(BigDecimal("100"))

        assertTrue(quote.inverseRate.compareTo(BigDecimal.ZERO) == 0)
        assertTrue(quote.convertedAmount.compareTo(BigDecimal.ZERO) == 0)
        assertTrue(quote.conversionRate.compareTo(BigDecimal.ZERO) == 0)
    }

    @Test
    fun `toConversionQuote uses provided amount as sourceAmount`() {
        val response = PairConversionResponse(
            result = "success",
            baseCode = "EUR",
            targetCode = "USD",
            conversionRate = 1.08,
            conversionResult = 54.0
        )
        val amount = BigDecimal("50")

        val quote = response.toConversionQuote(amount)

        assertEquals(BigDecimal("50"), quote.sourceAmount)
    }

    // ==================== mapErrorType ====================

    @Test
    fun `mapErrorType unsupported-code returns UnsupportedCurrency`() {
        val error = mapErrorType("unsupported-code")
        assertTrue(error is DomainError.UnsupportedCurrency)
    }

    @Test
    fun `mapErrorType malformed-request returns ApiError`() {
        val error = mapErrorType("malformed-request")
        assertTrue(error is DomainError.ApiError)
        assertEquals("malformed-request", (error as DomainError.ApiError).code)
        assertEquals("The request was malformed.", error.message)
    }

    @Test
    fun `mapErrorType invalid-key returns InvalidApiKey`() {
        val error = mapErrorType("invalid-key")
        assertTrue(error is DomainError.InvalidApiKey)
    }

    @Test
    fun `mapErrorType inactive-account returns InvalidApiKey`() {
        val error = mapErrorType("inactive-account")
        assertTrue(error is DomainError.InvalidApiKey)
        assertEquals("The API account is inactive.", error.message)
    }

    @Test
    fun `mapErrorType quota-reached returns QuotaReached`() {
        val error = mapErrorType("quota-reached")
        assertTrue(error is DomainError.QuotaReached)
    }

    @Test
    fun `mapErrorType plan-upgrade-required returns QuotaReached`() {
        val error = mapErrorType("plan-upgrade-required")
        assertTrue(error is DomainError.QuotaReached)
        assertEquals("Plan upgrade required to access this feature.", error.message)
    }

    @Test
    fun `mapErrorType null returns UnknownError`() {
        val error = mapErrorType(null)
        assertTrue(error is DomainError.UnknownError)
        assertEquals("An unexpected error occurred.", error.message)
    }

    @Test
    fun `mapErrorType unknown string returns UnknownError`() {
        val error = mapErrorType("some-unknown-error-type")
        assertTrue(error is DomainError.UnknownError)
        assertEquals("some-unknown-error-type", error.message)
    }

    @Test
    fun `mapErrorType empty string returns UnknownError`() {
        val error = mapErrorType("")
        assertTrue(error is DomainError.UnknownError)
        // Falls through to else: empty string is not null, so it uses "" as the message
        assertEquals("", error.message)
    }
}
