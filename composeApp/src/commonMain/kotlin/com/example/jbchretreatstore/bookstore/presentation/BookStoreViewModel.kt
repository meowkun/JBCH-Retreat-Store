package com.example.jbchretreatstore.bookstore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.usecase.CheckoutUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageCartUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageDisplayItemsUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.PurchaseHistoryUseCase
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavigator
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.added_to_cart
import jbchretreatstore.composeapp.generated.resources.checkout_success
import jbchretreatstore.composeapp.generated.resources.item_added_success
import jbchretreatstore.composeapp.generated.resources.item_removed_success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookStoreViewModel(
    private val manageDisplayItemsUseCase: ManageDisplayItemsUseCase,
    private val manageCartUseCase: ManageCartUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val purchaseHistoryUseCase: PurchaseHistoryUseCase
) : ViewModel() {

    init {
        // Load saved data
        viewModelScope.launch {
            manageDisplayItemsUseCase.getDisplayItems().collect { list ->
                _state.update {
                    it.copy(
                        displayItemList = list,
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            purchaseHistoryUseCase.getAllReceipts().collect { receipts ->
                _state.update {
                    it.copy(receiptList = receipts)
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
                viewModelScope.launch {
                    val result = manageDisplayItemsUseCase.addDisplayItem(intent.newItem)
                    result.onSuccess {
                        _state.update {
                            it.copy(
                                displayAddDisplayItemDialog = false,
                                snackbarMessage = Res.string.item_added_success
                            )
                        }
                    }.onFailure { error ->
                        _state.update {
                            it.copy(
                                displayAddDisplayItemDialog = false
                            )
                        }
                    }
                }
            }

            is BookStoreIntent.OnDeleteDisplayItem -> {
                viewModelScope.launch {
                    val result = manageDisplayItemsUseCase.removeDisplayItem(intent.displayItem)
                    result.onSuccess {
                        _state.update {
                            it.copy(
                                displayRemoveDisplayItemDialog = false,
                                snackbarMessage = Res.string.item_removed_success
                            )
                        }
                    }.onFailure { error ->
                        _state.update {
                            it.copy(
                                displayRemoveDisplayItemDialog = false
                            )
                        }
                    }
                }
            }

            is BookStoreIntent.OnAddToCheckoutItem -> {
                val result = manageCartUseCase.addToCart(
                    state.value.currentCheckoutList,
                    intent.checkoutItem
                )
                result.onSuccess { updatedCart ->
                    _state.update {
                        it.copy(
                            currentCheckoutList = updatedCart,
                            snackbarMessage = Res.string.added_to_cart
                        )
                    }
                }.onFailure {
                    // Validation failed - use case handles validation
                }
            }

            is BookStoreIntent.OnCheckout -> {
                viewModelScope.launch {
                    val result = checkoutUseCase.processCheckout(
                        state.value.currentCheckoutList,
                        intent.buyerName,
                        intent.checkoutStatus,
                        intent.paymentMethod
                    )
                    result.onSuccess {
                        _state.update {
                            it.copy(
                                currentCheckoutList = ReceiptData(),
                                displayCheckoutDialog = false,
                                snackbarMessage = Res.string.checkout_success
                            )
                        }
                    }.onFailure {
                        // Validation failed - use case handles validation
                    }
                }
            }

            is BookStoreIntent.OnRemoveFromCheckoutItem -> {
                val result = manageCartUseCase.removeFromCart(
                    state.value.currentCheckoutList,
                    intent.checkoutItem
                )
                result.onSuccess { updatedCart ->
                    _state.update {
                        it.copy(currentCheckoutList = updatedCart)
                    }
                }
            }

            is BookStoreIntent.OnNavigate -> {
                navigator.navigateTo(intent.destination)
            }

            is BookStoreIntent.OnSnackbarDismissed -> {
                _state.update {
                    it.copy(snackbarMessage = null)
                }
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