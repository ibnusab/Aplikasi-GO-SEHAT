package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SigmaDarkColorScheme = darkColorScheme(
    primary = SigmaOrange,
    secondary = SigmaGreen,
    tertiary = WaterBlue,
    background = CarbonDark,
    surface = CarbonCard,
    onBackground = OnCarbonDark,
    onSurface = OnCarbonCard,
    primaryContainer = CarbonCardElevated,
    onPrimaryContainer = OnCarbonCard,
    secondaryContainer = SigmaOrange,
    onSecondaryContainer = Color.White
)

private val SigmaLightColorScheme = lightColorScheme(
    primary = SigmaOrange,
    secondary = SigmaGreen,
    tertiary = WaterBlue,
    background = Color(0xFFF9F9F9),
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFFEEEEEE),
    onPrimaryContainer = Color(0xFF1C1B1F),
    secondaryContainer = SigmaOrange,
    onSecondaryContainer = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set false to prioritize our signature brand theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SigmaDarkColorScheme
        else -> SigmaLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
