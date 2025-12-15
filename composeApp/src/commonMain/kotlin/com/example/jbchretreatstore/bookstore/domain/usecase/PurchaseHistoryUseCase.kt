package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.data.testdata.SampleTestData
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi

/**
 * Use case for managing purchase history
 */
@OptIn(ExperimentalUuidApi::class)
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
        return repository.fetchReceiptList().first()
            .filter { it.checkoutStatus == CheckoutStatus.CHECKED_OUT }
            .sumOf { receipt -> receipt.checkoutList.sumOf { it.totalPrice } }
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

    /**
     * Remove a receipt from the purchase history
     */
    suspend fun removeReceipt(receipt: ReceiptData): Result<Unit> {
        return try {
            val currentReceipts = repository.fetchReceiptList().first()
            val updatedReceipts = currentReceipts.filter { it.id != receipt.id }
            repository.updateReceiptList(updatedReceipts)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update a checkout item within a receipt by matching itemName and variants.
     * If the updated item's variants match an existing item, merge them by combining quantities.
     */
    suspend fun updateCheckoutItemByVariants(
        receipt: ReceiptData,
        originalItem: CheckoutItem,
        updatedItem: CheckoutItem
    ): Result<Unit> {
        return try {
            val currentReceipts = repository.fetchReceiptList().first()
            val updatedReceipts = currentReceipts.map { r ->
                if (r.id == receipt.id) {
                    // Check if updated item's variants match an existing item (other than original)
                    val existingItem = r.checkoutList.find { item ->
                        item.itemName == updatedItem.itemName &&
                                item.variantsMap == updatedItem.variantsMap &&
                                // Make sure it's not the original item we're editing
                                !(item.itemName == originalItem.itemName && item.variantsMap == originalItem.variantsMap)
                    }

                    val newCheckoutList = if (existingItem != null) {
                        // Merge: combine quantities and remove the original item
                        // Use updatedItem's quantity (what user set in the edit form)
                        val unitPrice = if (updatedItem.quantity > 0) {
                            updatedItem.totalPrice / updatedItem.quantity
                        } else {
                            updatedItem.totalPrice
                        }
                        r.checkoutList.mapNotNull { item ->
                            when {
                                // This is the existing item with matching variants - add updated item's quantity
                                item.itemName == existingItem.itemName &&
                                        item.variantsMap == existingItem.variantsMap -> {
                                    val newQuantity = item.quantity + updatedItem.quantity
                                    item.copy(
                                        quantity = newQuantity,
                                        totalPrice = unitPrice * newQuantity
                                    )
                                }
                                // This is the original item being edited - remove it (merged into existing)
                                item.itemName == originalItem.itemName &&
                                        item.variantsMap == originalItem.variantsMap -> null
                                // Other items - keep as is
                                else -> item
                            }
                        }
                    } else {
                        // No merge needed - just update the item
                        r.checkoutList.map { item ->
                            if (item.itemName == originalItem.itemName &&
                                item.variantsMap == originalItem.variantsMap
                            ) {
                                updatedItem
                            } else item
                        }
                    }

                    r.copy(checkoutList = newCheckoutList)
                } else r
            }
            repository.updateReceiptList(updatedReceipts)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Load sample test data (for development/testing purposes)
     * This replaces existing receipts with sample data
     */
    suspend fun loadTestData() {
        repository.updateReceiptList(SampleTestData.samplePurchaseHistory)
    }

    /**
     * Clear all receipts
     */
    suspend fun clearAllReceipts() {
        repository.updateReceiptList(emptyList())
    }
}

