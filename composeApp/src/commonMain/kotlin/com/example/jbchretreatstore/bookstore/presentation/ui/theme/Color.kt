package com.example.jbchretreatstore.bookstore.presentation.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary colors from Figma
val Primary = Color(0xFF4242EA) // Blue primary button color
val PrimaryContainer = Color(0xFFE0E8FF) // Light blue background
val OnPrimary = Color(0xFFFFFFFF)
val OnPrimaryContainer = Color(0xFF58657E)

// Secondary colors
val Secondary = Color(0xFFB7C3E4) // Light blue-gray for cards
val SecondaryContainer = Color(0xFFE0E8FF)
val OnSecondary = Color(0xFF58657E)
val OnSecondaryContainer = Color(0xFF58657E)

// Background and Surface
val Background = Color(0xFFECEFF4) // Light gray background
val Surface = Color(0xFFE0E8FF) // Light blue surface
val SurfaceVariant = Color(0xFFB7C3E4) // Card surface
val OnBackground = Color(0xFF58657E) // Dark gray text
val OnSurface = Color(0xFF58657E) // Dark gray text

// Error
val Error = Color(0xFFE74C3C)
val OnError = Color(0xFFFFFFFF)

// Custom colors used in the app
val White = Color(0xFFFFFFFF)
val SandYellow = Color(0xFFFFF8E1) // Keep for backward compatibility
val LightBlue = Color(0xFFE0E8FF)
val MediumBlue = Color(0xFFB7C3E4)
val BrightBlue = Color(0xFF4242EA)
val TextGray = Color(0xFF58657E)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    error = Error,
    onError = OnError
)

val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFF2A2A9A),
    onPrimaryContainer = Color(0xFFE0E8FF),
    secondary = Color(0xFF8892B0),
    onSecondary = OnSecondary,
    secondaryContainer = Color(0xFF3D4458),
    onSecondaryContainer = Color(0xFFB7C3E4),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE1E3E5),
    surface = Color(0xFF2B2D30),
    onSurface = Color(0xFFE1E3E5),
    surfaceVariant = Color(0xFF43454A),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000)
)

