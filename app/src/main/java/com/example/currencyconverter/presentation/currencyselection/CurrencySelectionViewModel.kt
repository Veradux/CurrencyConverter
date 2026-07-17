package com.example.currencyconverter.presentation.currencyselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyResult
import com.example.currencyconverter.domain.model.DomainError
import com.example.currencyconverter.domain.usecase.GetSupportedCurrenciesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CurrencySelectionUiState(
    val isLoading: Boolean = false,
    val currencies: List<Currency> = emptyList(),
    val searchQuery: String = "",
    val error: DomainError? = null,
    val oppositeCurrency: Currency? = null
)

class CurrencySelectionViewModel(
    private val getSupportedCurrenciesUseCase: GetSupportedCurrenciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencySelectionUiState())
    val uiState: StateFlow<CurrencySelectionUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    val filteredCurrencies: StateFlow<List<Currency>> = combine(
        _uiState,
        _searchQuery
    ) { state, query ->
        val base = state.currencies.filter { currency ->
            state.oppositeCurrency == null || currency.code != state.oppositeCurrency.code
        }
        if (query.isBlank()) {
            base
        } else {
            val q = query.trim().lowercase()
            base.filter { currency ->
                currency.code.value.lowercase().contains(q) ||
                    currency.displayName.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadCurrencies(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getSupportedCurrenciesUseCase(forceRefresh)) {
                is CurrencyResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currencies = result.data,
                            error = null
                        )
                    }
                }
                is CurrencyResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setOppositeCurrency(currency: Currency?) {
        _uiState.update { it.copy(oppositeCurrency = currency) }
    }

    fun onRetry() {
        loadCurrencies(forceRefresh = true)
    }
}
