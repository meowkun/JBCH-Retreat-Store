package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Use case for handling checkout process with business logic
 */
class CheckoutUseCase(
    private val repository: BookStoreRepository
) {

    /**
     * Process checkout with validation
     */
    @OptIn(ExperimentalTime::class)
    suspend fun processCheckout(
        cart: ReceiptData,
        buyerName: String,
        checkoutStatus: CheckoutStatus
    ): Result<ReceiptData> {
        // Validate buyer name
        if (buyerName.isBlank()) {
            return Result.failure(IllegalArgumentException("Buyer name cannot be empty"))
        }

        // Validate cart is not empty
        if (cart.checkoutList.isEmpty()) {
            return Result.failure(IllegalStateException("Cannot checkout with empty cart"))
        }

        // Validate all items have positive quantities and prices
        val hasInvalidItems = cart.checkoutList.any {
            it.quantity <= 0 || it.totalPrice <= 0
        }

        if (hasInvalidItems) {
            return Result.failure(IllegalStateException("Cart contains invalid items"))
        }

        // Create receipt with timestamp
        val receipt = cart.copy(
            buyerName = buyerName,
            checkoutStatus = checkoutStatus,
            dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        // Save to receipt history
        val currentReceipts = repository.fetchReceiptList().first()
        val updatedReceipts = currentReceipts + receipt
        repository.updateReceiptList(updatedReceipts)

        return Result.success(receipt)
    }

    /**
     * Save cart for later
     */
    @OptIn(ExperimentalTime::class)
    suspend fun saveForLater(
        cart: ReceiptData,
        buyerName: String
    ): Result<ReceiptData> {
        return processCheckout(cart, buyerName, CheckoutStatus.SAVE_FOR_LATER)
    }

    /**
     * Calculate total amount for checkout
     */
    fun calculateCheckoutTotal(cart: ReceiptData): Double {
        return cart.checkoutList.sumOf { it.totalPrice }
    }
}

