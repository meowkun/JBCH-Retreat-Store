package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem

data class AddItemState(
    val newItem: DisplayItem = DisplayItem(),
    val newItemVariant: DisplayItem.Variant = DisplayItem.Variant(),
    val displayAddOptionView: Boolean = false,
    val showItemNameError: Boolean = false,
    val showItemPriceError: Boolean = false,
    val showAddOptionError: Boolean = false,
    val showValueError: Boolean = false,
    val showDuplicateKeyError: Boolean = false,
    val displayPrice: String = "",
    val optionValue: String = ""
) {
    /**
     * Whether the variant key field is enabled (only when no values have been added yet)
     */
    val isOptionKeyEnabled: Boolean
        get() = newItemVariant.valueList.isEmpty()

    companion object {
        /**
         * Creates an AddItemState from an optional initial item (for edit mode)
         */
        fun fromItem(item: DisplayItem?): AddItemState {
            val displayItem = item ?: DisplayItem()
            return AddItemState(
                newItem = displayItem,
                displayPrice = if (displayItem.price > 0.0) displayItem.price.toString() else ""
            )
        }
    }
}
