package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

/**
 * Represents a month-year combination for grouping receipts
 */
data class YearMonth(
    val year: Int,
    val month: Month
) : Comparable<YearMonth> {
    override fun compareTo(other: YearMonth): Int {
        val yearCompare = year.compareTo(other.year)
        return if (yearCompare != 0) yearCompare else month.compareTo(other.month)
    }

    val key: String get() = "${year}_${month.ordinal}"
}

/**
 * Represents a date group within a month
 */
data class DateGroup(
    val date: LocalDate,
    val receipts: List<ReceiptData>
)

/**
 * Represents a group of receipts for a specific month
 */
data class MonthGroup(
    val yearMonth: YearMonth,
    val customName: String? = null,
    val receipts: List<ReceiptData> = emptyList(),
    val isExpanded: Boolean = true
) {
    /** Display name - custom name if set, otherwise formatted month/year */
    val displayName: String
        get() = customName ?: "${
            yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
        } ${yearMonth.year}"

    /** Total price for all receipts in this group */
    val totalPrice: Double
        get() = receipts.sumOf { it.totalPrice }

    /** Formatted total price */
    val formattedTotalPrice: String
        get() = totalPrice.toCurrency()

    /** Number of receipts in this group */
    val receiptCount: Int
        get() = receipts.size

    /** Receipts grouped by date within this month, sorted descending */
    val groupedByDate: List<DateGroup>
        get() = receipts
            .groupBy { it.dateTime.date }
            .map { (date, receiptsForDate) -> DateGroup(date, receiptsForDate) }
            .sortedByDescending { it.date }
}

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
    val receiptToEditBuyerName: ReceiptData? = null,
    // Month grouping state
    val collapsedMonths: Set<String> = emptySet(), // Set of YearMonth keys that are collapsed
    val customMonthNames: Map<String, String> = emptyMap(), // Map of YearMonth key to custom name
    val showEditMonthNameDialog: Boolean = false,
    val monthToEditName: YearMonth? = null
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

    /** Receipts grouped by month, sorted descending (most recent first) */
    val groupedByMonth: List<MonthGroup>
        get() = purchasedHistory
            .sortedByDescending { it.dateTime }
            .groupBy { YearMonth(it.dateTime.year, it.dateTime.month) }
            .map { (yearMonth, receipts) ->
                MonthGroup(
                    yearMonth = yearMonth,
                    customName = customMonthNames[yearMonth.key],
                    receipts = receipts.sortedByDescending { it.dateTime },
                    isExpanded = !collapsedMonths.contains(yearMonth.key)
                )
            }
            .sortedByDescending { it.yearMonth }

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

    /** Data for edit month name dialog when available, null otherwise */
    val editMonthNameDialogData: Pair<YearMonth, String?>?
        get() = if (showEditMonthNameDialog && monthToEditName != null) {
            Pair(monthToEditName, customMonthNames[monthToEditName.key])
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

    // Month group operations
    data class ToggleMonthExpanded(val yearMonth: YearMonth) : PurchaseHistoryIntent
    data class ShowEditMonthNameDialog(val show: Boolean, val yearMonth: YearMonth? = null) :
        PurchaseHistoryIntent

    data class UpdateMonthName(val yearMonth: YearMonth, val newName: String?) :
        PurchaseHistoryIntent
}

// Extension properties for UI display

/** Unique key for LazyColumn items, combining id and dateTime */
val ReceiptData.uniqueKey: String
    get() = "${id}_${dateTime}"

/** Unique key for date header in LazyColumn */
val LocalDate.headerKey: String
    get() = "header_$this"

/** Unique key for month group header in LazyColumn */
val MonthGroup.headerKey: String
    get() = "month_header_${yearMonth.key}"

/** Total price formatted as currency string */
val ReceiptData.formattedTotalPrice: String
    get() = totalPrice.toCurrency()

/** Total price formatted as currency string */
val CheckoutItem.formattedTotalPrice: String
    get() = totalPrice.toCurrency()

/** Unit price formatted as currency string */
val CheckoutItem.formattedUnitPrice: String
    get() = unitPrice.toCurrency()

