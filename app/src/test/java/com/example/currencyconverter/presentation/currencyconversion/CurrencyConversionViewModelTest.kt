package com.example.currencyconverter.presentation.currencyconversion

import androidx.lifecycle.SavedStateHandle
import com.example.currencyconverter.FakeExchangeRatesRepository
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.usecase.ConvertCurrencyUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import java.math.BigDecimal
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConversionViewModelTest {

    private lateinit var fakeRepository: FakeExchangeRatesRepository
    private lateinit var convertCurrencyUseCase: ConvertCurrencyUseCase
    private val testDispatcher = StandardTestDispatcher()

    private val usd = CurrencyCode("USD")
    private val eur = CurrencyCode("EUR")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeExchangeRatesRepository()
        convertCurrencyUseCase = ConvertCurrencyUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        fromCode: String = "USD",
        toCode: String = "EUR",
        amount: String = "100"
    ): CurrencyConversionViewModel {
        val savedStateHandle = SavedStateHandle().apply {
            set("fromCode", fromCode)
            set("toCode", toCode)
            set("amount", amount)
        }
        return CurrencyConversionViewModel(savedStateHandle, convertCurrencyUseCase)
    }

    @Test
    fun initialStateFromSavedStateHandleArgs() = runTest(testDispatcher) {
        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "50")

        val state = viewModel.uiState.value
        assertEquals("USD", state.fromCurrency.value)
        assertEquals("EUR", state.toCurrency.value)
        assertEquals("50", state.sourceAmount)
    }

    @Test
    fun initialConversionRequestFires() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "100")

        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.convertCurrencyCallCount)

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Success)
        val quote = (status as ConversionStatus.Success).quote
        assertEquals(BigDecimal("92.50"), quote.convertedAmount)
    }

    @Test
    fun successfulConversionDisplaysQuoteAndRates() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Success)
        val quote = (status as ConversionStatus.Success).quote
        assertEquals(BigDecimal("100"), quote.sourceAmount)
        assertEquals(BigDecimal("92.50"), quote.convertedAmount)
        assertEquals(BigDecimal("0.925"), quote.conversionRate)
        assertEquals(BigDecimal("1.0810810810"), quote.inverseRate)
    }

    @Test
    fun directAndInverseRatesCorrect() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("1"),
                convertedAmount = BigDecimal("0.925"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "1")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Success)
        val quote = (status as ConversionStatus.Success).quote
        assertEquals(BigDecimal("0.925"), quote.conversionRate)
        assertEquals(BigDecimal("1.0810810810"), quote.inverseRate)
    }

    @Test
    fun zeroAmountPreventsRequest() = runTest(testDispatcher) {
        createViewModel(amount = "0")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertEquals(0, fakeRepository.convertCurrencyCallCount)
    }

    @Test
    fun debouncingRapidChangesOnlyTriggersOneRequest() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "1")

        advanceTimeBy(200.milliseconds)

        viewModel.onAmountDigit('0')
        viewModel.onSourceAmountChanged("10")
        advanceTimeBy(200.milliseconds)

        viewModel.onAmountDigit('0')
        viewModel.onSourceAmountChanged("100")

        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.convertCurrencyCallCount)
    }

    @Test
    fun networkErrorLeadsToErrorState() = runTest(testDispatcher) {
        fakeRepository.shouldThrowNetworkError = true

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Error)
        assertTrue((status as ConversionStatus.Error).error is DomainError.NetworkError)
        assertFalse(status.hasPreviousResult)
    }

    @Test
    fun apiErrorLeadsToErrorState() = runTest(testDispatcher) {
        fakeRepository.shouldThrowApiError = true

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Error)
        assertTrue((status as ConversionStatus.Error).error is DomainError.ApiError)
    }

    @Test
    fun retryTriggersNewRequest() = runTest(testDispatcher) {
        fakeRepository.shouldThrowNetworkError = true

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.convertCurrencyCallCount)

        fakeRepository.shouldThrowNetworkError = false
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        viewModel.onRetry()
        advanceUntilIdle()

        assertEquals(2, fakeRepository.convertCurrencyCallCount)
        assertTrue(viewModel.uiState.value.conversionStatus is ConversionStatus.Success)
    }

    @Test
    fun currencySwapExchangesCodes() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        viewModel.onSwapCurrencies()

        val state = viewModel.uiState.value
        assertEquals("EUR", state.fromCurrency.value)
        assertEquals("USD", state.toCurrency.value)
    }

    @Test
    fun swapBeforeResultExists() = runTest(testDispatcher) {
        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "100")

        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        viewModel.onSwapCurrencies()

        val state = viewModel.uiState.value
        assertEquals("EUR", state.fromCurrency.value)
        assertEquals("USD", state.toCurrency.value)
    }

    @Test
    fun subsequentErrorAfterSuccessKeepsPreviousResult() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.conversionStatus is ConversionStatus.Success)
        assertNotNull(viewModel.uiState.value.lastQuote)

        fakeRepository.shouldThrowNetworkError = true
        viewModel.onSourceAmountChanged("200")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Error)
        assertTrue((status as ConversionStatus.Error).hasPreviousResult)
        assertNotNull(viewModel.uiState.value.lastQuote)
    }

    @Test
    fun invalidAmountFormatDoesNotTriggerConversion() = runTest(testDispatcher) {
        createViewModel(fromCode = "USD", toCode = "EUR", amount = "")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertEquals(0, fakeRepository.convertCurrencyCallCount)
    }

    @Test
    fun onAmountBackspaceUpdatesState() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("10"),
                convertedAmount = BigDecimal("9.25"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        viewModel.onAmountBackspace()
        assertEquals("10", viewModel.uiState.value.sourceAmount)

        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        val status = viewModel.uiState.value.conversionStatus
        assertTrue(status is ConversionStatus.Success)
    }

    @Test
    fun onAmountDecimalHandlesEmptyCurrent() = runTest(testDispatcher) {
        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "")

        viewModel.onAmountDecimal()
        assertEquals("0.", viewModel.uiState.value.sourceAmount)
    }

    @Test
    fun onAmountDecimalDoesNotDuplicate() = runTest(testDispatcher) {
        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "1.")

        viewModel.onAmountDecimal()
        assertEquals("1.", viewModel.uiState.value.sourceAmount)
    }

    @Test
    fun maxLengthOf15Respected() = runTest(testDispatcher) {
        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "1".repeat(15))

        viewModel.onAmountDigit('2')
        assertEquals("1".repeat(15), viewModel.uiState.value.sourceAmount)
    }

    @Test
    fun invalidAmountFormatDoesNotCrash() = runTest(testDispatcher) {
        createViewModel(amount = "abc")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        assertEquals(0, fakeRepository.convertCurrencyCallCount)
    }

    @Test
    fun swapBeforeConversionKeepsSourceAmount() = runTest(testDispatcher) {
        val viewModel = createViewModel(fromCode = "USD", toCode = "EUR", amount = "100")

        // Swap before debounce triggers - no lastQuote yet
        viewModel.onSwapCurrencies()

        val state = viewModel.uiState.value
        assertEquals("EUR", state.fromCurrency.value)
        assertEquals("USD", state.toCurrency.value)
        assertEquals("100", state.sourceAmount)
    }

    @Test
    fun swapWithLastQuoteUsesConvertedAmount() = runTest(testDispatcher) {
        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = usd,
                toCurrency = eur,
                sourceAmount = BigDecimal("100"),
                convertedAmount = BigDecimal("92.50"),
                conversionRate = BigDecimal("0.925"),
                inverseRate = BigDecimal("1.0810810810")
            )
        )

        val viewModel = createViewModel(amount = "100")
        advanceTimeBy(500.milliseconds)
        advanceUntilIdle()

        fakeRepository.conversionResult = CurrencyResult.Success(
            ConversionQuote(
                fromCurrency = eur,
                toCurrency = usd,
                sourceAmount = BigDecimal("92.50"),
                convertedAmount = BigDecimal("108.11"),
                conversionRate = BigDecimal("1.0810810810"),
                inverseRate = BigDecimal("0.925")
            )
        )

        viewModel.onSwapCurrencies()

        assertEquals("92.50", viewModel.uiState.value.sourceAmount)
    }

    @Test
    fun onAmountBackspaceOnEmptyDoesNothing() = runTest(testDispatcher) {
        val viewModel = createViewModel(amount = "")

        viewModel.onAmountBackspace()
        assertEquals("", viewModel.uiState.value.sourceAmount)
    }
}
