package com.example.jbchretreatstore.bookstore.presentation.itemlist

import com.example.jbchretreatstore.bookstore.domain.DisplayItem

sealed interface ItemListAction {
    data class OnSearchQueryChange(val query: String) : ItemListAction
    data class OnIteClick(val displayItem: DisplayItem) : ItemListAction
    data class OnBottomNavSelected(val index: Int) : ItemListAction
}
