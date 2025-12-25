package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency

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

    /** True when clear search button should be shown */
    val showClearSearchButton: Boolean
        get() = searchQuery.isNotBlank()

    /** Data for remove item dialog when available, null otherwise */
    val removeDialogData: DisplayItem?
        get() = if (showRemoveItemDialog && itemToRemove != null) itemToRemove else null

    /** Data for edit item dialog when available, null otherwise */
    val editDialogData: DisplayItem?
        get() = if (showEditItemDialog && itemToEdit != null) itemToEdit else null
}

// Extension properties for UI display

/** Price formatted as currency string */
val DisplayItem.formattedPrice: String
    get() = price.toCurrency()

/**
 * Gets the selected value for a variant, falling back to the first option or empty string.
 * Used for dropdown menus displaying variant options.
 */
fun CheckoutItem.getSelectedVariantValue(variant: DisplayItem.Variant): String =
    variantsMap[variant.key] ?: variant.valueList.firstOrNull() ?: ""

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

