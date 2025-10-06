package com.example.jbchretreatstore.bookstore.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class DisplayItem(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val iconVector: ImageVector? = null,
    val options: List<Option> = emptyList(),
    val isInCart: Boolean = false
) {
    data class Option(
        val optionKey: String = "",
        val optionValue: List<String> = emptyList()
    )
}