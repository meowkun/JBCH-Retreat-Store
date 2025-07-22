package com.example.jbchretreatstore.bookstore.domain

import androidx.compose.ui.graphics.vector.ImageVector

data class Item(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val iconVector: ImageVector? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.NOT_SPECIFIED,
    val options: Options = Options(),
    val isEdited: Boolean = false
) {
    data class Options(
        val optionKey: String = "",
        val optionValue: List<String> = emptyList()
    )
}

enum class PaymentMethod {
    CASH,
    ZELLE,
    VENMO,
    NOT_SPECIFIED
}
