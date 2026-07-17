package com.example.currencyconverter.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.currencyconverter.presentation.currencyconversion.CurrencyConversionScreen
import com.example.currencyconverter.presentation.currencyinput.CurrencyInputScreen
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme

/**
 * Route constants for the app navigation graph.
 */
object AppRoutes {
    const val CURRENCY_INPUT = "currency_input"
    const val CURRENCY_CONVERSION = "currency_conversion/{fromCode}/{toCode}/{amount}"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    CurrencyConverterTheme {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.CURRENCY_INPUT,
            modifier = modifier
        ) {
            composable(AppRoutes.CURRENCY_INPUT) {
                CurrencyInputScreen(navController = navController)
            }

            composable(
                route = AppRoutes.CURRENCY_CONVERSION,
                arguments = listOf(
                    navArgument("fromCode") { type = NavType.StringType },
                    navArgument("toCode") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.StringType }
                )
            ) {
                CurrencyConversionScreen(navController = navController)
            }
        }
    }
}
