package com.norton.securitydashboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NortonColorScheme = lightColorScheme(
    primary = NortonYellow,
    onPrimary = Color.Black,
    primaryContainer = NortonYellowDark,
    onPrimaryContainer = Color.Black,
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    outline = Color(0xFFE0E0E0)
)

@Composable
fun SecurityHealthDashboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NortonColorScheme,
        typography = Typography,
        content = content
    )
}
