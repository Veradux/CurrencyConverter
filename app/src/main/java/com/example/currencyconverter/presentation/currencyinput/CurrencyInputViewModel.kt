package com.example.currencyconverter.presentation.currencyinput

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.usecase.GetSupportedCurrenciesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class CurrencyInputUiState(
    val fromCurrency: Currency? = null,
    val toCurrency: Currency? = null,
    val amount: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isValid: Boolean = false,
    val validationError: String? = null
)

class CurrencyInputViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getSupportedCurrenciesUseCase: GetSupportedCurrenciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CurrencyInputUiState(
            fromCurrency = savedStateHandle.get<Currency>("fromCurrency"),
            toCurrency = savedStateHandle.get<Currency>("toCurrency"),
            amount = savedStateHandle.get<String>("amount") ?: ""
        )
    )
    val uiState: StateFlow<CurrencyInputUiState> = _uiState.asStateFlow()

    init {
        if (_uiState.value.fromCurrency == null || _uiState.value.toCurrency == null) {
            loadCurrencies()
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getSupportedCurrenciesUseCase(forceRefresh = false)) {
                is CurrencyResult.Success -> {
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
                is CurrencyResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getSupportedCurrenciesUseCase(forceRefresh = true)) {
                is CurrencyResult.Success -> {
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
                is CurrencyResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun onFromCurrencySelected(currency: Currency) {
        _uiState.update { state ->
            val newState = state.copy(fromCurrency = currency)
            savedStateHandle["fromCurrency"] = currency
            newState.copy(
                isValid = validate(newState),
                validationError = getValidationError(newState)
            )
        }
    }

    fun onToCurrencySelected(currency: Currency) {
        _uiState.update { state ->
            val newState = state.copy(toCurrency = currency)
            savedStateHandle["toCurrency"] = currency
            newState.copy(
                isValid = validate(newState),
                validationError = getValidationError(newState)
            )
        }
    }

    fun onAmountDigit(digit: Char) {
        _uiState.update { state ->
            val currentAmount = state.amount
            if (currentAmount.length >= 15) return@update state
            // Prevent more than 2 digits after the decimal point
            val decimalIndex = currentAmount.indexOf('.')
            if (decimalIndex >= 0 && currentAmount.length - decimalIndex > 2) return@update state
            val newAmount = currentAmount + digit
            savedStateHandle["amount"] = newAmount
            val newState = state.copy(amount = newAmount)
            newState.copy(
                isValid = validate(newState),
                validationError = getValidationError(newState)
            )
        }
    }

    fun onAmountDecimal() {
        _uiState.update { state ->
            if (state.amount.contains('.')) return@update state
            val newAmount = if (state.amount.isEmpty()) "0." else state.amount + "."
            savedStateHandle["amount"] = newAmount
            val newState = state.copy(amount = newAmount)
            newState.copy(
                isValid = validate(newState),
                validationError = getValidationError(newState)
            )
        }
    }

    fun onAmountBackspace() {
        _uiState.update { state ->
            if (state.amount.isEmpty()) return@update state
            val newAmount = state.amount.dropLast(1)
            savedStateHandle["amount"] = newAmount
            val newState = state.copy(amount = newAmount)
            newState.copy(
                isValid = validate(newState),
                validationError = getValidationError(newState)
            )
        }
    }

    fun onSwapCurrencies() {
        _uiState.update { state ->
            if (state.fromCurrency == null || state.toCurrency == null) return@update state
            val newState = state.copy(
                fromCurrency = state.toCurrency,
                toCurrency = state.fromCurrency
            )
            savedStateHandle["fromCurrency"] = newState.fromCurrency
            savedStateHandle["toCurrency"] = newState.toCurrency
            newState.copy(
                isValid = validate(newState),
                validationError = getValidationError(newState)
            )
        }
    }

    private fun validate(state: CurrencyInputUiState): Boolean {
        return getValidationError(state) == null
    }

    private fun getValidationError(state: CurrencyInputUiState): String? {
        val amount = state.amount
        val fromCurrency = state.fromCurrency
        val toCurrency = state.toCurrency

        if (amount.isEmpty()) return null

        val parsedAmount = try {
            BigDecimal(amount)
        } catch (_: NumberFormatException) {
            return "Invalid amount format"
        }

        if (parsedAmount <= BigDecimal.ZERO) {
            return "Amount must be greater than zero"
        }

        if (fromCurrency != null && toCurrency != null && fromCurrency.code == toCurrency.code) {
            return "Please select different currencies"
        }

        if (fromCurrency == null || toCurrency == null) {
            return null
        }

        return null
    }
}
