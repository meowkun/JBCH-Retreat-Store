package com.example.jbchretreatstore.bookstore.presentation.ui.shop

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
import jbchretreatstore.composeapp.generated.resources.item_update_failed
import jbchretreatstore.composeapp.generated.resources.item_update_success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Shop screen following MVI architecture pattern.
 * All user actions are processed through the handleIntent() function.
 */
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

    /**
     * Central intent handler following MVI pattern.
     * All user actions should be dispatched through this function.
     */
    fun handleIntent(intent: ShopIntent) {
        when (intent) {
            is ShopIntent.UpdateSearchQuery -> reduceSearchQuery(intent.query)
            is ShopIntent.AddDisplayItem -> handleAddDisplayItem(intent.item)
            is ShopIntent.UpdateDisplayItem -> handleUpdateDisplayItem(intent.item)
            is ShopIntent.DeleteDisplayItem -> handleDeleteDisplayItem(intent.item)
            is ShopIntent.AddToCart -> handleAddToCart(intent.checkoutItem)
            is ShopIntent.ShowAddItemDialog -> reduceShowAddItemDialog(intent.show)
            is ShopIntent.ShowRemoveItemDialog -> reduceShowRemoveItemDialog(
                intent.show,
                intent.item
            )

            is ShopIntent.ShowEditItemDialog -> reduceShowEditItemDialog(intent.show, intent.item)
            is ShopIntent.LoadTestData -> handleLoadTestData()
        }
    }

    // region State Reducers (pure state updates)

    private fun reduceSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun reduceShowAddItemDialog(show: Boolean) {
        _uiState.update { it.copy(showAddItemDialog = show) }
    }

    private fun reduceShowRemoveItemDialog(show: Boolean, item: DisplayItem?) {
        _uiState.update { it.copy(showRemoveItemDialog = show, itemToRemove = item) }
    }

    private fun reduceShowEditItemDialog(show: Boolean, item: DisplayItem?) {
        _uiState.update { it.copy(showEditItemDialog = show, itemToEdit = item) }
    }

    // endregion

    // region Side Effects (async operations)

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

    private fun handleAddDisplayItem(newItem: DisplayItem) {
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

    private fun handleDeleteDisplayItem(displayItem: DisplayItem) {
        viewModelScope.launch {
            val result = manageDisplayItemsUseCase.removeDisplayItem(displayItem)
            result.onSuccess {
                _uiState.update { it.copy(showRemoveItemDialog = false, itemToRemove = null) }
                snackbarManager.showSnackbar(Res.string.item_removed_success)
            }.onFailure { error ->
                println("Failed to remove item: ${error.message}")
                _uiState.update { it.copy(showRemoveItemDialog = false, itemToRemove = null) }
                snackbarManager.showSnackbar(Res.string.item_remove_failed)
            }
        }
    }

    private fun handleUpdateDisplayItem(updatedItem: DisplayItem) {
        viewModelScope.launch {
            val result = manageDisplayItemsUseCase.updateDisplayItem(updatedItem)
            result.onSuccess {
                _uiState.update { it.copy(showEditItemDialog = false, itemToEdit = null) }
                snackbarManager.showSnackbar(Res.string.item_update_success)
            }.onFailure { error ->
                println("Failed to update item: ${error.message}")
                _uiState.update { it.copy(showEditItemDialog = false, itemToEdit = null) }
                snackbarManager.showSnackbar(Res.string.item_update_failed)
            }
        }
    }

    private fun handleAddToCart(checkoutItem: CheckoutItem) {
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

    private fun handleLoadTestData() {
        viewModelScope.launch {
            manageDisplayItemsUseCase.loadTestData()
        }
    }

    // endregion
}

