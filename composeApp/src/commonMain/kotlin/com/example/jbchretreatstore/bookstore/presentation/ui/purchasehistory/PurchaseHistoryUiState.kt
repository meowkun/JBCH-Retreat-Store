package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData

data class PurchaseHistoryUiState(
    val purchasedHistory: List<ReceiptData> = emptyList(),
    val isLoading: Boolean = true,
    val showRemoveBottomSheet: Boolean = false,
    val receiptToRemove: ReceiptData? = null
) {
    val totalAmount: Double
        get() = purchasedHistory.sumOf { receipt ->
            receipt.checkoutList.sumOf { it.totalPrice }
        }
}

