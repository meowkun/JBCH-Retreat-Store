package com.example.jbchretreatstore.bookstore.domain.model

data class ReceiptData(
    val buyerName: String = "",
    val checkoutList: List<CheckoutItem> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val checkoutStatus: CheckoutStatus = CheckoutStatus.PENDING
)