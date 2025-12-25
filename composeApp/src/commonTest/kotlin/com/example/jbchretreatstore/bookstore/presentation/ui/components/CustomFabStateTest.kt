package com.example.jbchretreatstore.bookstore.presentation.ui.components

import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CustomFabStateTest {

    // showCheckCartButton tests
    @Test
    fun `showCheckCartButton returns true when on shop screen with items in cart`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasItemsInCart = true
        )
        assertTrue(state.showCheckCartButton)
    }

    @Test
    fun `showCheckCartButton returns false when on shop screen with empty cart`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasItemsInCart = false
        )
        assertFalse(state.showCheckCartButton)
    }

    @Test
    fun `showCheckCartButton returns false when not on shop screen`() {
        val checkoutState = CustomFabState(
            currentScreen = BookStoreNavDestination.CheckoutScreen,
            hasItemsInCart = true
        )
        val receiptState = CustomFabState(
            currentScreen = BookStoreNavDestination.ReceiptScreen,
            hasItemsInCart = true
        )
        assertFalse(checkoutState.showCheckCartButton)
        assertFalse(receiptState.showCheckCartButton)
    }

    // showCheckoutButton tests
    @Test
    fun `showCheckoutButton returns true only when on checkout screen`() {
        val checkoutState = CustomFabState(currentScreen = BookStoreNavDestination.CheckoutScreen)
        val shopState = CustomFabState(currentScreen = BookStoreNavDestination.ShopScreen)
        val receiptState = CustomFabState(currentScreen = BookStoreNavDestination.ReceiptScreen)

        assertTrue(checkoutState.showCheckoutButton)
        assertFalse(shopState.showCheckoutButton)
        assertFalse(receiptState.showCheckoutButton)
    }

    // showAddItemButton tests
    @Test
    fun `showAddItemButton returns true when on shop screen with empty cart`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasItemsInCart = false
        )
        assertTrue(state.showAddItemButton)
    }

    @Test
    fun `showAddItemButton returns false when on shop screen with items in cart`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasItemsInCart = true
        )
        assertFalse(state.showAddItemButton)
    }

    @Test
    fun `showAddItemButton returns false when not on shop screen`() {
        val checkoutState = CustomFabState(
            currentScreen = BookStoreNavDestination.CheckoutScreen,
            hasItemsInCart = false
        )
        val receiptState = CustomFabState(
            currentScreen = BookStoreNavDestination.ReceiptScreen,
            hasItemsInCart = false
        )
        assertFalse(checkoutState.showAddItemButton)
        assertFalse(receiptState.showAddItemButton)
    }

    // showShareButton tests
    @Test
    fun `showShareButton returns true when on receipt screen with receipt data`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ReceiptScreen,
            hasReceiptData = true
        )
        assertTrue(state.showShareButton)
    }

    @Test
    fun `showShareButton returns false when on receipt screen without receipt data`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ReceiptScreen,
            hasReceiptData = false
        )
        assertFalse(state.showShareButton)
    }

    @Test
    fun `showShareButton returns false when not on receipt screen`() {
        val shopState = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasReceiptData = true
        )
        val checkoutState = CustomFabState(
            currentScreen = BookStoreNavDestination.CheckoutScreen,
            hasReceiptData = true
        )
        assertFalse(shopState.showShareButton)
        assertFalse(checkoutState.showShareButton)
    }

    // Mutual exclusivity tests
    @Test
    fun `only one button is visible at a time on shop screen with items`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasItemsInCart = true
        )
        assertTrue(state.showCheckCartButton)
        assertFalse(state.showAddItemButton)
        assertFalse(state.showCheckoutButton)
        assertFalse(state.showShareButton)
    }

    @Test
    fun `only one button is visible at a time on shop screen without items`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ShopScreen,
            hasItemsInCart = false
        )
        assertFalse(state.showCheckCartButton)
        assertTrue(state.showAddItemButton)
        assertFalse(state.showCheckoutButton)
        assertFalse(state.showShareButton)
    }

    @Test
    fun `only one button is visible at a time on checkout screen`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.CheckoutScreen,
            hasItemsInCart = true
        )
        assertFalse(state.showCheckCartButton)
        assertFalse(state.showAddItemButton)
        assertTrue(state.showCheckoutButton)
        assertFalse(state.showShareButton)
    }

    @Test
    fun `only one button is visible at a time on receipt screen with data`() {
        val state = CustomFabState(
            currentScreen = BookStoreNavDestination.ReceiptScreen,
            hasReceiptData = true
        )
        assertFalse(state.showCheckCartButton)
        assertFalse(state.showAddItemButton)
        assertFalse(state.showCheckoutButton)
        assertTrue(state.showShareButton)
    }
}
