package com.example.currencyconverter.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import org.junit.Rule
import org.junit.Test

class AdaptiveLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun adaptiveLayoutRendersBothContentAreas() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                AdaptiveLayout(
                    narrowLeftContent = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            androidx.compose.material3.Text("Left Content")
                        }
                    },
                    rightContent = {
                        androidx.compose.material3.Text("Right Content")
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Left Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Right Content").assertIsDisplayed()
    }
}
