package com.example.jbchretreatstore.bookstore.presentation.ui.components

import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination

/**
 * UI State for CustomFloatingActionButton
 */
data class CustomFabState(
    val currentScreen: BookStoreNavDestination,
    val hasItemsInCart: Boolean = false,
    val itemCount: Int = 0,
    val totalPrice: Double = 0.0,
    val hasReceiptData: Boolean = false
) {
    val showCheckCartButton: Boolean
        get() = currentScreen == BookStoreNavDestination.ShopScreen && hasItemsInCart

    val showCheckoutButton: Boolean
        get() = currentScreen == BookStoreNavDestination.CheckoutScreen

    val showAddItemButton: Boolean
        get() = currentScreen == BookStoreNavDestination.ShopScreen && !hasItemsInCart

    val showShareButton: Boolean
        get() = currentScreen == BookStoreNavDestination.ReceiptScreen && hasReceiptData
}


