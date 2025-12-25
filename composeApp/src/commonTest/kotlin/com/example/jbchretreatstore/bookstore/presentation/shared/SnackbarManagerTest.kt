package com.example.jbchretreatstore.bookstore.presentation.shared

import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_failed
import jbchretreatstore.composeapp.generated.resources.checkout_success
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SnackbarManagerTest {

    @Test
    fun `initial state has no snackbar message`() {
        val manager = SnackbarManager()

        assertNull(manager.snackbarMessage.value)
    }

    @Test
    fun `showSnackbar sets the message`() {
        val manager = SnackbarManager()

        manager.showSnackbar(Res.string.checkout_success)

        assertEquals(Res.string.checkout_success, manager.snackbarMessage.value)
    }

    @Test
    fun `dismissSnackbar clears the message`() {
        val manager = SnackbarManager()
        manager.showSnackbar(Res.string.checkout_success)

        manager.dismissSnackbar()

        assertNull(manager.snackbarMessage.value)
    }

    @Test
    fun `showSnackbar replaces previous message`() {
        val manager = SnackbarManager()
        manager.showSnackbar(Res.string.checkout_success)

        manager.showSnackbar(Res.string.checkout_failed)

        assertEquals(Res.string.checkout_failed, manager.snackbarMessage.value)
    }

    @Test
    fun `dismissSnackbar is idempotent when no message`() {
        val manager = SnackbarManager()

        manager.dismissSnackbar()

        assertNull(manager.snackbarMessage.value)
    }
}

