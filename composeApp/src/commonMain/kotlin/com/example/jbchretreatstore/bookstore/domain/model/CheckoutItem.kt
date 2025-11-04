package com.example.jbchretreatstore.bookstore.domain.model

data class CheckoutItem(
    val itemName: String = "",
    val quantity: Int = 1,
    val optionsMap: Map<String, String> = mapOf(),
    val totalPrice: Double = 0.0
)
