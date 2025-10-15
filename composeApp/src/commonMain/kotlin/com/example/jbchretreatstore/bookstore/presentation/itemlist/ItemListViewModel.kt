package com.example.jbchretreatstore.bookstore.presentation.itemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.data.local.DisplayItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemListViewModel : ViewModel() {
    private val repository: DisplayItemRepository by lazy {
        DisplayItemRepository()
    }

    init {
        // Load saved data
        viewModelScope.launch {
            repository.getItems().collect { list ->
                _state.update {
                    it.copy(
                        displayItemList = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private val _state = MutableStateFlow(ItemListState())
    val state = _state.asStateFlow()

    fun onItemListAction(action: ItemListAction) {
        when (action) {
            is ItemListAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }

            is ItemListAction.OnAddNewItem -> {
                if (state.value.displayItemList.contains(action.newItem)) return
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList + action.newItem
                    )
                }
                viewModelScope.launch {
                    repository.saveItems(state.value.displayItemList)
                }
            }

            is ItemListAction.onRemoveItem -> {
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList.filter { item -> item != action.item }
                    )
                }
                viewModelScope.launch {
                    repository.saveItems(state.value.displayItemList)
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