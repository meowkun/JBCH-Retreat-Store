package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
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
    fun sharePurchaseHistory() {
        viewModelScope.launch {
            try {
                val csvResult = convertReceiptsToCsv(_uiState.value.purchasedHistory)
                val timestamp = Clock.System.now().toEpochMilliseconds()
                shareManager.shareCsv(csvResult.combinedCsv, "purchase_history_$timestamp.csv")
            } catch (e: Exception) {
                println("Failed to share purchase history: ${e.message}")
            }
        }
    }

    fun showRemoveBottomSheet(show: Boolean, receipt: ReceiptData? = null) {
        _uiState.update {
            it.copy(showRemoveBottomSheet = show, receiptToRemove = receipt)
        }
    }

    fun removeReceipt(receipt: ReceiptData) {
        viewModelScope.launch {
            purchaseHistoryUseCase.removeReceipt(receipt)
                .onFailure { println("Failed to remove receipt: ${it.message}") }
            // Always dismiss - flow collection handles UI update
            dismissRemoveBottomSheet()
        }
    }

    fun showEditBottomSheet(
        show: Boolean,
        receipt: ReceiptData? = null,
        purchaseHistoryItem: CheckoutItem? = null
    ) {
        _uiState.update {
            it.copy(
                showEditBottomSheet = show,
                receiptToEdit = receipt,
                purchaseHistoryItemToEdit = purchaseHistoryItem
            )
        }
    }

    fun updateCheckoutItem(
        receipt: ReceiptData,
        originalItem: CheckoutItem,
        updatedItem: CheckoutItem
    ) {
        viewModelScope.launch {
            purchaseHistoryUseCase.updateCheckoutItemByVariants(receipt, originalItem, updatedItem)
                .onFailure { println("Failed to update checkout item: ${it.message}") }
            // Always dismiss - flow collection handles UI update
            dismissEditBottomSheet()
        }
    }

    private fun dismissRemoveBottomSheet() {
        _uiState.update {
            it.copy(showRemoveBottomSheet = false, receiptToRemove = null)
        }
    }

    private fun dismissEditBottomSheet() {
        _uiState.update {
            it.copy(
                showEditBottomSheet = false,
                receiptToEdit = null,
                purchaseHistoryItemToEdit = null
            )
        }
    }

    fun showEditBuyerNameDialog(show: Boolean, receipt: ReceiptData? = null) {
        _uiState.update {
            it.copy(
                showEditBuyerNameDialog = show,
                receiptToEditBuyerName = receipt
            )
        }
    }

    fun updateBuyerName(receipt: ReceiptData, newBuyerName: String) {
        viewModelScope.launch {
            purchaseHistoryUseCase.updateBuyerName(receipt, newBuyerName)
                .onFailure { println("Failed to update buyer name: ${it.message}") }
            dismissEditBuyerNameDialog()
        }
    }

    private fun dismissEditBuyerNameDialog() {
        _uiState.update {
            it.copy(
                showEditBuyerNameDialog = false,
                receiptToEditBuyerName = null
            )
        }
    }

    /**
     * Check if there is any receipt data to display/share
     */
    fun hasReceiptData(): Boolean = uiState.value.hasReceiptData
}

