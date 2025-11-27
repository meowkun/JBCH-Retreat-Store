package com.example.jbchretreatstore.bookstore.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Main theme composable for the JBCH Book Store app.
 *
 * Theme colors based on Figma design:
 * - Primary: #4242EA (Bright Blue)
 * - Background: #ECEFF4 (Light Gray)
 * - Surface: #E0E8FF (Light Blue)
 * - Card Surface: #B7C3E4 (Medium Blue)
 * - Text: #58657E (Dark Gray)
 *
 * @param useDarkTheme Whether to use dark theme. Defaults to system preference.
 * @param content The content to wrap with the theme.
 */
@Composable
fun BookStoreTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography(),
        content = content
    )
}

