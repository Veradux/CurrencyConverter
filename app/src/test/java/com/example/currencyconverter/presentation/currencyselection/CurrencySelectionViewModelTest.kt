package com.example.currencyconverter.presentation.currencyselection

import com.example.currencyconverter.FakeExchangeRatesRepository
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.usecase.GetSupportedCurrenciesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencySelectionViewModelTest {

    private lateinit var fakeRepository: FakeExchangeRatesRepository
    private lateinit var getSupportedCurrenciesUseCase: GetSupportedCurrenciesUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeExchangeRatesRepository()
        getSupportedCurrenciesUseCase = GetSupportedCurrenciesUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): CurrencySelectionViewModel {
        return CurrencySelectionViewModel(getSupportedCurrenciesUseCase)
    }

    @Test
    fun initialLoadingState() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
    }

    @Test
    fun successfulCurrencyListLoading() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA"),
            Currency(CurrencyCode("GBP"), "British Pound", "\uD83C\uDDEC\uD83C\uDDE7")
        )

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.currencies.size)
        assertTrue(state.currencies.any { it.code.value == "USD" })
        assertTrue(state.currencies.any { it.code.value == "EUR" })
        assertTrue(state.currencies.any { it.code.value == "GBP" })
    }

    @Test
    fun currenciesStoredInProvidedOrder() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA"),
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("GBP"), "British Pound", "\uD83C\uDDEC\uD83C\uDDE7")
        )

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        val codes = viewModel.uiState.value.currencies.map { it.code.value }
        assertEquals(listOf("EUR", "USD", "GBP"), codes)
    }

    @Test
    fun searchQueryUpdatesUiState() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("us")
        advanceUntilIdle()

        assertEquals("us", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun emptyResponseLeadsToEmptyState() = runTest(testDispatcher) {
        fakeRepository.currencies = emptyList()

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.currencies.isEmpty())
    }

    @Test
    fun networkErrorLeadsToErrorState() = runTest(testDispatcher) {
        fakeRepository.shouldThrowNetworkError = true

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error is DomainError.NetworkError)
    }

    @Test
    fun apiErrorLeadsToErrorState() = runTest(testDispatcher) {
        fakeRepository.shouldThrowApiError = true

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error is DomainError.ApiError)
    }

    @Test
    fun retryTriggersReload() = runTest(testDispatcher) {
        fakeRepository.shouldThrowNetworkError = true

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        fakeRepository.shouldThrowNetworkError = false
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8")
        )

        viewModel.onRetry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.currencies.size)
        assertEquals("USD", state.currencies[0].code.value)
    }

    @Test
    fun callingLoadCurrenciesIncrementsCallCount() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8")
        )

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        assertEquals(1, fakeRepository.getSupportedCurrenciesCallCount)

        viewModel.loadCurrencies(forceRefresh = false)
        advanceUntilIdle()

        assertEquals(2, fakeRepository.getSupportedCurrenciesCallCount)
    }

    @Test
    fun searchQueryCanBeSetAndCleared() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("us")
        advanceUntilIdle()

        assertEquals("us", viewModel.uiState.value.searchQuery)

        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun oppositeCurrencyIsStored() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA"),
            Currency(CurrencyCode("GBP"), "British Pound", "\uD83C\uDDEC\uD83C\uDDE7")
        )

        val viewModel = createViewModel()
        viewModel.loadCurrencies()
        advanceUntilIdle()

        viewModel.setOppositeCurrency(Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA"))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.oppositeCurrency)
        assertEquals("EUR", viewModel.uiState.value.oppositeCurrency?.code?.value)
    }
}
