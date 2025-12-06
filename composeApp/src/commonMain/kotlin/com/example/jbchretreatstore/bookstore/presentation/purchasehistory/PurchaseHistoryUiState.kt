package com.example.jbchretreatstore.bookstore.presentation.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData

data class PurchaseHistoryUiState(
    val purchasedHistory: List<ReceiptData> = emptyList(),
    val isLoading: Boolean = true
) {
    val totalAmount: Double
        get() = purchasedHistory.sumOf { receipt ->
            receipt.checkoutList.sumOf { it.totalPrice }
        }
}

