package com.example.jbchretreatstore.bookstore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import com.example.jbchretreatstore.bookstore.domain.usecase.DisplayItemUseCase
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookStoreViewModel : ViewModel() {
    private val repository: BookStoreRepository by lazy {
        BookStoreRepository()
    }

    private val displayItemUseCase: DisplayItemUseCase by lazy {
        DisplayItemUseCase(repository)
    }

    init {
        // Load saved data
        viewModelScope.launch {
            displayItemUseCase.fetchDisplayItems().collect { list ->
                _state.update {
                    it.copy(
                        displayItemList = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private val _state = MutableStateFlow(BookStoreViewState())
    val state = _state.asStateFlow()

    fun onUserIntent(intent: BookStoreIntent, navigator: BookStoreNavigator) {
        when (intent) {
            is BookStoreIntent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = intent.query)
                }
            }

            is BookStoreIntent.OnAddNewItem -> {
                if (state.value.displayItemList.any {
                        it.name.equals(intent.newItem.name, ignoreCase = true)
                    }) return
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList + intent.newItem
                    )
                }
                viewModelScope.launch {
                    displayItemUseCase.updateDisplayItems(state.value.displayItemList)
                }
            }

            is BookStoreIntent.OnRemoveItem -> {
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList.filter { item -> item != intent.displayItem }
                    )
                }
                viewModelScope.launch {
                    displayItemUseCase.updateDisplayItems(state.value.displayItemList)
                }
            }

            is BookStoreIntent.OnAddToCart -> {
                _state.update { currentState ->
                    val existingItem = currentState.cartList.find {
                        it.name == intent.checkoutItem.name && it.optionsMap == intent.checkoutItem.optionsMap
                    }
                    val updatedCartList = if (existingItem != null) {
                        currentState.cartList.map { item ->
                            if (item.name == intent.checkoutItem.name) {
                                item.copy(
                                    quantity = item.quantity + intent.checkoutItem.quantity,
                                    totalPrice = item.totalPrice + intent.checkoutItem.totalPrice
                                )
                            } else item
                        }
                    } else {
                        // Add as new cart item
                        currentState.cartList + intent.checkoutItem
                    }

                    currentState.copy(cartList = updatedCartList)
                }
            }

            is BookStoreIntent.OnNavigate -> {
                navigator.navigateTo(intent.destination)
            }
        }
    }
}