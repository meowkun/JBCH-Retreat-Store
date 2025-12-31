package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.constants.ErrorMessages
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData

/**
 * Use case for managing shopping cart with business logic
 */
class ManageCartUseCase {

    /**
     * Add item to cart or update quantity if already exists
     * @return Result with updated cart or failure with error message
     */
    fun addToCart(
        currentCart: ReceiptData,
        newItem: CheckoutItem
    ): Result<ReceiptData> {
        // Validate item name is not empty
        if (newItem.itemName.isBlank()) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_NAME_EMPTY))
        }

        // Validate quantity is positive
        if (newItem.quantity <= 0) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_QUANTITY_INVALID))
        }

        // Validate price is positive
        if (newItem.unitPrice <= 0) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_PRICE_INVALID))
        }

        // Check if item with same name, variants, and unit price already exists
        val existingItem = currentCart.checkoutList.find {
            it.itemName == newItem.itemName &&
                    it.variantsMap == newItem.variantsMap &&
                    it.unitPrice == newItem.unitPrice
        }

        val updatedList = if (existingItem != null) {
            // Update existing item quantity
            currentCart.checkoutList.map { item ->
                if (item.itemName == newItem.itemName &&
                    item.variantsMap == newItem.variantsMap &&
                    item.unitPrice == newItem.unitPrice
                ) {
                    item.copy(quantity = item.quantity + newItem.quantity)
                } else {
                    item
                }
            }
        } else {
            // Add as new item
            currentCart.checkoutList + newItem
        }

        return Result.success(currentCart.copy(checkoutList = updatedList))
    }

    /**
     * Remove item from cart
     * @return Result with updated cart or failure if item not found
     */
    fun removeFromCart(
        currentCart: ReceiptData,
        itemToRemove: CheckoutItem
    ): Result<ReceiptData> {
        val updatedList = currentCart.checkoutList.filter {
            it != itemToRemove
        }

        if (updatedList.size == currentCart.checkoutList.size) {
            return Result.failure(IllegalArgumentException(ErrorMessages.CART_ITEM_NOT_FOUND))
        }

        return Result.success(currentCart.copy(checkoutList = updatedList))
    }

    /**
     * Update quantity of an existing cart item.
     * @return Result with updated cart or failure with error message.
     */
    fun updateQuantity(
        currentCart: ReceiptData,
        itemToUpdate: CheckoutItem,
        newQuantity: Int
    ): Result<ReceiptData> {
        // Find the item in cart
        val itemIndex = currentCart.checkoutList.indexOfFirst { it == itemToUpdate }
        if (itemIndex == -1) {
            return Result.failure(IllegalArgumentException(ErrorMessages.CART_ITEM_NOT_FOUND))
        }

        val item = currentCart.checkoutList[itemIndex]

        // Remove item if quantity <= 0
        if (newQuantity <= 0) {
            val updatedList = currentCart.checkoutList.filter {
                it != itemToUpdate
            }
            return Result.success(currentCart.copy(checkoutList = updatedList))
        }

        // Update item with new quantity (unitPrice stays the same)
        val updatedItem = item.copy(
            quantity = newQuantity
        )

        val updatedList = currentCart.checkoutList.toMutableList().apply {
            set(itemIndex, updatedItem)
        }

        return Result.success(currentCart.copy(checkoutList = updatedList))
    }

    /**
     * Clear all items from cart.
     * @return Result with empty cart.
     * @testOnly Currently only used in unit tests.
     */
    fun clearCart(currentCart: ReceiptData): Result<ReceiptData> {
        return Result.success(currentCart.copy(checkoutList = emptyList()))
    }

    /**
     * Calculate total price of all items in cart.
     * @return Total price as Double.
     * @testOnly Currently only used in unit tests.
     */
    fun calculateTotal(cart: ReceiptData): Double {
        return cart.checkoutList.sumOf { it.totalPrice }
    }

    /**
     * Get total number of items in cart (sum of quantities).
     * @return Total item count.
     * @testOnly Currently only used in unit tests.
     */
    fun getItemCount(cart: ReceiptData): Int {
        return cart.checkoutList.sumOf { it.quantity }
    }

    /**
     * Validate cart before checkout.
     * @return Result.success if valid, Result.failure with error message if invalid.
     * @testOnly Currently only used in unit tests.
     */
    fun validateCart(cart: ReceiptData): Result<Unit> {
        if (cart.checkoutList.isEmpty()) {
            return Result.failure(IllegalStateException(ErrorMessages.CART_EMPTY))
        }

        if (cart.checkoutList.any { it.quantity <= 0 }) {
            return Result.failure(IllegalStateException(ErrorMessages.ITEM_QUANTITY_VALIDATION_FAILED))
        }

        if (cart.checkoutList.any { it.totalPrice <= 0 }) {
            return Result.failure(IllegalStateException(ErrorMessages.ITEM_PRICE_VALIDATION_FAILED))
        }

        if (cart.checkoutList.any { it.itemName.isBlank() }) {
            return Result.failure(IllegalStateException(ErrorMessages.ITEM_NAME_INVALID))
        }

        return Result.success(Unit)
    }

    /**
     * Check if cart contains a specific item.
     * @return true if item exists in cart, false otherwise.
     * @testOnly Currently only used in unit tests.
     */
    fun containsItem(cart: ReceiptData, itemName: String, options: Map<String, String>): Boolean {
        return cart.checkoutList.any {
            it.itemName == itemName && it.variantsMap == options
        }
    }

    /**
     * Get item from cart by ID.
     * @return Result with CheckoutItem or failure if not found.
     * @testOnly Currently only used in unit tests.
     */
    fun getItemById(cart: ReceiptData, itemId: kotlin.uuid.Uuid): Result<CheckoutItem> {
        val item = cart.checkoutList.find { it.id == itemId }
        return if (item != null) {
            Result.success(item)
        } else {
            Result.failure(IllegalArgumentException(ErrorMessages.CART_ITEM_NOT_FOUND))
        }
    }
}

