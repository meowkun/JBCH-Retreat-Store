package com.example.jbchretreatstore.bookstore.presentation.itemlist

import com.example.jbchretreatstore.bookstore.domain.Item

sealed interface ItemListAction {
    data class OnSearchQueryChange(val query: String) : ItemListAction
    data class OnIteClick(val item: Item) : ItemListAction
    data class OnTabSelected(val index: Int) : ItemListAction

}