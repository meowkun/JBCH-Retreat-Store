package com.example.jbchretreatstore.bookstore.presentation.itemlist

import com.example.jbchretreatstore.bookstore.domain.Item
import com.example.jbchretreatstore.core.presentation.UiText

data class ItemListState(
    val searchQuery: String = "",
    val searchResults: List<Item> = emptyList(),
    val favoriteItems: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null
)
