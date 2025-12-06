package com.example.jbchretreatstore.bookstore.presentation.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.StringResource

/**
 * Shared snackbar manager that can be used across multiple ViewModels.
 * This allows any screen to show snackbar messages.
 */
class SnackbarManager {
    private val _snackbarMessage = MutableStateFlow<StringResource?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun showSnackbar(message: StringResource) {
        _snackbarMessage.value = message
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }
}

