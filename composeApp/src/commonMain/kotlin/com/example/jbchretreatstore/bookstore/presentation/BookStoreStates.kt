package com.example.jbchretreatstore.bookstore.presentation

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType

// Encapsulated state used for checkout
data class CheckoutState(
    val buyerName: String,
    val checkoutStatus: CheckoutStatus,
    val paymentMethod: PaymentMethod
)

// Encapsulated dialog visibility state used by intents and viewmodel
data class DialogVisibilityState(
    val alertDialogType: AlertDialogType,
    val isVisible: Boolean
)
