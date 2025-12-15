package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod

data class CheckoutUiState(
    val checkoutItems: List<CheckoutItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH,
    val showCheckoutDialog: Boolean = false,
    val checkoutSuccess: Boolean = false
)

