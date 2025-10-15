package com.example.jbchretreatstore.bookstore.presentation.itemlist

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.core.presentation.UiText

data class ItemListState(
    val searchQuery: String = "",
    val displayItemList: List<DisplayItem> = emptyList(),
    val favoriteDisplayItems: List<DisplayItem> = emptyList(),
    val cartList: List<DisplayItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: UiText? = null,
    val selectedIndex: Int = 0
)