package com.example.jbchretreatstore.bookstore.presentation.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageCartUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageDisplayItemsUseCase
import com.example.jbchretreatstore.bookstore.presentation.shared.CartStateHolder
import com.example.jbchretreatstore.bookstore.presentation.shared.SnackbarManager
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.added_to_cart
import jbchretreatstore.composeapp.generated.resources.checkout_failed
import jbchretreatstore.composeapp.generated.resources.item_add_failed
import jbchretreatstore.composeapp.generated.resources.item_added_success
import jbchretreatstore.composeapp.generated.resources.item_remove_failed
import jbchretreatstore.composeapp.generated.resources.item_removed_success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopViewModel(
    private val manageDisplayItemsUseCase: ManageDisplayItemsUseCase,
    private val manageCartUseCase: ManageCartUseCase,
    private val cartStateHolder: CartStateHolder,
    private val snackbarManager: SnackbarManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())

    val uiState = combine(
        _uiState,
        cartStateHolder.cartState
    ) { state, cart ->
        state.copy(
            cartItemCount = cart.checkoutList.sumOf { it.quantity },
            hasItemsInCart = cart.checkoutList.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShopUiState()
    )

    init {
        loadDisplayItems()
    }

    private fun loadDisplayItems() {
        viewModelScope.launch {
            manageDisplayItemsUseCase.getDisplayItems().collect { list ->
                _uiState.update {
                    it.copy(
                        displayItemList = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onAddDisplayItem(newItem: DisplayItem) {
        viewModelScope.launch {
            val result = manageDisplayItemsUseCase.addDisplayItem(newItem)
            result.onSuccess {
                _uiState.update { it.copy(showAddItemDialog = false) }
                snackbarManager.showSnackbar(Res.string.item_added_success)
            }.onFailure { error ->
                println("Failed to add item: ${error.message}")
                _uiState.update { it.copy(showAddItemDialog = false) }
                snackbarManager.showSnackbar(Res.string.item_add_failed)
            }
        }
    }

    fun onDeleteDisplayItem(displayItem: DisplayItem) {
        viewModelScope.launch {
            val result = manageDisplayItemsUseCase.removeDisplayItem(displayItem)
            result.onSuccess {
                _uiState.update { it.copy(showRemoveItemDialog = false) }
                snackbarManager.showSnackbar(Res.string.item_removed_success)
            }.onFailure { error ->
                println("Failed to remove item: ${error.message}")
                _uiState.update { it.copy(showRemoveItemDialog = false) }
                snackbarManager.showSnackbar(Res.string.item_remove_failed)
            }
        }
    }

    fun onAddToCart(checkoutItem: CheckoutItem) {
        val result = manageCartUseCase.addToCart(
            cartStateHolder.cartState.value,
            checkoutItem
        )
        result.onSuccess { updatedCart ->
            cartStateHolder.updateCart(updatedCart)
            snackbarManager.showSnackbar(Res.string.added_to_cart)
        }.onFailure { error ->
            println("Failed to add to cart: ${error.message}")
            snackbarManager.showSnackbar(Res.string.checkout_failed)
        }
    }

    fun showAddItemDialog(show: Boolean) {
        _uiState.update { it.copy(showAddItemDialog = show) }
    }

    fun showRemoveItemDialog(show: Boolean) {
        _uiState.update { it.copy(showRemoveItemDialog = show) }
    }
}

