package com.example.jbchretreatstore.bookstore.presentation.shop

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem

data class ShopUiState(
    val searchQuery: String = "",
    val displayItemList: List<DisplayItem> = emptyList(),
    val isLoading: Boolean = true,
    val showAddItemDialog: Boolean = false,
    val showRemoveItemDialog: Boolean = false,
    val cartItemCount: Int = 0,
    val hasItemsInCart: Boolean = false
) {
    val searchedItemList: List<DisplayItem>
        get() = if (searchQuery.isBlank()) {
            displayItemList
        } else {
            displayItemList.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
}

