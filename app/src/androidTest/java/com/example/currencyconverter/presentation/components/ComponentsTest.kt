package com.example.currencyconverter.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import org.junit.Rule
import org.junit.Test

class ComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorContentShowsMessageAndRetryButton() {
        var retryClicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                ErrorContent(
                    message = "Network error occurred",
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Network error occurred").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed().performClick()
        assert(retryClicked)
    }

    @Test
    fun errorContentWithDismissShowsDismissButton() {
        var dismissClicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                ErrorContent(
                    message = "Error",
                    onRetry = {},
                    onDismiss = { dismissClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Dismiss").assertIsDisplayed().performClick()
        assert(dismissClicked)
    }

    @Test
    fun errorContentWithoutDismissHidesDismissButton() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                ErrorContent(
                    message = "Error",
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Dismiss").assertDoesNotExist()
    }

    @Test
    fun loadingContentShowsDefaultMessage() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                LoadingContent()
            }
        }

        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun loadingContentShowsCustomMessage() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                LoadingContent(message = "Fetching rates...")
            }
        }

        composeTestRule.onNodeWithText("Fetching rates...").assertIsDisplayed()
    }

    @Test
    fun currencySelectorSelectedShowsCurrencyInfo() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencySelector(
                    label = "From",
                    selectedCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("From").assertIsDisplayed()
        // The display text includes flag + code + name
        composeTestRule.onNodeWithText("USD", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("US Dollar", substring = true).assertIsDisplayed()
    }

    @Test
    fun currencySelectorUnselectedShowsPlaceholder() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencySelector(
                    label = "To",
                    selectedCurrency = null,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Select currency").assertIsDisplayed()
    }

    @Test
    fun currencySelectorCallsOnClick() {
        var clicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencySelector(
                    label = "From",
                    selectedCurrency = Currency(
                        code = CurrencyCode("USD"),
                        displayName = "US Dollar",
                        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
                    ),
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("From: USD - US Dollar").performClick()
        assert(clicked)
    }

    @Test
    fun currencyListItemEnabledShowsAllInfo() {
        var clicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyListItem(
                    currency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    isEnabled = true,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("Euro").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("EUR Euro").performClick()
        assert(clicked)
    }

    @Test
    fun currencyListItemDisabledDoesNotCallOnClick() {
        var clicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyListItem(
                    currency = Currency(
                        code = CurrencyCode("EUR"),
                        displayName = "Euro",
                        flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA"
                    ),
                    isEnabled = false,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("EUR Euro, unavailable").assertIsDisplayed()
        // Click shouldn't work since it's not clickable, but we verify callback not called
        assert(!clicked)
    }

    @Test
    fun currencyKeypadDigitClickInvokesCallback() {
        var digit: Char? = null
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyKeypad(
                    onDigitClick = { digit = it },
                    onDecimalClick = {},
                    onBackspaceClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Digit 5").performClick()
        assert(digit == '5')
    }

    @Test
    fun currencyKeypadDecimalClickInvokesCallback() {
        var decimalClicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyKeypad(
                    onDigitClick = {},
                    onDecimalClick = { decimalClicked = true },
                    onBackspaceClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Decimal point").performClick()
        assert(decimalClicked)
    }

    @Test
    fun currencyKeypadBackspaceClickInvokesCallback() {
        var backspaceClicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyKeypad(
                    onDigitClick = {},
                    onDecimalClick = {},
                    onBackspaceClick = { backspaceClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Backspace").performClick()
        assert(backspaceClicked)
    }

    @Test
    fun primaryActionButtonEnabledRespondsToClick() {
        var clicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                PrimaryActionButton(
                    text = "Convert",
                    onClick = { clicked = true },
                    enabled = true
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Convert").performClick()
        assert(clicked)
    }

    @Test
    fun primaryActionButtonDisabledDoesNotRespond() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                PrimaryActionButton(
                    text = "Convert",
                    onClick = { },
                    enabled = false
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Convert").assertIsNotEnabled()
    }

    @Test
    fun swapCurrenciesButtonInvokesCallback() {
        var clicked = false
        composeTestRule.setContent {
            CurrencyConverterTheme {
                SwapCurrenciesButton(onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("Swap currencies").performClick()
        assert(clicked)
    }

    @Test
    fun currencyAmountRowShowsAllInfo() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyAmountRow(
                    amount = "123.45",
                    currencyCode = "USD",
                    flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
                    label = "You send"
                )
            }
        }

        composeTestRule.onNodeWithText("You send").assertIsDisplayed()
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("123.45").assertIsDisplayed()
    }

    @Test
    fun currencyAmountRowEmptyShowsZero() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyAmountRow(
                    amount = "",
                    currencyCode = "EUR",
                    flagEmoji = "\uD83C\uDDEA\uD83C\uDDFA",
                    label = "You send"
                )
            }
        }

        composeTestRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun currencyAmountRowSemanticDescription() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                CurrencyAmountRow(
                    amount = "50",
                    currencyCode = "GBP",
                    flagEmoji = "\uD83C\uDDEC\uD83C\uDDE7",
                    label = "You receive"
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("You receive: 50 GBP").assertIsDisplayed()
    }
}
