package com.example.jbchretreatstore.bookstore.presentation.itemlist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ItemListViewModel: ViewModel() {
    private val _state = MutableStateFlow(ItemListState())
    val state = _state.asStateFlow()

    fun onItemListAction(action: ItemListAction) {
        when (action) {
            is ItemListAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }
            is ItemListAction.OnIteClick -> {
                // Handle item click
            }
            is ItemListAction.OnBottomNavSelected -> {

            }
        }
    }
}