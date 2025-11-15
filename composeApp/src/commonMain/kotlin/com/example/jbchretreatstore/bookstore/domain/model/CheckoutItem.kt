package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CheckoutItem @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid = Uuid.random(),
    val itemName: String = "",
    val quantity: Int = 1,
    val optionsMap: Map<String, String> = mapOf(),
    val totalPrice: Double = 0.0
)
