package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import kotlinx.datetime.LocalDate

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
        get() = purchasedHistory.sumOf { it.totalPrice }

    /** Total amount formatted as currency string */
    val formattedTotalAmount: String
        get() = totalAmount.toCurrency()

    val hasReceiptData: Boolean
        get() = purchasedHistory.isNotEmpty() &&
                purchasedHistory.any { it.checkoutList.isNotEmpty() }

    /** Receipts grouped by date, sorted descending (most recent first) */
    val groupedReceipts: List<Pair<LocalDate, List<ReceiptData>>>
        get() = purchasedHistory
            .sortedByDescending { it.dateTime }
            .groupBy { it.dateTime.date }
            .toList()
            .sortedByDescending { it.first }

    /** Data for edit bottom sheet when available, null otherwise */
    val editBottomSheetData: EditBottomSheetData?
        get() = if (showEditBottomSheet && receiptToEdit != null && purchaseHistoryItemToEdit != null) {
            EditBottomSheetData(receiptToEdit, purchaseHistoryItemToEdit)
        } else null

    /** Data for edit buyer name dialog when available, null otherwise */
    val editBuyerNameDialogData: ReceiptData?
        get() = if (showEditBuyerNameDialog && receiptToEditBuyerName != null) {
            receiptToEditBuyerName
        } else null
}

/** Data required to show the edit bottom sheet */
data class EditBottomSheetData(
    val receipt: ReceiptData,
    val item: CheckoutItem
)

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

    data class UpdateBuyerName(
        val receipt: ReceiptData,
        val newBuyerName: String,
        val newPaymentMethod: PaymentMethod
    ) : PurchaseHistoryIntent
}

// Extension properties for UI display

/** Unique key for LazyColumn items, combining id and dateTime */
val ReceiptData.uniqueKey: String
    get() = "${id}_${dateTime}"

/** Unique key for date header in LazyColumn */
val LocalDate.headerKey: String
    get() = "header_$this"

/** Total price formatted as currency string */
val ReceiptData.formattedTotalPrice: String
    get() = totalPrice.toCurrency()

/** Total price formatted as currency string */
val CheckoutItem.formattedTotalPrice: String
    get() = totalPrice.toCurrency()

/** Unit price formatted as currency string */
val CheckoutItem.formattedUnitPrice: String
    get() = unitPrice.toCurrency()

