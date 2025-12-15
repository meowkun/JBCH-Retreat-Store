package com.example.jbchretreatstore.bookstore.data.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class CheckoutItemDto @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid = Uuid.random(), // Default for backward compatibility
    val itemName: String = "", // Default for backward compatibility
    val quantity: Int = 1, // Default for backward compatibility
    val optionsMap: Map<String, String> = emptyMap(), // Legacy field for backward compatibility
    val variants: List<VariantDto> = emptyList(), // New structure with all options
    val totalPrice: Double = 0.0 // Default for backward compatibility
) {
    @Serializable
    data class VariantDto(
        val key: String = "",
        val valueList: List<String> = emptyList(),
        val selectedValue: String = ""
    )
}

