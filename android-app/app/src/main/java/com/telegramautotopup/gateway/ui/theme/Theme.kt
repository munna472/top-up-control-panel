package com.telegramautotopup.gateway.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0891B2),      // cyan-600
    secondary = Color(0xFF10B981),    // emerald-500
    tertiary = Color(0xFFF59E0B),     // amber-500
    background = Color(0xFF020617),   // slate-950
    surface = Color(0xFF0F172A),      // slate-900
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun TelegramAutoTopUpGatewayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
