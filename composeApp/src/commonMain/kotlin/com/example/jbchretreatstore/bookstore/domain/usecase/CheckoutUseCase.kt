package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import com.example.jbchretreatstore.bookstore.presentation.CheckoutState
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
     * Process checkout with validation (simplified API for tests)
     */
    @OptIn(ExperimentalTime::class)
    suspend fun processCheckout(
        cart: ReceiptData,
        buyerName: String,
        checkoutStatus: CheckoutStatus,
        paymentMethod: PaymentMethod = PaymentMethod.CASH
    ): Result<ReceiptData> {
        // Use "Unknown" if buyer name is empty
        val finalBuyerName = buyerName.ifBlank { "Unknown" }

        // Validate cart is not empty
        if (cart.checkoutList.isEmpty()) {
            return Result.failure(IllegalStateException("Cannot checkout with empty cart"))
        }

        // Validate all items have positive quantities and prices
        if (cart.checkoutList.any { it.quantity <= 0 || it.totalPrice <= 0 }) {
            return Result.failure(IllegalStateException("Cart contains invalid items"))
        }

        // Create receipt with timestamp
        val receipt = cart.copy(
            buyerName = finalBuyerName,
            checkoutStatus = checkoutStatus,
            paymentMethod = paymentMethod,
            dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        // Save to receipt history
        val currentReceipts = repository.fetchReceiptList().first()
        val updatedReceipts = currentReceipts + receipt
        repository.updateReceiptList(updatedReceipts)

        return Result.success(receipt)
    }

    /**
     * Process checkout with CheckoutState (used by ViewModel)
     */
    suspend fun processCheckout(
        cart: ReceiptData,
        checkoutState: CheckoutState
    ): Result<ReceiptData> = processCheckout(
        cart = cart,
        buyerName = checkoutState.buyerName,
        checkoutStatus = checkoutState.checkoutStatus,
        paymentMethod = checkoutState.paymentMethod
    )

    /**
     * Save cart for later with SAVE_FOR_LATER status
     */
    suspend fun saveForLater(
        cart: ReceiptData,
        buyerName: String
    ): Result<ReceiptData> = processCheckout(
        cart = cart,
        buyerName = buyerName,
        checkoutStatus = CheckoutStatus.SAVE_FOR_LATER,
        paymentMethod = PaymentMethod.CASH
    )

}
