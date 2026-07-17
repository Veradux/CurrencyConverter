package com.example.currencyconverter.presentation.currencyconversion

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.currencyconverter.R
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.util.CurrencyFlagProvider
import com.example.currencyconverter.presentation.components.AdaptiveLayout
import com.example.currencyconverter.presentation.components.CurrencyKeypad
import com.example.currencyconverter.presentation.components.ErrorContent
import com.example.currencyconverter.presentation.components.LoadingContent
import com.example.currencyconverter.presentation.components.SwapCurrenciesButton
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import com.example.currencyconverter.presentation.theme.CurrencyConverterGradientBackground
import org.koin.androidx.compose.koinViewModel
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConversionScreen(
    navController: NavController,
    viewModel: CurrencyConversionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ConversionTopBar(onBackClick = { navController.popBackStack() })
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.systemBars
                .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                AdaptiveLayout(
                    narrowLeftContent = {
                        ConversionSection(
                            uiState = uiState,
                            onSwapCurrencies = viewModel::onSwapCurrencies
                        )
                    },
                    rightContent = {
                        CurrencyKeypad(
                            onDigitClick = viewModel::onAmountDigit,
                            onDecimalClick = viewModel::onAmountDecimal,
                            onBackspaceClick = viewModel::onAmountBackspace,
                            modifier = Modifier.widthIn(max = 600.dp)
                        )
                    }
                )

                ConversionOverlays(
                    uiState = uiState,
                    onRetry = viewModel::onRetry
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversionTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        modifier = Modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun ConversionSection(
    uiState: CurrencyConversionUiState,
    onSwapCurrencies: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Source currency
            CurrencyConversionCard(
                flagEmoji = CurrencyFlagProvider.flagFor(uiState.fromCurrency.value),
                currencyCode = uiState.fromCurrency.value.ifEmpty { "---" },
                amount = uiState.sourceAmount.ifEmpty { "0" },
                isSource = true,
                amountColor = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))
            SwapCurrenciesButton(onClick = onSwapCurrencies)
            Spacer(modifier = Modifier.height(16.dp))

            // Target currency
            val convertedAmount = when (val status = uiState.conversionStatus) {
                is ConversionStatus.Success -> status.quote.convertedAmount
                    .setScale(2, RoundingMode.HALF_UP).toPlainString()
                else -> ""
            }

            CurrencyConversionCard(
                flagEmoji = CurrencyFlagProvider.flagFor(uiState.toCurrency.value),
                currencyCode = uiState.toCurrency.value.ifEmpty { "---" },
                amount = convertedAmount.ifEmpty { "0" },
                isSource = false,
                amountColor = MaterialTheme.colorScheme.onBackground,
                isLoading = uiState.conversionStatus is ConversionStatus.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            ConversionRateDisplay(
                conversionStatus = uiState.conversionStatus,
                lastQuote = uiState.lastQuote
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ConversionRateDisplay(
    conversionStatus: ConversionStatus,
    lastQuote: ConversionQuote?
) {
    when (conversionStatus) {
        is ConversionStatus.Success -> {
            ConversionRateInfo(quote = conversionStatus.quote)
        }
        is ConversionStatus.Error -> {
            if (conversionStatus.hasPreviousResult) {
                lastQuote?.let { quote ->
                    ConversionRateInfo(quote = quote)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = conversionStatus.error.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = CurrencyConverterTheme.colors.accentCoral,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        else -> { /* idle / loading — nothing to show */ }
    }
}

@Composable
private fun ConversionOverlays(
    uiState: CurrencyConversionUiState,
    onRetry: () -> Unit
) {
    val status = uiState.conversionStatus
    val hasCachedResult = uiState.lastQuote != null

    if (status is ConversionStatus.Error && !status.hasPreviousResult && !hasCachedResult) {
        ErrorContent(
            message = status.error.message,
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize()
        )
    }

    if (status is ConversionStatus.Loading && !hasCachedResult) {
        LoadingContent(
            message = stringResource(R.string.currency_conversion_loading_message),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@VisibleForTesting
internal fun CurrencyConversionCard(
    modifier: Modifier = Modifier,
    flagEmoji: String,
    currencyCode: String,
    amount: String,
    isSource: Boolean,
    amountColor: Color,
    isLoading: Boolean = false
) {
    val desc = if (isSource) {
        "Source currency: $currencyCode, amount: $amount"
    } else {
        "Target currency: $currencyCode, amount: $amount"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = desc },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Flag + currency code row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = flagEmoji,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Amount
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 48.sp),
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@VisibleForTesting
internal fun ConversionRateInfo(
    quote: ConversionQuote,
    modifier: Modifier = Modifier
) {
    val fromCode = quote.fromCurrency.value
    val toCode = quote.toCurrency.value
    val rate = quote.conversionRate.setScale(2, RoundingMode.HALF_UP).toPlainString()
    val inverseRate = quote.inverseRate.setScale(2, RoundingMode.HALF_UP).toPlainString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CurrencyConverterTheme.shapes.large)
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "1 $fromCode = $rate $toCode",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "1 $toCode = $inverseRate $fromCode",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "Portrait Light")
@Composable
private fun CurrencyConversionScreenPortraitLightPreview() {
    CurrencyConverterTheme(darkTheme = false) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CurrencyConversionCard(
                        flagEmoji = "🇺🇸",
                        currencyCode = "USD",
                        amount = "100.00",
                        isSource = true,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyConversionCard(
                        flagEmoji = "🇪🇺",
                        currencyCode = "EUR",
                        amount = "92.50",
                        isSource = false,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Portrait Dark")
@Composable
private fun CurrencyConversionScreenPortraitDarkPreview() {
    CurrencyConverterTheme(darkTheme = true) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CurrencyConversionCard(
                    flagEmoji = "🇺🇸",
                    currencyCode = "USD",
                    amount = "100.00",
                    isSource = true,
                    amountColor = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                CurrencyConversionCard(
                    flagEmoji = "🇪🇺",
                    currencyCode = "EUR",
                    amount = "92.50",
                    isSource = false,
                    amountColor = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Landscape Light",
    device = "spec:width=800dp,height=400dp,dpi=420,orientation=landscape"
)
@Composable
private fun CurrencyConversionScreenLandscapeLightPreview() {
    CurrencyConverterTheme(darkTheme = false) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CurrencyConversionCard(
                        flagEmoji = "🇬🇧",
                        currencyCode = "GBP",
                        amount = "50.00",
                        isSource = true,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CurrencyConversionCard(
                        flagEmoji = "🇯🇵",
                        currencyCode = "JPY",
                        amount = "8250.00",
                        isSource = false,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun CurrencyConversionScreenLoadingPreview() {
    CurrencyConverterTheme(darkTheme = true) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Converting...", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun CurrencyConversionScreenErrorPreview() {
    CurrencyConverterTheme(darkTheme = true) {
        CurrencyConverterGradientBackground(modifier = Modifier.fillMaxSize()) {
            ErrorContent(
                message = "Unable to connect. Please check your internet connection.",
                onRetry = {}
            )
        }
    }
}
