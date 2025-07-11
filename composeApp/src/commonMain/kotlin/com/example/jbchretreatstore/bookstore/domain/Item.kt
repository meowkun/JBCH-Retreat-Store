package com.example.jbchretreatstore.bookstore.domain

data class Item(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.NOT_SPECIFIED,
    val options: Map<String, List<String>> = emptyMap()
)

enum class PaymentMethod {
    CASH,
    ZELLE,
    VENMO,
    NOT_SPECIFIED
}
