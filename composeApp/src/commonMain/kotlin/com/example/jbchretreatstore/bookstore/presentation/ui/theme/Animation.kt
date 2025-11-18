package com.example.jbchretreatstore.bookstore.presentation.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween

/**
 * Animation duration and easing constants for the JBCH Book Store app.
 * Based on Material Design 3 motion guidelines.
 */
object Animation {

    // Duration Constants (in milliseconds)
    object Duration {
        const val instant: Int = 0
        const val short1: Int = 50
        const val short2: Int = 100
        const val short3: Int = 150
        const val short4: Int = 200
        const val medium1: Int = 250
        const val medium2: Int = 300
        const val medium3: Int = 350
        const val medium4: Int = 400
        const val long1: Int = 450
        const val long2: Int = 500
        const val long3: Int = 550
        const val long4: Int = 600
        const val extraLong1: Int = 700
        const val extraLong2: Int = 800
        const val extraLong3: Int = 900
        const val extraLong4: Int = 1000
    }

    // Easing Functions
    object Easing {
        val emphasized = FastOutSlowInEasing
        val standard = FastOutSlowInEasing
        val linear = LinearEasing
    }

    // Common Animation Specs
    object Spec {
        val fadeIn = tween<Float>(
            durationMillis = Duration.short4,
            easing = Easing.standard
        )

        val fadeOut = tween<Float>(
            durationMillis = Duration.short3,
            easing = Easing.standard
        )

        val slideIn = tween<Float>(
            durationMillis = Duration.medium2,
            easing = Easing.emphasized
        )

        val slideOut = tween<Float>(
            durationMillis = Duration.short4,
            easing = Easing.emphasized
        )

        val expand = tween<Float>(
            durationMillis = Duration.medium2,
            easing = Easing.emphasized
        )

        val collapse = tween<Float>(
            durationMillis = Duration.medium1,
            easing = Easing.emphasized
        )

        val scaleIn = tween<Float>(
            durationMillis = Duration.medium1,
            easing = Easing.emphasized
        )

        val scaleOut = tween<Float>(
            durationMillis = Duration.short3,
            easing = Easing.emphasized
        )
    }

    // Component Specific Durations
    object Component {
        const val button_press: Int = Duration.short3
        const val dialog_enter: Int = Duration.medium2
        const val dialog_exit: Int = Duration.short4
        const val dropdown_expand: Int = Duration.medium1
        const val dropdown_collapse: Int = Duration.short4
        const val card_elevation_change: Int = Duration.short3
        const val content_size_change: Int = Duration.medium2
        const val navigation_transition: Int = Duration.medium3
        const val snackbar_enter: Int = Duration.medium2
        const val snackbar_exit: Int = Duration.short4
        const val loading_shimmer: Int = Duration.extraLong2
    }
}

