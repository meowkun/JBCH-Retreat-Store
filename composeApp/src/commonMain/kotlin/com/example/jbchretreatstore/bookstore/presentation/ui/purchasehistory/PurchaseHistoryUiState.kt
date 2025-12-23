package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData

/**
 * UI State for Purchase History screen following MVI pattern
 */
data class PurchaseHistoryUiState(
    val purchasedHistory: List<ReceiptData> = emptyList(),
    val isLoading: Boolean = true,
    val showRemoveBottomSheet: Boolean = false,
    val receiptToRemove: ReceiptData? = null,
    val showEditBottomSheet: Boolean = false,
    val receiptToEdit: ReceiptData? = null,
    val purchaseHistoryItemToEdit: CheckoutItem? = null,
    val showEditBuyerNameDialog: Boolean = false,
    val receiptToEditBuyerName: ReceiptData? = null
) {
    val totalAmount: Double
        get() = purchasedHistory.sumOf { receipt ->
            receipt.checkoutList.sumOf { it.totalPrice }
        }

    val hasReceiptData: Boolean
        get() = purchasedHistory.isNotEmpty() &&
                purchasedHistory.any { it.checkoutList.isNotEmpty() }
}

/**
 * User intents (actions) for Purchase History screen following MVI pattern
 */
sealed interface PurchaseHistoryIntent {
    // Share
    data object SharePurchaseHistory : PurchaseHistoryIntent

    // Remove receipt
    data class ShowRemoveBottomSheet(val show: Boolean, val receipt: ReceiptData? = null) :
        PurchaseHistoryIntent

    data class RemoveReceipt(val receipt: ReceiptData) : PurchaseHistoryIntent

    // Edit checkout item
    data class ShowEditBottomSheet(
        val show: Boolean,
        val receipt: ReceiptData? = null,
        val item: CheckoutItem? = null
    ) : PurchaseHistoryIntent

    data class UpdateCheckoutItem(
        val receipt: ReceiptData,
        val originalItem: CheckoutItem,
        val updatedItem: CheckoutItem
    ) : PurchaseHistoryIntent

    // Edit buyer name
    data class ShowEditBuyerNameDialog(val show: Boolean, val receipt: ReceiptData? = null) :
        PurchaseHistoryIntent

    data class UpdateBuyerName(val receipt: ReceiptData, val newBuyerName: String) :
        PurchaseHistoryIntent
}

