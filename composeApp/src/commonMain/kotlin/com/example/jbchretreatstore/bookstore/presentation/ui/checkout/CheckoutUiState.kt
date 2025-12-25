package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod

/**
 * UI State for Checkout screen following MVI pattern
 */
data class CheckoutUiState(
    val checkoutItems: List<CheckoutItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH,
    val showCheckoutDialog: Boolean = false,
    val checkoutSuccess: Boolean = false
) {
    /** True when cart is empty and checkout hasn't succeeded (should navigate back to shop) */
    val shouldNavigateBackToShop: Boolean
        get() = checkoutItems.isEmpty() && !checkoutSuccess
}

/**
 * User intents (actions) for Checkout screen following MVI pattern
 */
sealed interface CheckoutIntent {
    // Payment
    data class SelectPaymentMethod(val paymentMethod: PaymentMethod) : CheckoutIntent
    data class ProcessCheckout(val buyerName: String) : CheckoutIntent

    // Cart management
    data class RemoveFromCart(val item: CheckoutItem) : CheckoutIntent

    // Dialog visibility
    data class ShowCheckoutDialog(val show: Boolean) : CheckoutIntent

    // Navigation handling
    data object CheckoutSuccessHandled : CheckoutIntent
}

