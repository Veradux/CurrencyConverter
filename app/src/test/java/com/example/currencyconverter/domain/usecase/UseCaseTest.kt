package com.example.currencyconverter.domain.usecase

import com.example.currencyconverter.FakeExchangeRatesRepository
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.DomainError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    // ==================== ConvertCurrencyUseCase ====================

    @Test
    fun `ConvertCurrencyUseCase delegates to repository convertCurrency`() = runTest(testDispatcher) {
        val fakeRepository = FakeExchangeRatesRepository()
        val expectedQuote = ConversionQuote(
            fromCurrency = CurrencyCode("EUR"),
            toCurrency = CurrencyCode("USD"),
            sourceAmount = BigDecimal("100"),
            convertedAmount = BigDecimal("108"),
            conversionRate = BigDecimal("1.08"),
            inverseRate = BigDecimal.ONE.divide(BigDecimal("1.08"), 10, java.math.RoundingMode.HALF_UP),
        )
        fakeRepository.conversionResult = CurrencyResult.Success(expectedQuote)

        val useCase = ConvertCurrencyUseCase(fakeRepository)
        val result = useCase(CurrencyCode("EUR"), CurrencyCode("USD"), BigDecimal("100"))

        assertTrue(result is CurrencyResult.Success)
        assertEquals(expectedQuote, (result as CurrencyResult.Success).data)
        assertEquals(1, fakeRepository.convertCurrencyCallCount)
    }

    @Test
    fun `ConvertCurrencyUseCase returns error from repository`() = runTest(testDispatcher) {
        val fakeRepository = FakeExchangeRatesRepository()
        fakeRepository.shouldThrowNetworkError = true

        val useCase = ConvertCurrencyUseCase(fakeRepository)
        val result = useCase(CurrencyCode("EUR"), CurrencyCode("USD"), BigDecimal("100"))

        assertTrue(result is CurrencyResult.Error)
        assertTrue((result as CurrencyResult.Error).error is DomainError.NetworkError)
        assertEquals(1, fakeRepository.convertCurrencyCallCount)
    }

    // ==================== GetSupportedCurrenciesUseCase ====================

    @Test
    fun `GetSupportedCurrenciesUseCase delegates to repository getSupportedCurrencies`() = runTest(testDispatcher) {
        val fakeRepository = FakeExchangeRatesRepository()
        val expectedCurrencies = listOf(
            Currency(CurrencyCode("EUR"), "Euro", "🇪🇺"),
            Currency(CurrencyCode("USD"), "United States Dollar", "🇺🇸")
        )
        fakeRepository.currencies = expectedCurrencies

        val useCase = GetSupportedCurrenciesUseCase(fakeRepository)
        val result = useCase()

        assertTrue(result is CurrencyResult.Success)
        assertEquals(expectedCurrencies, (result as CurrencyResult.Success).data)
        assertEquals(1, fakeRepository.getSupportedCurrenciesCallCount)
    }

    @Test
    fun `GetSupportedCurrenciesUseCase delegates to repository with forceRefresh false by default`() = runTest(testDispatcher) {
        val fakeRepository = FakeExchangeRatesRepository()
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("GBP"), "British Pound Sterling", "🇬🇧")
        )

        val useCase = GetSupportedCurrenciesUseCase(fakeRepository)
        useCase()

        assertEquals(1, fakeRepository.getSupportedCurrenciesCallCount)
    }

    @Test
    fun `GetSupportedCurrenciesUseCase delegates to repository with forceRefresh true`() = runTest(testDispatcher) {
        val fakeRepository = FakeExchangeRatesRepository()
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("GBP"), "British Pound Sterling", "🇬🇧")
        )

        val useCase = GetSupportedCurrenciesUseCase(fakeRepository)
        val result = useCase(forceRefresh = true)

        assertTrue(result is CurrencyResult.Success)
        assertEquals(1, fakeRepository.getSupportedCurrenciesCallCount)
    }

    @Test
    fun `GetSupportedCurrenciesUseCase returns error from repository`() = runTest(testDispatcher) {
        val fakeRepository = FakeExchangeRatesRepository()
        fakeRepository.shouldThrowApiError = true

        val useCase = GetSupportedCurrenciesUseCase(fakeRepository)
        val result = useCase()

        assertTrue(result is CurrencyResult.Error)
        assertTrue((result as CurrencyResult.Error).error is DomainError.ApiError)
        assertEquals("test-error", (result.error as DomainError.ApiError).code)
        assertEquals(1, fakeRepository.getSupportedCurrenciesCallCount)
    }
}
