package com.software.financetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    // Primary — filled CTAs, FAB, interactive controls
    primary = GreenAccent,
    onPrimary = TextOnDark,
    primaryContainer = GreenLight,
    onPrimaryContainer = HouseGreen,
    // Secondary — section headers, secondary actions
    secondary = StarbucksGreen,
    onSecondary = TextOnDark,
    secondaryContainer = GreenLight,
    onSecondaryContainer = HouseGreen,
    // Tertiary — rewards / ceremony highlights (Gold reserved per design spec)
    tertiary = Gold,
    onTertiary = HouseGreen,
    tertiaryContainer = GoldLightest,
    onTertiaryContainer = HouseGreen,
    // Canvas & surfaces
    background = NeutralWarm,
    onBackground = TextOnLight,
    surface = White,
    onSurface = TextOnLight,
    surfaceVariant = Ceramic,
    onSurfaceVariant = TextOnLightSoft,
    outline = GreenUplift,
    outlineVariant = GreenLight,
    // Semantic
    error = ErrorRed,
    onError = TextOnDark,
    errorContainer = GoldLightest,
    onErrorContainer = ErrorRed,
)

// Dark theme maps the deep House Green (#1E3932) as the canvas — matching the
// design doc's dark-green feature bands and footer surfaces.
private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = HouseGreen,
    primaryContainer = StarbucksGreen,
    onPrimaryContainer = GreenLight,
    secondary = GoldLight,
    onSecondary = HouseGreen,
    secondaryContainer = GreenUplift,
    onSecondaryContainer = GoldLight,
    tertiary = Gold,
    onTertiary = HouseGreen,
    tertiaryContainer = GreenUplift,
    onTertiaryContainer = GoldLight,
    background = HouseGreen,
    onBackground = TextOnDark,
    surface = GreenUplift,
    onSurface = TextOnDark,
    surfaceVariant = RewardsGreen,
    onSurfaceVariant = TextOnDarkSoft,
    outline = GreenLight,
    outlineVariant = GreenUplift,
    error = ErrorRed,
    onError = TextOnDark,
    errorContainer = GreenUplift,
    onErrorContainer = GoldLightest,
)

@Composable
fun FinanceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
