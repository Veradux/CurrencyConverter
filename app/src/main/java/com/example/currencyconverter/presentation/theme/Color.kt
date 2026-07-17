package com.example.currencyconverter.presentation.theme

import androidx.compose.ui.graphics.Color

// Dark theme
val DarkBackground = Color(0xFF0D0B1E)
val DarkSurface = Color(0xFF1A1540)
val DarkSurfaceVariant = Color(0xFF252050)
val DarkPrimary = Color(0xFF9B8BFF)
val DarkOnPrimary = Color(0xFF0D0B1E)
val DarkOnBackground = Color(0xFFF5F0FF)
val DarkOnSurface = Color(0xFFE8E0F0)
val DarkOnSurfaceVariant = Color(0xFFB0A0CC)
val DarkOutline = Color(0xFF6B5B8F)
val DarkSecondary = Color(0xFFC5B8FF)
val DarkError = Color(0xFFFF6B6B)
val DarkOnError = Color(0xFF0D0B1E)
val DarkTertiary = Color(0xFFA0D0FF)
val DarkOnTertiary = Color(0xFF0D1B2E)
val DarkInverseSurface = Color(0xFFE8E0F0)
val DarkInverseOnSurface = Color(0xFF1A1540)
val DarkInversePrimary = Color(0xFF5B4FCF)
val DarkScrim = Color(0xFF000000)

// Light theme
val LightBackground = Color(0xFFF8F6FF)
val LightSurface = Color(0xFFF0EDFF)
val LightSurfaceVariant = Color(0xFFE6E0F5)
val LightPrimary = Color(0xFF5B4FCF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1A1540)
val LightOnSurface = Color(0xFF252050)
val LightOnSurfaceVariant = Color(0xFF5B4F8F)
val LightOutline = Color(0xFFB0A0CC)
val LightSecondary = Color(0xFF7B6FE0)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightError = Color(0xFFD32F2F)
val LightOnError = Color(0xFFFFFFFF)
val LightTertiary = Color(0xFF3B6FCF)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightInverseSurface = Color(0xFF252050)
val LightInverseOnSurface = Color(0xFFF5F0FF)
val LightInversePrimary = Color(0xFF9B8BFF)
val LightScrim = Color(0xFF000000)

/**
 * Dark theme custom app colors.
 * Extends beyond Material3 ColorScheme with app-specific semantic tokens.
 */
val DarkAppColors = AppColors(
    gradientStart = Color(0xFF9378BE),
    gradientMiddle = Color(0xFF3F2D77),
    gradientEnd = Color(0xFF0D0B1E),
    accentCoral = Color(0xFFFF6B6B),
    accentTeal = Color(0xFF4ECDC4),
    keypadBackground = Color(0xFF1A1540),
    keypadForeground = Color(0xFFF5F0FF),
    amountDecrease = Color(0xFFFF6B6B),
    amountIncrease = Color(0xFF4ECDC4),
)

/**
 * Light theme custom app colors.
 */
val LightAppColors = AppColors(
    gradientStart = Color(0xFFFFFFFF),
    gradientMiddle = Color(0xFFEFE8FF),
    gradientEnd = Color(0xFFDDD3FF),
    accentCoral = Color(0xFFE84C4C),
    accentTeal = Color(0xFF3AADA5),
    keypadBackground = Color(0xFFF0EDFF),
    keypadForeground = Color(0xFF1A1540),
    amountDecrease = Color(0xFFE84C4C),
    amountIncrease = Color(0xFF3AADA5),
)
