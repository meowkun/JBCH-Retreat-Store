package com.example.jbchretreatstore.bookstore.presentation.shared

import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared cart state holder that can be used across multiple ViewModels.
 * This allows the cart state to be shared between ShopScreen and CheckoutScreen.
 */
class CartStateHolder {
    private val _cartState = MutableStateFlow(ReceiptData())
    val cartState = _cartState.asStateFlow()

    fun updateCart(cart: ReceiptData) {
        _cartState.value = cart
    }

    fun clearCart() {
        _cartState.value = ReceiptData()
    }
}

