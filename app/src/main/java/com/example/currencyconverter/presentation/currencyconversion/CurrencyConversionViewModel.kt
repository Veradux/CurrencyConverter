package com.example.currencyconverter.presentation.currencyconversion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.usecase.ConvertCurrencyUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.time.Duration.Companion.milliseconds

data class CurrencyConversionUiState(
    val fromCurrency: CurrencyCode = CurrencyCode("AAA"),
    val toCurrency: CurrencyCode = CurrencyCode("AAA"),
    val sourceAmount: String = "",
    val conversionStatus: ConversionStatus = ConversionStatus.Idle,
    val lastQuote: ConversionQuote? = null
)

sealed class ConversionStatus {
    object Idle : ConversionStatus()
    object Loading : ConversionStatus()
    data class Success(val quote: ConversionQuote) : ConversionStatus()
    data class Error(val error: DomainError, val hasPreviousResult: Boolean = false) : ConversionStatus()
}

@OptIn(FlowPreview::class)
class CurrencyConversionViewModel(
    savedStateHandle: SavedStateHandle,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyConversionUiState())
    val uiState: StateFlow<CurrencyConversionUiState> = _uiState.asStateFlow()

    private val _amountFlow = MutableStateFlow("")
    private var conversionJob: Job? = null

    init {
        val fromCode = savedStateHandle.get<String>("fromCode") ?: ""
        val toCode = savedStateHandle.get<String>("toCode") ?: ""
        val amount = savedStateHandle.get<String>("amount") ?: ""

        _uiState.update {
            it.copy(
                fromCurrency = CurrencyCode(fromCode),
                toCurrency = CurrencyCode(toCode),
                sourceAmount = amount
            )
        }

        _amountFlow.value = amount

        viewModelScope.launch {
            _amountFlow
                .debounce(400.milliseconds)
                .collect { tryTriggerConversion(it) }
        }
    }

    fun onSourceAmountChanged(newAmount: String) {
        _uiState.update { it.copy(sourceAmount = newAmount) }
        _amountFlow.value = newAmount
    }

    fun onAmountDigit(digit: Char) {
        val current = _uiState.value.sourceAmount
        if (current.length >= 15) return

        // Enforce max 2 decimal places
        val decimalIndex = current.indexOf('.')
        if (decimalIndex >= 0 && current.length - decimalIndex > 2) return

        onSourceAmountChanged(current + digit)
    }

    fun onAmountDecimal() {
        val current = _uiState.value.sourceAmount
        if (current.contains('.')) return
        val newAmount = if (current.isEmpty()) "0." else "$current."
        onSourceAmountChanged(newAmount)
    }

    fun onAmountBackspace() {
        val current = _uiState.value.sourceAmount
        if (current.isEmpty()) return
        val newAmount = current.dropLast(1)
        onSourceAmountChanged(newAmount)
    }

    fun onSwapCurrencies() {
        conversionJob?.cancel()

        _uiState.update { state ->
            val newSourceAmount = state.lastQuote?.convertedAmount
                ?.setScale(2, RoundingMode.HALF_UP)
                ?.toPlainString()
                ?: state.sourceAmount

            state.copy(
                fromCurrency = state.toCurrency,
                toCurrency = state.fromCurrency,
                sourceAmount = newSourceAmount,
                lastQuote = null,
                conversionStatus = ConversionStatus.Idle
            )
        }

        _amountFlow.value = _uiState.value.sourceAmount
        tryTriggerConversion(_uiState.value.sourceAmount)
    }

    fun onRetry() {
        _uiState.update { it.copy(conversionStatus = ConversionStatus.Loading) }
        tryTriggerConversion(_uiState.value.sourceAmount)
    }

    private fun tryTriggerConversion(amountStr: String) {
        if (amountStr.isEmpty()) return
        val parsed = try {
            BigDecimal(amountStr)
        } catch (_: NumberFormatException) {
            null
        }
        if (parsed != null && parsed > BigDecimal.ZERO) {
            performConversion(parsed)
        }
    }

    private fun performConversion(amount: BigDecimal) {
        val from = _uiState.value.fromCurrency
        val to = _uiState.value.toCurrency

        if (from.value.isEmpty() || to.value.isEmpty()) return

        conversionJob?.cancel()
        conversionJob = viewModelScope.launch {
            val hasPreviousResult = _uiState.value.lastQuote != null
            _uiState.update { it.copy(conversionStatus = ConversionStatus.Loading) }

            when (val result = convertCurrencyUseCase(from, to, amount)) {
                is CurrencyResult.Success -> {
                    _uiState.update {
                        it.copy(
                            conversionStatus = ConversionStatus.Success(result.data),
                            lastQuote = result.data
                        )
                    }
                }
                is CurrencyResult.Error -> {
                    _uiState.update {
                        it.copy(
                            conversionStatus = ConversionStatus.Error(
                                error = result.error,
                                hasPreviousResult = hasPreviousResult
                            )
                        )
                    }
                }
            }
        }
    }
}
