package com.example.currencyconverter.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

@Immutable
data class AppColors(
    val gradientStart: Color,
    val gradientMiddle: Color,
    val gradientEnd: Color,
    val accentCoral: Color,
    val accentTeal: Color,
    val keypadBackground: Color,
    val keypadForeground: Color,
    val amountDecrease: Color,
    val amountIncrease: Color,
)

@Immutable
data class AppTypography(
    val amountLarge: TextStyle,
    val amountMedium: TextStyle,
)

@Immutable
data class AppShapes(
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val keypad: Shape,
    val circularButton: Shape,
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        gradientStart = Color.Unspecified,
        gradientMiddle = Color.Unspecified,
        gradientEnd = Color.Unspecified,
        accentCoral = Color.Unspecified,
        accentTeal = Color.Unspecified,
        keypadBackground = Color.Unspecified,
        keypadForeground = Color.Unspecified,
        amountDecrease = Color.Unspecified,
        amountIncrease = Color.Unspecified,
    )
}

val LocalAppTypography = staticCompositionLocalOf {
    AppTypography(
        amountLarge = TextStyle.Default,
        amountMedium = TextStyle.Default,
    )
}

val LocalAppShapes = staticCompositionLocalOf {
    AppShapes(
        small = RoundedCornerShape(0.dp),
        medium = RoundedCornerShape(0.dp),
        large = RoundedCornerShape(0.dp),
        keypad = RoundedCornerShape(0.dp),
        circularButton = CircleShape,
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkSecondary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = DarkTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline.copy(alpha = 0.5f),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkError.copy(alpha = 0.15f),
    onErrorContainer = DarkError,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    scrim = DarkScrim,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightSecondary,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightSurfaceVariant,
    onTertiaryContainer = LightTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutline.copy(alpha = 0.5f),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightError.copy(alpha = 0.1f),
    onErrorContainer = LightError,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    scrim = LightScrim,
)

/**
 * The main theme for the Currency Converter app.
 *
 * Provides both [MaterialTheme] for standard M3 tokens and custom
 * [AppColors], [AppTypography], and [AppShapes] via [CompositionLocalProvider].
 */
@Composable
fun CurrencyConverterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Build Material3 color schemes (dynamic or static)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Build custom app tokens based on theme
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val appTypography = AppTypography(
        amountLarge = TextStyle(
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        ),
        amountMedium = TextStyle(
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        ),
    )
    val appShapes = AppShapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        keypad = RoundedCornerShape(12.dp),
        circularButton = CircleShape,
    )

    // Set system bar icon colors appropriately for edge-to-edge
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        SideEffect {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    // Provide both MaterialTheme AND custom tokens
    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppTypography provides appTypography,
        LocalAppShapes provides appShapes,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CurrencyConverterTypography,
            content = content,
        )
    }
}

/**
 * Convenience object for accessing custom theme tokens.
 */
object CurrencyConverterTheme {
    val colors: AppColors
        @Composable get() = LocalAppColors.current
    val typography: AppTypography
        @Composable get() = LocalAppTypography.current
    val shapes: AppShapes
        @Composable get() = LocalAppShapes.current
}

@Composable
fun CurrencyConverterGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = CurrencyConverterTheme.colors
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(colors.gradientStart, colors.gradientMiddle, colors.gradientEnd),
            ),
        ),
        content = content,
    )
}
