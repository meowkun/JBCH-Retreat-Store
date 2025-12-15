package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CheckoutItem @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid = Uuid.random(),
    val itemName: String = "",
    val quantity: Int = 1,
    val variants: List<Variant> = emptyList(),
    val totalPrice: Double = 0.0
) {
    /**
     * Variant with all available options and the selected value
     */
    data class Variant(
        val key: String = "",
        val valueList: List<String> = emptyList(),
        val selectedValue: String = ""
    )

    /**
     * Backward compatible map of variant key to selected value
     */
    val variantsMap: Map<String, String>
        get() = variants.associate { it.key to it.selectedValue }
}
