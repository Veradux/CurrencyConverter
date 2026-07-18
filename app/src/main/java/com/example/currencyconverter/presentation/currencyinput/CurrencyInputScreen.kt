package com.example.currencyconverter.presentation.currencyinput

import android.content.res.Configuration
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.domain.model.CurrencySelectionTarget
import com.example.currencyconverter.presentation.components.*
import com.example.currencyconverter.presentation.currencyselection.CurrencySelectionSheet
import com.example.currencyconverter.presentation.theme.CurrencyConverterGradientBackground
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInputScreen(
    navController: NavController,
    viewModel: CurrencyInputViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var selectionTarget by rememberSaveable { mutableStateOf(CurrencySelectionTarget.FROM) }

    CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
            contentWindowInsets = WindowInsets.systemBars
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ) { innerPadding ->
            when {
                uiState.isLoading -> {
                    LoadingContent(
                        message = "Loading currencies...",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                uiState.error != null -> {
                    ErrorContent(
                        message = uiState.error!!,
                        onRetry = viewModel::retry,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                else -> {
                    CurrencyInputContent(
                        uiState = uiState,
                        onFromCurrencyClick = {
                            selectionTarget = CurrencySelectionTarget.FROM
                            showSheet = true
                        },
                        onToCurrencyClick = {
                            selectionTarget = CurrencySelectionTarget.TO
                            showSheet = true
                        },
                        onSwapCurrencies = viewModel::onSwapCurrencies,
                        onDigitClick = viewModel::onAmountDigit,
                        onDecimalClick = viewModel::onAmountDecimal,
                        onBackspaceClick = viewModel::onAmountBackspace,
                        onContinueClick = {
                            val fromCode =
                                uiState.fromCurrency?.code?.value ?: return@CurrencyInputContent
                            val toCode =
                                uiState.toCurrency?.code?.value ?: return@CurrencyInputContent
                            val amount = uiState.amount
                            navController.navigate("currency_conversion/$fromCode/$toCode/$amount")
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Currency selection bottom sheet
    if (showSheet) {
        val oppositeCurrency = when (selectionTarget) {
            CurrencySelectionTarget.FROM -> uiState.toCurrency
            CurrencySelectionTarget.TO -> uiState.fromCurrency
        }

        CurrencySelectionSheet(
            target = selectionTarget,
            oppositeCurrency = oppositeCurrency,
            onCurrencySelected = { currency ->
                when (selectionTarget) {
                    CurrencySelectionTarget.FROM -> viewModel.onFromCurrencySelected(currency)
                    CurrencySelectionTarget.TO -> viewModel.onToCurrencySelected(currency)
                }
                showSheet = false
            },
            onDismiss = { showSheet = false }
        )
    }
}

@Composable
@VisibleForTesting
internal fun CurrencyInputContent(
    modifier: Modifier = Modifier,
    uiState: CurrencyInputUiState,
    onFromCurrencyClick: () -> Unit,
    onToCurrencyClick: () -> Unit,
    onSwapCurrencies: () -> Unit,
    onDigitClick: (Char) -> Unit,
    onDecimalClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isWide = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
            configuration.screenWidthDp > 600

    val leftContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isWide) Modifier.padding(horizontal = 24.dp)
                    else Modifier
                )
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = !isWide)
                    .let { if (!isWide) it.verticalScroll(scrollState) else it }
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // From currency selector
                CurrencySelector(
                    label = "From",
                    selectedCurrency = uiState.fromCurrency,
                    onClick = onFromCurrencyClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                SwapCurrenciesButton(onClick = onSwapCurrencies)

                Spacer(modifier = Modifier.height(8.dp))

                // To currency selector
                CurrencySelector(
                    label = "To",
                    selectedCurrency = uiState.toCurrency,
                    onClick = onToCurrencyClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Validation error
                val error = uiState.validationError
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Amount display
                    val fromCurrency = uiState.fromCurrency
                    CurrencyAmountRow(
                        amount = uiState.amount,
                        currencyCode = fromCurrency?.code?.value ?: "",
                        flagEmoji = fromCurrency?.flagEmoji ?: "",
                        label = stringResource(R.string.currency_input_empty_field_label),
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Continue button
                    PrimaryActionButton(
                        text = stringResource(R.string.currency_input_convert_button_label),
                        onClick = onContinueClick,
                        enabled = uiState.isValid
                    )
                }

                if (!isWide) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    val keypadContent = @Composable {
        CurrencyKeypad(
            onDigitClick = onDigitClick,
            onDecimalClick = onDecimalClick,
            onBackspaceClick = onBackspaceClick,
            modifier = Modifier.widthIn(max = 600.dp)
        )
    }

    AdaptiveLayout(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        narrowLeftContent = leftContent,
        rightContent = keypadContent
    )
}

@Preview(showBackground = true, name = "Portrait Light")
@Composable
private fun CurrencyInputScreenPortraitLightPreview() {
    CurrencyConverterTheme(darkTheme = false) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "🇺🇸"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "🇪🇺"
                    ),
                    amount = "123.45",
                    isValid = true
                ),
                onFromCurrencyClick = {},
                onToCurrencyClick = {},
                onSwapCurrencies = {},
                onDigitClick = {},
                onDecimalClick = {},
                onBackspaceClick = {},
                onContinueClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Portrait Dark")
@Composable
private fun CurrencyInputScreenPortraitDarkPreview() {
    CurrencyConverterTheme(darkTheme = true) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "🇺🇸"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "🇪🇺"
                    ),
                    amount = "123.45",
                    isValid = true
                ),
                onFromCurrencyClick = {},
                onToCurrencyClick = {},
                onSwapCurrencies = {},
                onDigitClick = {},
                onDecimalClick = {},
                onBackspaceClick = {},
                onContinueClick = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Landscape Light",
    device = "spec:width=800dp,height=400dp,dpi=420,orientation=landscape"
)
@Composable
private fun CurrencyInputScreenLandscapeLightPreview() {
    CurrencyConverterTheme(darkTheme = false) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("GBP"),
                        displayName = "British Pound",
                        flagEmoji = "🇬🇧"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("JPY"),
                        displayName = "Japanese Yen",
                        flagEmoji = "🇯🇵"
                    ),
                    amount = "5000",
                    isValid = true
                ),
                onFromCurrencyClick = {},
                onToCurrencyClick = {},
                onSwapCurrencies = {},
                onDigitClick = {},
                onDecimalClick = {},
                onBackspaceClick = {},
                onContinueClick = {}
            )
        }
    }
}
