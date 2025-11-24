package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem

data class AddItemState (
    val newItem: DisplayItem = DisplayItem(),
    val newItemOption: DisplayItem.Option = DisplayItem.Option(),
    val displayAddOptionView: Boolean = false,
    val showAddItemError: Boolean = false,
    val showAddOptionError: Boolean = false
)