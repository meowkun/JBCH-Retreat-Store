package com.example.jbchretreatstore.bookstore.domain.model

import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CheckoutItem @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid = Uuid.random(),
    val itemName: String = "",
    val quantity: Int = 1,
    val variants: List<Variant> = emptyList(),
    val unitPrice: Double = 0.0
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
     * Backward compatible map of variant key to selected value.
     */
    val variantsMap: Map<String, String>
        get() = variants.associate { it.key to it.selectedValue }

    /**
     * Returns true if this item has any variants.
     */
    val hasVariants: Boolean
        get() = variants.isNotEmpty()

    /**
     * Returns a list of variant key-value pairs for UI formatting.
     * Use with string resource for proper localization.
     */
    val variantPairs: List<Pair<String, String>>
        get() = variants.map { it.key to it.selectedValue }

    /**
     * Returns a unique key for this item, combining id and variants hash.
     * Useful for LazyColumn item keys.
     */
    val uniqueKey: String
        get() = "${id}_${itemName.hashCode()}_${quantity.hashCode()}_${unitPrice.hashCode()}_${variantsMap.hashCode()}"

    /**
     * Returns a total price for this item.
     */
    val totalPrice: Double
        get() = unitPrice * quantity

    /**
     * Returns a total price currency string
     * Use with string resource for proper localization.
     */
    val totalPriceString: String
        get() = totalPrice.toCurrency()
}
