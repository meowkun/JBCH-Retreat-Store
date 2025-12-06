package com.example.jbchretreatstore.bookstore.presentation.purchasehistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.usecase.PurchaseHistoryUseCase
import com.example.jbchretreatstore.bookstore.presentation.share.ShareManager
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
                val csvContent = convertReceiptsToCsv(_uiState.value.purchasedHistory)
                val fileName = "purchase_history_${Clock.System.now().toEpochMilliseconds()}.csv"
                shareManager.shareCsv(csvContent, fileName)
            } catch (e: Exception) {
                println("Failed to share purchase history: ${e.message}")
            }
        }
    }

    fun hasReceiptData(): Boolean = _uiState.value.purchasedHistory.isNotEmpty() &&
            _uiState.value.purchasedHistory.any { it.checkoutList.isNotEmpty() }
}

