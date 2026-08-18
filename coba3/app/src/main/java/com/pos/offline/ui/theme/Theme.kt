package com.pos.offline.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tema "Deep Calm" untuk shift malam atau ruangan redup
private val DarkColors = darkColorScheme(
    primary = Color(0xFF818CF8), // Indigo Terang (Aksen Utama)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3730A3), // Background baris aktif
    onPrimaryContainer = Color(0xFFE0E7FF),
    
    secondary = Color(0xFF10B981), // Emerald (Aksi Sukses/Bayar)
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF059669),
    onSecondaryContainer = Color(0xFFD1FAE5),
    
    tertiary = Color(0xFF38BDF8), // Aksen opsional (misal: QRIS)
    tertiaryContainer = Color(0xFF0369A1),
    onTertiaryContainer = Color(0xFFE0F2FE),
    
    background = Color(0xFF0F172A), // Slate 900 (Gelap namun tidak pure black)
    surface = Color(0xFF1E293B), // Slate 800
    surfaceVariant = Color(0xFF334155), // Slate 700
    outlineVariant = Color(0xFF475569), 
    
    onBackground = Color(0xFFF8FAFC), // Slate 50 (Putih tulang, tidak silau)
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFCBD5E1), // Teks sekunder
    
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

// Tema "Clear Focus" untuk shift pagi/siang
private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5), // Indigo
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    
    secondary = Color(0xFF10B981), // Emerald
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    
    tertiary = Color(0xFF0284C7),
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF0C4A6E),
    
    background = Color(0xFFF8FAFC), // Slate 50 (Sangat bersih)
    surface = Color(0xFFFFFFFF), 
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    outlineVariant = Color(0xFFCBD5E1),
    
    onBackground = Color(0xFF0F172A), // Slate 900 (Sangat gelap, bukan pure black)
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B), // Teks sekunder
    
    error = Color(0xFFEF4444),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D)
)

private val AppTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 0.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
)

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
