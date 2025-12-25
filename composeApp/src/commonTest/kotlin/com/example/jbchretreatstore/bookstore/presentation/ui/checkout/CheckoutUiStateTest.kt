package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckoutUiStateTest {

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
}


