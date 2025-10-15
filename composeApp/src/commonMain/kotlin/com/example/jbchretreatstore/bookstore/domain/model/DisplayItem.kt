package com.example.jbchretreatstore.bookstore.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DisplayItem(
    val name: String = "",
    val price: Double = 0.0,
    val options: List<Option> = emptyList(),
    val isInCart: Boolean = false
) {
    @Serializable
    data class Option(
        val optionKey: String = "",
        val optionValueList: List<String> = emptyList()
    )
}