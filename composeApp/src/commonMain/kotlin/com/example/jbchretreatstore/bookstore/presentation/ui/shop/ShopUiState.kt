package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem

/**
 * UI State for Shop screen following MVI pattern
 */
data class ShopUiState(
    val searchQuery: String = "",
    val displayItemList: List<DisplayItem> = emptyList(),
    val isLoading: Boolean = true,
    val showAddItemDialog: Boolean = false,
    val showRemoveItemDialog: Boolean = false,
    val showEditItemDialog: Boolean = false,
    val itemToRemove: DisplayItem? = null,
    val itemToEdit: DisplayItem? = null,
    val cartItemCount: Int = 0,
    val hasItemsInCart: Boolean = false
) {
    val searchedItemList: List<DisplayItem>
        get() = if (searchQuery.isBlank()) {
            displayItemList
        } else {
            displayItemList.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
}

/**
 * User intents (actions) for Shop screen following MVI pattern
 */
sealed interface ShopIntent {
    // Search
    data class UpdateSearchQuery(val query: String) : ShopIntent

    // Display Item Management
    data class AddDisplayItem(val item: DisplayItem) : ShopIntent
    data class UpdateDisplayItem(val item: DisplayItem) : ShopIntent
    data class DeleteDisplayItem(val item: DisplayItem) : ShopIntent

    // Cart
    data class AddToCart(val checkoutItem: CheckoutItem) : ShopIntent

    // Dialog visibility
    data class ShowAddItemDialog(val show: Boolean) : ShopIntent
    data class ShowRemoveItemDialog(val show: Boolean, val item: DisplayItem? = null) : ShopIntent
    data class ShowEditItemDialog(val show: Boolean, val item: DisplayItem? = null) : ShopIntent

    // Test data
    data object LoadTestData : ShopIntent
}

