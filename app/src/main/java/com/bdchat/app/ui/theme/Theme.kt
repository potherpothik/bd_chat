package com.bdchat.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenDark,
    tertiary = GreenAccent
)

private val DarkColors = darkColorScheme(
    primary = GreenAccent,
    secondary = GreenPrimary,
    tertiary = GreenDark
)

@Composable
fun BDChatTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
