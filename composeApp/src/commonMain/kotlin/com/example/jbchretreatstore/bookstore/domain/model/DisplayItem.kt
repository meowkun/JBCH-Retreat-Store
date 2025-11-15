package com.example.jbchretreatstore.bookstore.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DisplayItem(
    val id: Uuid = Uuid.random(),
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