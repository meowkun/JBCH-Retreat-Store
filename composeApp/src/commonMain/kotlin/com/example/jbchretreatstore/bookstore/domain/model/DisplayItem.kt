package com.example.jbchretreatstore.bookstore.domain.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DisplayItem(
    val id: Uuid = Uuid.random(),
    val name: String = "",
    val price: Double = 0.0,
    val variants: List<Variant> = emptyList(),
    val isInCart: Boolean = false
) {
    @Serializable
    data class Variant(
        val id: Uuid = Uuid.random(),
        val key: String = "",
        val valueList: List<String> = emptyList()
    )
}