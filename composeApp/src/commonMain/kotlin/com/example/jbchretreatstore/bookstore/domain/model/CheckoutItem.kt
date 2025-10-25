package com.example.jbchretreatstore.bookstore.domain.model

data class CheckoutItem(
    val name: String = "",
    val quantity: Int = 1,
    val optionsMap: MutableMap<String, String> = mutableMapOf(),
    val totalPrice: Double = 0.0
)
