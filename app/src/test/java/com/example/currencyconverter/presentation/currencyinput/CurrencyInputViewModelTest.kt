package com.example.currencyconverter.presentation.currencyinput

import androidx.lifecycle.SavedStateHandle
import com.example.currencyconverter.FakeExchangeRatesRepository
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyInputViewModelTest {

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

    private fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()): CurrencyInputViewModel {
        return CurrencyInputViewModel(savedStateHandle, getSupportedCurrenciesUseCase)
    }

    @Test
    fun initialStateHasDefaultValues() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("USD", state.fromCurrency?.code?.value)
        assertEquals("EUR", state.toCurrency?.code?.value)
        assertEquals("", state.amount)
        assertFalse(state.isValid)
        assertFalse(state.isLoading)
    }

    @Test
    fun validAmountEntryEnablesContinue() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('1')
        viewModel.onAmountDigit('0')
        viewModel.onAmountDigit('0')

        val state = viewModel.uiState.value
        assertEquals("100", state.amount)
        assertTrue(state.isValid)
        assertNull(state.validationError)
    }

    @Test
    fun zeroAmountShowsValidationError() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('0')

        val state = viewModel.uiState.value
        assertFalse(state.isValid)
        assertEquals("Amount must be greater than zero", state.validationError)
    }

    @Test
    fun validAmountPassesValidation() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('1')
        viewModel.onAmountDigit('2')
        viewModel.onAmountDigit('3')

        assertTrue(viewModel.uiState.value.isValid)
    }

    @Test
    fun continueButtonEnabledWhenAllValid() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('5')
        viewModel.onAmountDigit('0')

        assertTrue(viewModel.uiState.value.isValid)
    }

    @Test
    fun continueButtonDisabledWhenInvalid() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('0')

        assertFalse(viewModel.uiState.value.isValid)
    }

    @Test
    fun onAmountDigitAppendsCorrectly() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('1')
        assertEquals("1", viewModel.uiState.value.amount)

        viewModel.onAmountDigit('2')
        assertEquals("12", viewModel.uiState.value.amount)

        viewModel.onAmountDigit('3')
        assertEquals("123", viewModel.uiState.value.amount)
    }

    @Test
    fun onAmountDecimalAddsDot() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('1')
        viewModel.onAmountDecimal()

        assertEquals("1.", viewModel.uiState.value.amount)
    }

    @Test
    fun decimalOnEmptyPrependsZero() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDecimal()

        assertEquals("0.", viewModel.uiState.value.amount)
    }

    @Test
    fun onAmountBackspaceRemovesLastChar() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('1')
        viewModel.onAmountDigit('2')
        viewModel.onAmountDigit('3')
        assertEquals("123", viewModel.uiState.value.amount)

        viewModel.onAmountBackspace()
        assertEquals("12", viewModel.uiState.value.amount)

        viewModel.onAmountBackspace()
        assertEquals("1", viewModel.uiState.value.amount)
    }

    @Test
    fun decimalNotDuplicated() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountDigit('1')
        viewModel.onAmountDecimal()
        viewModel.onAmountDigit('5')
        viewModel.onAmountDecimal()

        assertEquals("1.5", viewModel.uiState.value.amount)
    }

    @Test
    fun maxLengthRespected() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        repeat(15) { viewModel.onAmountDigit('1') }
        assertEquals("1".repeat(15), viewModel.uiState.value.amount)

        viewModel.onAmountDigit('2')
        assertEquals("1".repeat(15), viewModel.uiState.value.amount)
    }

    @Test
    fun backspaceOnEmptyDoesNothing() = runTest(testDispatcher) {
        fakeRepository.currencies = listOf(
            Currency(CurrencyCode("USD"), "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
            Currency(CurrencyCode("EUR"), "Euro", "\uD83C\uDDEA\uD83C\uDDFA")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAmountBackspace()
        assertEquals("", viewModel.uiState.value.amount)
    }
}
