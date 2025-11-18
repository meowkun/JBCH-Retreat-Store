package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for managing purchase history
 */
class PurchaseHistoryUseCase(
    private val repository: BookStoreRepository
) {

    /**
     * Get all receipts
     */
    fun getAllReceipts(): Flow<List<ReceiptData>> {
        return repository.fetchReceiptList()
    }

    /**
     * Get purchased (checked out) items only
     */
    fun getPurchaseHistory(): Flow<List<ReceiptData>> {
        return repository.fetchReceiptList().map { receipts ->
            receipts.filter { it.checkoutStatus == CheckoutStatus.CHECKED_OUT }
        }
    }

    /**
     * Get saved for later items
     */
    fun getSavedForLater(): Flow<List<ReceiptData>> {
        return repository.fetchReceiptList().map { receipts ->
            receipts.filter { it.checkoutStatus == CheckoutStatus.SAVE_FOR_LATER }
        }
    }

    /**
     * Calculate total revenue from all purchases
     */
    suspend fun calculateTotalRevenue(): Double {
        return repository.fetchReceiptList().map { receipts ->
            receipts
                .filter { it.checkoutStatus == CheckoutStatus.CHECKED_OUT }
                .sumOf { receipt ->
                    receipt.checkoutList.sumOf { it.totalPrice }
                }
        }.map { it }.first()
    }

    /**
     * Get receipts by buyer name
     */
    fun getReceiptsByBuyer(buyerName: String): Flow<List<ReceiptData>> {
        return repository.fetchReceiptList().map { receipts ->
            receipts.filter {
                it.buyerName.contains(buyerName, ignoreCase = true)
            }
        }
    }

    /**
     * Get receipt count
     */
    suspend fun getReceiptCount(): Int {
        return repository.fetchReceiptList().map { it.size }.first()
    }
}

private suspend fun <T> Flow<T>.first(): T {
    var result: T? = null
    map { result = it }.collect {}
    return result!!
}

