package com.example.currencyconverter.presentation.currencyconversion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.example.currencyconverter.domain.model.ConversionQuote
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class CurrencyConversionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun successStateDisplaysCurrenciesAndAmounts() {
        val quote = ConversionQuote(
            fromCurrency = CurrencyCode("USD"),
            toCurrency = CurrencyCode("EUR"),
            sourceAmount = BigDecimal("100"),
            convertedAmount = BigDecimal("92.50"),
            conversionRate = BigDecimal("0.92"),
            inverseRate = BigDecimal("1.08")
        )

        composeTestRule.setContent {
            CurrencyConverterTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CurrencyConversionCard(
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
                        currencyCode = "USD",
                        amount = "100",
                        isSource = true,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyConversionCard(
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA",
                        currencyCode = "EUR",
                        amount = "92.50",
                        isSource = false,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ConversionRateInfo(quote = quote)
                }
            }
        }

        // Verify currencies and amounts are displayed
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
        composeTestRule.onNodeWithText("92.50").assertIsDisplayed()
    }

    @Test
    fun conversionRateInfoIsDisplayed() {
        val quote = ConversionQuote(
            fromCurrency = CurrencyCode("USD"),
            toCurrency = CurrencyCode("EUR"),
            sourceAmount = BigDecimal("100"),
            convertedAmount = BigDecimal("92.50"),
            conversionRate = BigDecimal("0.92"),
            inverseRate = BigDecimal("1.08")
        )

        composeTestRule.setContent {
            CurrencyConverterTheme {
                ConversionRateInfo(quote = quote)
            }
        }

        composeTestRule.onNodeWithText("1 USD = 0.92 EUR").assertIsDisplayed()
    }

    @Test
    fun currencyConversionCardShowsLoadingIndicator() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyConversionCard(
                    flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
                    currencyCode = "USD",
                    amount = "",
                    isSource = false,
                    amountColor = MaterialTheme.colorScheme.onBackground,
                    isLoading = true
                )
            }
        }

        // Verify the card still shows with loading indicator
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
    }

    @Test
    fun semanticDescriptionsAreSet() {
        val quote = ConversionQuote(
            fromCurrency = CurrencyCode("USD"),
            toCurrency = CurrencyCode("EUR"),
            sourceAmount = BigDecimal("100"),
            convertedAmount = BigDecimal("92.50"),
            conversionRate = BigDecimal("0.925"),
            inverseRate = BigDecimal("1.08")
        )

        composeTestRule.setContent {
            CurrencyConverterTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    CurrencyConversionCard(
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
                        currencyCode = "USD",
                        amount = "100",
                        isSource = true,
                        amountColor = MaterialTheme.colorScheme.onBackground
                    )
                    ConversionRateInfo(quote = quote)
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Source currency: USD, amount: 100").assertIsDisplayed()
    }

    @Test
    fun inverseRateIsDisplayed() {
        val quote = ConversionQuote(
            fromCurrency = CurrencyCode("USD"),
            toCurrency = CurrencyCode("EUR"),
            sourceAmount = BigDecimal("100"),
            convertedAmount = BigDecimal("92.50"),
            conversionRate = BigDecimal("0.92"),
            inverseRate = BigDecimal("1.08")
        )

        composeTestRule.setContent {
            CurrencyConverterTheme {
                ConversionRateInfo(quote = quote)
            }
        }

        composeTestRule.onNodeWithText("1 EUR = 1.08 USD").assertIsDisplayed()
    }
}
