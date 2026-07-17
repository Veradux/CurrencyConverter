package com.example.currencyconverter.presentation.currencyinput

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import org.junit.Rule
import org.junit.Test

class CurrencyInputScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersWithFromAndToCurrencySelectors() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "",
                    isValid = false
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

        // Verify from/to selectors appear
        composeTestRule.onNodeWithText("From").assertIsDisplayed()
        composeTestRule.onNodeWithText("To").assertIsDisplayed()
    }

    @Test
    fun amountDisplayShowsEnteredText() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "123",
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

        composeTestRule.onNodeWithText("123").assertIsDisplayed()
    }

    @Test
    fun continueButtonEnabledWhenValid() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "100",
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

        composeTestRule.onNodeWithText("Continue").assertIsEnabled()
    }

    @Test
    fun continueButtonDisabledWhenInvalid() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "0",
                    isValid = false,
                    validationError = "Amount must be greater than zero"
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

        composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()
    }

    @Test
    fun validationErrorShownWhenPresent() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    amount = "100",
                    isValid = false,
                    validationError = "Please select different currencies"
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

        composeTestRule.onNodeWithText("Please select different currencies").assertIsDisplayed()
    }

    @Test
    fun swapCurrenciesButtonIsDisplayed() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "",
                    isValid = false
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

        composeTestRule.onNodeWithContentDescription("Swap currencies").assertIsDisplayed()
    }

    @Test
    fun keypadDigitsAreDisplayed() {
        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "",
                    isValid = false
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

        // Verify keypad digits are present
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("9").assertIsDisplayed()
    }

    @Test
    fun keypadDigitClickInvokesCallback() {
        var clickedDigit: Char? = null

        composeTestRule.setContent {
            CurrencyInputContent(
                uiState = CurrencyInputUiState(
                    fromCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    toCurrency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    amount = "",
                    isValid = false
                ),
                onFromCurrencyClick = {},
                onToCurrencyClick = {},
                onSwapCurrencies = {},
                onDigitClick = { clickedDigit = it },
                onDecimalClick = {},
                onBackspaceClick = {},
                onContinueClick = {}
            )
        }

        composeTestRule.onNodeWithText("7").performClick()
        assert(clickedDigit == '7')
    }
}
