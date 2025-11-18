package com.example.jbchretreatstore.bookstore.presentation

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import org.jetbrains.compose.resources.StringResource

data class BookStoreViewState(
    val searchQuery: String = "",
    val displayItemList: List<DisplayItem> = emptyList(),
    val currentCheckoutList: ReceiptData = ReceiptData(),
    val receiptList: List<ReceiptData> = emptyList(),
    val isLoading: Boolean = true,
    val snackbarMessage: StringResource? = null,
    val selectedIndex: Int = 0,
    val displayAddDisplayItemDialog: Boolean = false,
    val displayRemoveDisplayItemDialog: Boolean = false,
    val displayCheckoutDialog: Boolean = false
) {
    val searchedItemList: List<DisplayItem>
        get() = if (searchQuery.isBlank()) {
            displayItemList
        } else {
            displayItemList.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    val purchasedHistory: List<ReceiptData>
        get() = receiptList.filter {
            it.checkoutStatus == CheckoutStatus.CHECKED_OUT
        }

    val saveForLaterList: List<ReceiptData>
        get() = receiptList.filter {
            it.checkoutStatus == CheckoutStatus.SAVE_FOR_LATER
        }
}

