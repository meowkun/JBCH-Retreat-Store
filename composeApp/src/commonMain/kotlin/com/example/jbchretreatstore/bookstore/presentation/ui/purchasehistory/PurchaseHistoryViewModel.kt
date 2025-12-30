package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.constants.LogMessages
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.usecase.PurchaseHistoryUseCase
import com.example.jbchretreatstore.bookstore.presentation.shared.ShareManager
import com.example.jbchretreatstore.bookstore.presentation.utils.convertReceiptsToCsv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

/**
 * ViewModel for Purchase History screen following MVI architecture pattern.
 * All user actions are processed through the handleIntent() function.
 */
@OptIn(ExperimentalUuidApi::class)
class PurchaseHistoryViewModel(
    private val purchaseHistoryUseCase: PurchaseHistoryUseCase,
    private val shareManager: ShareManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observePurchaseHistory()
    }

    /**
     * Central intent handler following MVI pattern.
     * All user actions should be dispatched through this function.
     */
    fun handleIntent(intent: PurchaseHistoryIntent) {
        when (intent) {
            is PurchaseHistoryIntent.SharePurchaseHistory -> handleSharePurchaseHistory()
            is PurchaseHistoryIntent.ShowRemoveBottomSheet -> reduceShowRemoveBottomSheet(
                intent.show,
                intent.receipt
            )

            is PurchaseHistoryIntent.RemoveReceipt -> handleRemoveReceipt(intent.receipt)
            is PurchaseHistoryIntent.ShowEditBottomSheet -> reduceShowEditBottomSheet(
                intent.show,
                intent.receipt,
                intent.item
            )

            is PurchaseHistoryIntent.UpdateCheckoutItem -> handleUpdateCheckoutItem(
                intent.receipt,
                intent.originalItem,
                intent.updatedItem
            )

            is PurchaseHistoryIntent.ShowEditBuyerNameDialog -> reduceShowEditBuyerNameDialog(
                intent.show,
                intent.receipt
            )

            is PurchaseHistoryIntent.UpdateBuyerName -> handleUpdateBuyerName(
                intent.receipt,
                intent.newBuyerName,
                intent.newPaymentMethod
            )

            is PurchaseHistoryIntent.ToggleMonthExpanded -> reduceToggleMonthExpanded(intent.yearMonth)
            is PurchaseHistoryIntent.ShowEditMonthNameDialog -> reduceShowEditMonthNameDialog(
                intent.show,
                intent.yearMonth
            )

            is PurchaseHistoryIntent.UpdateMonthName -> reduceUpdateMonthName(
                intent.yearMonth,
                intent.newName
            )
        }
    }

    // region State Reducers (pure state updates)

    private fun reduceShowRemoveBottomSheet(show: Boolean, receipt: ReceiptData?) {
        _uiState.update {
            it.copy(showRemoveBottomSheet = show, receiptToRemove = receipt)
        }
    }

    private fun reduceShowEditBottomSheet(
        show: Boolean,
        receipt: ReceiptData?,
        item: CheckoutItem?
    ) {
        _uiState.update {
            it.copy(
                showEditBottomSheet = show,
                receiptToEdit = receipt,
                purchaseHistoryItemToEdit = item
            )
        }
    }

    private fun reduceShowEditBuyerNameDialog(show: Boolean, receipt: ReceiptData?) {
        _uiState.update {
            it.copy(
                showEditBuyerNameDialog = show,
                receiptToEditBuyerName = receipt
            )
        }
    }

    private fun reduceDismissRemoveBottomSheet() {
        _uiState.update {
            it.copy(showRemoveBottomSheet = false, receiptToRemove = null)
        }
    }

    private fun reduceDismissEditBottomSheet() {
        _uiState.update {
            it.copy(
                showEditBottomSheet = false,
                receiptToEdit = null,
                purchaseHistoryItemToEdit = null
            )
        }
    }

    private fun reduceDismissEditBuyerNameDialog() {
        _uiState.update {
            it.copy(
                showEditBuyerNameDialog = false,
                receiptToEditBuyerName = null
            )
        }
    }

    private fun reduceToggleMonthExpanded(yearMonth: YearMonth) {
        _uiState.update { state ->
            val currentCollapsed = state.collapsedMonths
            val newCollapsed = if (currentCollapsed.contains(yearMonth.key)) {
                // Was collapsed, now expand (remove from collapsed set)
                currentCollapsed - yearMonth.key
            } else {
                // Was expanded, now collapse (add to collapsed set)
                currentCollapsed + yearMonth.key
            }
            state.copy(collapsedMonths = newCollapsed)
        }
    }

    private fun reduceShowEditMonthNameDialog(show: Boolean, yearMonth: YearMonth?) {
        _uiState.update {
            it.copy(
                showEditMonthNameDialog = show,
                monthToEditName = yearMonth
            )
        }
    }

    private fun reduceUpdateMonthName(yearMonth: YearMonth, newName: String?) {
        _uiState.update { state ->
            val currentNames = state.customMonthNames.toMutableMap()
            if (newName.isNullOrBlank()) {
                currentNames.remove(yearMonth.key)
            } else {
                currentNames[yearMonth.key] = newName
            }
            state.copy(
                customMonthNames = currentNames,
                showEditMonthNameDialog = false,
                monthToEditName = null
            )
        }
    }

    // endregion

    // region Side Effects (async operations)

    private fun observePurchaseHistory() {
        viewModelScope.launch {
            purchaseHistoryUseCase.getPurchaseHistory().collect { purchasedHistory ->
                _uiState.update {
                    it.copy(purchasedHistory = purchasedHistory, isLoading = false)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun handleSharePurchaseHistory() {
        viewModelScope.launch {
            try {
                val csvResult = convertReceiptsToCsv(_uiState.value.purchasedHistory)
                val timestamp = Clock.System.now().toEpochMilliseconds()
                shareManager.shareCsv(csvResult.combinedCsv, "purchase_history_$timestamp.csv")
            } catch (e: Exception) {
                println(LogMessages.withError(LogMessages.SHARE_FAILED_PREFIX, e.message))
            }
        }
    }

    private fun handleRemoveReceipt(receipt: ReceiptData) {
        viewModelScope.launch {
            purchaseHistoryUseCase.removeReceipt(receipt)
                .onFailure {
                    println(
                        LogMessages.withError(
                            LogMessages.REMOVE_RECEIPT_FAILED_PREFIX,
                            it.message
                        )
                    )
                }
            // Always dismiss - flow collection handles UI update
            reduceDismissRemoveBottomSheet()
        }
    }

    private fun handleUpdateCheckoutItem(
        receipt: ReceiptData,
        originalItem: CheckoutItem,
        updatedItem: CheckoutItem
    ) {
        viewModelScope.launch {
            purchaseHistoryUseCase.updateCheckoutItemByVariants(receipt, originalItem, updatedItem)
                .onFailure {
                    println(
                        LogMessages.withError(
                            LogMessages.UPDATE_CHECKOUT_ITEM_FAILED_PREFIX,
                            it.message
                        )
                    )
                }
            // Always dismiss - flow collection handles UI update
            reduceDismissEditBottomSheet()
        }
    }

    private fun handleUpdateBuyerName(
        receipt: ReceiptData,
        newBuyerName: String,
        newPaymentMethod: PaymentMethod
    ) {
        viewModelScope.launch {
            purchaseHistoryUseCase.updateBuyerNameAndPaymentMethod(
                receipt,
                newBuyerName,
                newPaymentMethod
            )
                .onFailure {
                    println(
                        LogMessages.withError(
                            LogMessages.UPDATE_BUYER_NAME_FAILED_PREFIX,
                            it.message
                        )
                    )
                }
            reduceDismissEditBuyerNameDialog()
        }
    }

    // endregion

    /**
     * Check if there is any receipt data to display/share
     */
    fun hasReceiptData(): Boolean = uiState.value.hasReceiptData
}


