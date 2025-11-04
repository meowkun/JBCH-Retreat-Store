package com.example.jbchretreatstore.bookstore.presentation.viewmodel

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.core.presentation.UiText

data class BookStoreViewState(
    val searchQuery: String = "",
    val displayItemList: List<DisplayItem> = emptyList(),
    val favoriteDisplayItems: List<DisplayItem> = emptyList(),
    val currentCheckoutList: ReceiptData = ReceiptData(),
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val selectedIndex: Int = 0,
    val displayCheckoutDialog: Boolean = false,
    val receiptList: List<ReceiptData> = emptyList()
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

