package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem

data class AddItemState (
    val newItem: DisplayItem = DisplayItem(),
    val newItemVariant: DisplayItem.Variant = DisplayItem.Variant(),
    val displayAddOptionView: Boolean = false,
    val showItemNameError: Boolean = false,
    val showItemPriceError: Boolean = false,
    val showAddOptionError: Boolean = false,
    val showValueError: Boolean = false,
    val showDuplicateKeyError: Boolean = false
)