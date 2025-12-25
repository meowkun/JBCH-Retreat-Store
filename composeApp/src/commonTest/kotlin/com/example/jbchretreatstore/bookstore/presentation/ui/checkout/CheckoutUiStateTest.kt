package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckoutUiStateTest {

    // Tests for shouldNavigateBackToShop derived property

    @Test
    fun `shouldNavigateBackToShop returns true when cart is empty and checkout not successful`() {
        val uiState = CheckoutUiState(
            checkoutItems = emptyList(),
            checkoutSuccess = false
        )

        assertTrue(uiState.shouldNavigateBackToShop)
    }

    @Test
    fun `shouldNavigateBackToShop returns false when cart has items`() {
        val uiState = CheckoutUiState(
            checkoutItems = listOf(CheckoutItem(itemName = "Test Item")),
            checkoutSuccess = false
        )

        assertFalse(uiState.shouldNavigateBackToShop)
    }

    @Test
    fun `shouldNavigateBackToShop returns false when checkout is successful`() {
        val uiState = CheckoutUiState(
            checkoutItems = emptyList(),
            checkoutSuccess = true
        )

        assertFalse(uiState.shouldNavigateBackToShop)
    }

    @Test
    fun `shouldNavigateBackToShop returns false when cart has items and checkout successful`() {
        val uiState = CheckoutUiState(
            checkoutItems = listOf(CheckoutItem(itemName = "Test Item")),
            checkoutSuccess = true
        )

        assertFalse(uiState.shouldNavigateBackToShop)
    }

    // Tests for default state

    @Test
    fun `default state has empty checkout items`() {
        val uiState = CheckoutUiState()

        assertTrue(uiState.checkoutItems.isEmpty())
    }

    @Test
    fun `default state has zero total price`() {
        val uiState = CheckoutUiState()

        assertEquals(0.0, uiState.totalPrice)
    }

    @Test
    fun `default state has CASH payment method`() {
        val uiState = CheckoutUiState()

        assertEquals(PaymentMethod.CASH, uiState.selectedPaymentMethod)
    }

    @Test
    fun `default state has checkout dialog hidden`() {
        val uiState = CheckoutUiState()

        assertFalse(uiState.showCheckoutDialog)
    }

    @Test
    fun `default state has checkout not successful`() {
        val uiState = CheckoutUiState()

        assertFalse(uiState.checkoutSuccess)
    }

    // Tests for state with custom values

    @Test
    fun `state with items has correct item count`() {
        val items = listOf(
            CheckoutItem(itemName = "Item 1", totalPrice = 10.0),
            CheckoutItem(itemName = "Item 2", totalPrice = 20.0)
        )
        val uiState = CheckoutUiState(checkoutItems = items)

        assertEquals(2, uiState.checkoutItems.size)
    }

    @Test
    fun `state preserves total price`() {
        val uiState = CheckoutUiState(totalPrice = 99.99)

        assertEquals(99.99, uiState.totalPrice)
    }

    @Test
    fun `state preserves selected payment method`() {
        val uiState = CheckoutUiState(selectedPaymentMethod = PaymentMethod.ZELLE)

        assertEquals(PaymentMethod.ZELLE, uiState.selectedPaymentMethod)
    }

    @Test
    fun `state preserves show checkout dialog flag`() {
        val uiState = CheckoutUiState(showCheckoutDialog = true)

        assertTrue(uiState.showCheckoutDialog)
    }

    @Test
    fun `state preserves checkout success flag`() {
        val uiState = CheckoutUiState(checkoutSuccess = true)

        assertTrue(uiState.checkoutSuccess)
    }

    // Tests for copy functionality

    @Test
    fun `copy preserves unchanged values`() {
        val original = CheckoutUiState(
            checkoutItems = listOf(CheckoutItem(itemName = "Test")),
            totalPrice = 50.0,
            selectedPaymentMethod = PaymentMethod.VENMO,
            showCheckoutDialog = true,
            checkoutSuccess = false
        )

        val copied = original.copy(checkoutSuccess = true)

        assertEquals(original.checkoutItems, copied.checkoutItems)
        assertEquals(original.totalPrice, copied.totalPrice)
        assertEquals(original.selectedPaymentMethod, copied.selectedPaymentMethod)
        assertEquals(original.showCheckoutDialog, copied.showCheckoutDialog)
        assertTrue(copied.checkoutSuccess)
    }
}


