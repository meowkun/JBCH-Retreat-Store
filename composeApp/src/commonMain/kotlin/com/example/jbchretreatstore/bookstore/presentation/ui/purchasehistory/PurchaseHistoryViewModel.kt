package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
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

class PurchaseHistoryViewModel(
    private val purchaseHistoryUseCase: PurchaseHistoryUseCase,
    private val shareManager: ShareManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPurchaseHistory()
    }

    private fun loadPurchaseHistory() {
        viewModelScope.launch {
            purchaseHistoryUseCase.getAllReceipts().collect { receipts ->
                val purchasedHistory = receipts.filter {
                    it.checkoutStatus == CheckoutStatus.CHECKED_OUT
                }
                _uiState.update {
                    it.copy(
                        purchasedHistory = purchasedHistory,
                        isLoading = false
                    )
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

                // Share combined CSV with both detailed and summary sections
                shareManager.shareCsv(csvResult.combinedCsv, "purchase_history_$timestamp.csv")
            } catch (e: Exception) {
                println("Failed to share purchase history: ${e.message}")
            }
        }
    }

    fun hasReceiptData(): Boolean = _uiState.value.purchasedHistory.isNotEmpty() &&
            _uiState.value.purchasedHistory.any { it.checkoutList.isNotEmpty() }

    fun showRemoveBottomSheet(show: Boolean, receipt: ReceiptData? = null) {
        _uiState.update {
            it.copy(
                showRemoveBottomSheet = show,
                receiptToRemove = receipt
            )
        }
    }

    fun removeReceipt(receipt: ReceiptData) {
        viewModelScope.launch {
            val result = purchaseHistoryUseCase.removeReceipt(receipt)
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        purchasedHistory = state.purchasedHistory.filter { it.id != receipt.id },
                        showRemoveBottomSheet = false,
                        receiptToRemove = null
                    )
                }
            }.onFailure { error ->
                println("Failed to remove receipt: ${error.message}")
                _uiState.update {
                    it.copy(
                        showRemoveBottomSheet = false,
                        receiptToRemove = null
                    )
                }
            }
        }
    }
}

