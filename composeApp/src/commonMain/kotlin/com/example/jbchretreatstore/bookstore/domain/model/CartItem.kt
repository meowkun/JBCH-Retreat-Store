package com.example.jbchretreatstore.bookstore.domain.model

data class CartItem(
    var quantity : Int = 1,
    val optionsMap: MutableMap<String, String> = mutableMapOf(),
    val paymentMethod: PaymentMethod = PaymentMethod.NOT_SPECIFIED,
    var totalPrice: Double = 0.0
)

enum class PaymentMethod {
    CASH,
    ZELLE,
    VENMO,
    NOT_SPECIFIED
}
