package com.example.jbchretreatstore.bookstore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepositoryImpl
import com.example.jbchretreatstore.bookstore.domain.usecase.DisplayItemUseCase
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BookStoreViewModel : ViewModel() {
    private val repository: BookStoreRepositoryImpl by lazy {
        BookStoreRepositoryImpl()
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

    @OptIn(ExperimentalTime::class)
    fun onUserIntent(intent: BookStoreIntent, navigator: BookStoreNavigator) {
        when (intent) {
            is BookStoreIntent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = intent.query)
                }
            }

            is BookStoreIntent.OnAddDisplayItem -> {
                if (state.value.displayItemList.any { it.name.equals(intent.newItem.name, ignoreCase = true) }) {
                    _state.update {
                        it.copy(
                            displayAddDisplayItemDialog = false
                        )
                    }
                    return
                }
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList + intent.newItem,
                        displayAddDisplayItemDialog = false
                    )
                }
                viewModelScope.launch {
                    displayItemUseCase.updateDisplayItems(state.value.displayItemList)
                }
            }

            is BookStoreIntent.OnRemoveDisplayItem -> {
                _state.update {
                    it.copy(
                        displayItemList = it.displayItemList.filter { item -> item != intent.displayItem },
                        displayRemoveDisplayItemDialog = false
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
                        // Add as new cart item - create new mutable map from optionsMap
                        val clonedItem = intent.checkoutItem.copy(
                            optionsMap = intent.checkoutItem.optionsMap.toMutableMap()
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
                        checkoutStatus = intent.checkoutStatus,
                        dateTime = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
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

                    AlertDialogType.ADD_ITEM -> {
                        _state.update {
                            it.copy(
                                displayAddDisplayItemDialog = intent.isVisible
                            )
                        }
                    }

                    AlertDialogType.REMOVE_ITEM -> {
                        _state.update {
                            it.copy(
                                displayRemoveDisplayItemDialog = intent.isVisible
                            )
                        }
                    }
                }
            }
        }
    }
}