package com.example.currencyconverter.presentation.currencyselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencySelectionTarget
import com.example.currencyconverter.presentation.components.CurrencyListItem
import com.example.currencyconverter.presentation.components.ErrorContent
import com.example.currencyconverter.presentation.components.LoadingContent
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionSheet(
    target: CurrencySelectionTarget,
    oppositeCurrency: Currency?,
    onCurrencySelected: (Currency) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CurrencySelectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredCurrencies by viewModel.filteredCurrencies.collectAsState()

    LaunchedEffect(target) { viewModel.setOppositeCurrency(oppositeCurrency) }
    LaunchedEffect(Unit) { viewModel.loadCurrencies() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = CurrencyConverterTheme.shapes.large,
        dragHandle = { BottomSheetDragHandle() }
    ) {
        CurrencySelectionContent(
            uiState = uiState,
            filteredCurrencies = filteredCurrencies,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onRetry = viewModel::onRetry,
            onCurrencySelected = onCurrencySelected
        )
    }
}

@Composable
private fun BottomSheetDragHandle() {
    val dragHandleDesc = stringResource(R.string.drag_handle_desc)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                .semantics { contentDescription = dragHandleDesc }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CurrencySelectionContent(
    uiState: CurrencySelectionUiState,
    filteredCurrencies: List<Currency>,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onCurrencySelected: (Currency) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .widthIn(max = 480.dp)
    ) {
        Text(
            text = stringResource(R.string.select_currency_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        OppositeCurrencyBanner(oppositeCurrency = uiState.oppositeCurrency)

        Spacer(modifier = Modifier.height(8.dp))

        CurrencySearchField(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(12.dp))

        CurrencyListContent(
            isLoading = uiState.isLoading,
            error = uiState.error,
            currencies = filteredCurrencies,
            oppositeCurrency = uiState.oppositeCurrency,
            onRetry = onRetry,
            onCurrencySelected = onCurrencySelected
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun OppositeCurrencyBanner(oppositeCurrency: Currency?) {
    if (oppositeCurrency == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.already_selected_other_side),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = oppositeCurrency.flagEmoji,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = oppositeCurrency.code.value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CurrencySearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val searchDesc = stringResource(R.string.search_currencies)
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = searchDesc },
        placeholder = { Text(searchDesc) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.clear_search_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = CurrencyConverterTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun ColumnScope.CurrencyListContent(
    isLoading: Boolean,
    error: com.example.currencyconverter.domain.model.DomainError?,
    currencies: List<Currency>,
    oppositeCurrency: Currency?,
    onRetry: () -> Unit,
    onCurrencySelected: (Currency) -> Unit
) {
    when {
        isLoading -> {
            LoadingContent(
                message = stringResource(R.string.loading),
                modifier = Modifier.weight(1f)
            )
        }

        error != null -> {
            ErrorContent(
                message = error.message,
                onRetry = onRetry,
                modifier = Modifier.weight(1f)
            )
        }

        currencies.isEmpty() -> {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_currencies_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = currencies, key = { it.code.value }) { currency ->
                    val isOpposite =
                        oppositeCurrency?.let { currency.code == it.code } == true
                    CurrencyListItem(
                        currency = currency,
                        isEnabled = !isOpposite,
                        onClick = { if (!isOpposite) onCurrencySelected(currency) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
