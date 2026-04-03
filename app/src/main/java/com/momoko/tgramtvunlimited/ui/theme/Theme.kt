package com.momoko.tgramtvunlimited.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.material3.Color(0xFF00AFFE),
    secondary = androidx.compose.material3.Color(0xFF03DAC6),
    background = androidx.compose.material3.Color(0xFF121212),
    surface = androidx.compose.material3.Color(0xFF1E1E1E),
    onBackground = androidx.compose.material3.Color(0xFFFFFFFF),
    onSurface = androidx.compose.material3.Color(0xFFFFFFFF)
)

@Composable
fun TgramTVUnlimitedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
