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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    LaunchedEffect(target) {
        viewModel.setOppositeCurrency(oppositeCurrency)
    }

    LaunchedEffect(Unit) {
        viewModel.loadCurrencies()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = CurrencyConverterTheme.shapes.large,
        dragHandle = {
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
                        .semantics { contentDescription = "Drag handle" }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .widthIn(max = 480.dp)
        ) {
            // Title
            Text(
                text = "Select Currency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Opposite currency banner
            if (oppositeCurrency != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already selected for other side:",
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

            Spacer(modifier = Modifier.height(8.dp))

            // Search field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Search currencies" },
                placeholder = { Text("Search currencies") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
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

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            when {
                uiState.isLoading -> {
                    LoadingContent(
                        message = "Loading currencies...",
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.error != null -> {
                    ErrorContent(
                        message = uiState.error!!.message,
                        onRetry = viewModel::onRetry,
                        modifier = Modifier.weight(1f)
                    )
                }

                filteredCurrencies.isEmpty() && !uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No currencies available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = filteredCurrencies,
                            key = { it.code.value }
                        ) { currency ->
                            val isOpposite =
                                oppositeCurrency != null && currency.code == oppositeCurrency.code
                            CurrencyListItem(
                                currency = currency,
                                isEnabled = !isOpposite,
                                onClick = {
                                    if (!isOpposite) {
                                        onCurrencySelected(currency)
                                    }
                                }
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
