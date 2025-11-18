package com.example.jbchretreatstore.bookstore.data.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class DisplayItemDto @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid = Uuid.random(), // Default for backward compatibility
    val name: String = "", // Default for backward compatibility
    val price: Double = 0.0, // Default for backward compatibility
    val options: List<OptionDto> = emptyList(), // Default for backward compatibility
    val isInCart: Boolean = false // Default for backward compatibility
) {
    @Serializable
    data class OptionDto(
        val optionKey: String = "", // Default for backward compatibility
        val optionValueList: List<String> = emptyList() // Default for backward compatibility
    )
}


