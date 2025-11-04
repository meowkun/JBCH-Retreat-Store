package com.example.jbchretreatstore.bookstore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import com.example.jbchretreatstore.bookstore.domain.usecase.DisplayItemUseCase
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
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

            is BookStoreIntent.OnAddDisplayItem -> {
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

            is BookStoreIntent.OnRemoveDisplayItem -> {
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList.filter { item -> item != intent.displayItem }
                    )
                }
                viewModelScope.launch {
                    displayItemUseCase.updateDisplayItems(state.value.displayItemList)
                }
            }

            is BookStoreIntent.OnAddToCheckoutItem -> {
                _state.update { currentState ->
                    val existingItem = currentState.currentCheckoutList.checkoutList.find {
                        it.itemName == intent.checkoutItem.itemName && it.optionsMap == intent.checkoutItem.optionsMap
                    }
                    val updatedCheckoutList = if (existingItem != null) {
                        currentState.currentCheckoutList.checkoutList.map { item ->
                            if (item.itemName == intent.checkoutItem.itemName && item.optionsMap == intent.checkoutItem.optionsMap) {
                                item.copy(
                                    quantity = item.quantity + intent.checkoutItem.quantity,
                                    totalPrice = item.totalPrice + intent.checkoutItem.totalPrice
                                )
                            } else item
                        }
                    } else {
                        // Add as new cart item
                        val clonedItem = intent.checkoutItem.copy(
                            optionsMap = intent.checkoutItem.optionsMap.toMap() as MutableMap<String, String>
                        )
                        currentState.currentCheckoutList.checkoutList + clonedItem
                    }

                    currentState.copy(
                        currentCheckoutList = currentState.currentCheckoutList.copy(
                            checkoutList = updatedCheckoutList
                        )
                    )
                }
            }

            is BookStoreIntent.OnCheckout -> {
                _state.update { currentState ->
                    val checkoutData = currentState.currentCheckoutList.copy(
                        buyerName = intent.buyerName,
                        checkoutStatus = intent.checkoutStatus
                    )
                    currentState.copy(
                        currentCheckoutList = ReceiptData(),
                        displayCheckoutDialog = false,
                        receiptList = currentState.receiptList + checkoutData
                    )
                }
            }

            is BookStoreIntent.OnRemoveFromCheckoutItem -> {
                _state.update { currentState ->
                    val updatedCheckoutList =
                        currentState.currentCheckoutList.checkoutList.filterNot { item ->
                            item == intent.checkoutItem
                        }
                    currentState.copy(
                        currentCheckoutList = currentState.currentCheckoutList.copy(
                            checkoutList = updatedCheckoutList
                        )
                    )
                }
            }

            is BookStoreIntent.OnNavigate -> {
                if (intent.destination == BookStoreNavDestination.CheckoutScreen && state.value.currentCheckoutList.checkoutList.isEmpty()) return
                navigator.navigateTo(intent.destination)
            }

            is BookStoreIntent.OnUpdateDialogVisibility -> {
                when (intent.alertDialogType) {
                    AlertDialogType.CHECKOUT -> {
                        _state.update {
                            it.copy(
                                displayCheckoutDialog = intent.isVisible
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}