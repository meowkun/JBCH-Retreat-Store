package com.example.jbchretreatstore.bookstore.presentation

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod

// Encapsulated state used for checkout
data class CheckoutState(
    val buyerName: String,
    val checkoutStatus: CheckoutStatus,
    val paymentMethod: PaymentMethod
)
