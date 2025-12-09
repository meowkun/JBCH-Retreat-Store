package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData

data class PurchaseHistoryUiState(
    val purchasedHistory: List<ReceiptData> = emptyList(),
    val isLoading: Boolean = true,
    val showRemoveBottomSheet: Boolean = false,
    val receiptToRemove: ReceiptData? = null,
    val showEditBottomSheet: Boolean = false,
    val receiptToEdit: ReceiptData? = null,
    val purchaseHistoryItemToEdit: CheckoutItem? = null
) {
    val totalAmount: Double
        get() = purchasedHistory.sumOf { receipt ->
            receipt.checkoutList.sumOf { it.totalPrice }
        }

    val hasReceiptData: Boolean
        get() = purchasedHistory.isNotEmpty() &&
                purchasedHistory.any { it.checkoutList.isNotEmpty() }
}

