package com.example.jbchretreatstore.bookstore.presentation.viewmodel

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.core.presentation.UiText

data class BookStoreViewState(
    val searchQuery: String = "",
    val displayItemList: List<DisplayItem> = emptyList(),
    val favoriteDisplayItems: List<DisplayItem> = emptyList(),
    val cartList: List<CheckoutItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val selectedIndex: Int = 0
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