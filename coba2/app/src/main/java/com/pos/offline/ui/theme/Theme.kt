package com.pos.offline.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColors = darkColorScheme(
    primary = Color(0xFF72D39A),
    onPrimary = Color(0xFF08351F),
    primaryContainer = Color(0xFF164A30),
    onPrimaryContainer = Color(0xFFB9F0CB),
    secondary = Color(0xFF9FC7FF),
    secondaryContainer = Color(0xFF243C59),
    onSecondaryContainer = Color(0xFFD3E6FF),
    background = Color(0xFF0D1210),
    surface = Color(0xFF131916),
    surfaceVariant = Color(0xFF1D2621),
    onBackground = Color(0xFFE8EEE9),
    onSurface = Color(0xFFE8EEE9),
    onSurfaceVariant = Color(0xFFB8C2BC),
    outline = Color(0xFF5D6961),
    outlineVariant = Color(0xFF354038),
    error = Color(0xFFFF7B72),
    errorContainer = Color(0xFF5A1A17),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF157A45),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F3E2),
    onPrimaryContainer = Color(0xFF06351E),
    secondary = Color(0xFF2E6DA6),
    secondaryContainer = Color(0xFFDCEBFA),
    onSecondaryContainer = Color(0xFF102F4B),
    background = Color(0xFFF6F8F6),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEEF2EF),
    onBackground = Color(0xFF151A17),
    onSurface = Color(0xFF151A17),
    onSurfaceVariant = Color(0xFF606B64),
    outline = Color(0xFF7C887F),
    outlineVariant = Color(0xFFD2DBD4),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val AppTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
)

val PosMoneyFontFamily = FontFamily.Monospace

@Composable
fun PosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
