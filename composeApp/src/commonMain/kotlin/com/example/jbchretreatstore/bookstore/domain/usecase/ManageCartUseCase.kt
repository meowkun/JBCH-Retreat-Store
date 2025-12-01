package com.example.jbchretreatstore.bookstore.domain.usecase

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
            return Result.failure(IllegalArgumentException("Item name cannot be empty"))
        }

        // Validate quantity is positive
        if (newItem.quantity <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be greater than zero"))
        }

        // Validate price is positive
        if (newItem.totalPrice <= 0) {
            return Result.failure(IllegalArgumentException("Price must be greater than zero"))
        }

        // Check if item with same name and options already exists
        val existingItem = currentCart.checkoutList.find {
            it.itemName == newItem.itemName && it.variantsMap == newItem.variantsMap
        }

        val updatedList = if (existingItem != null) {
            // Update existing item quantity and price
            currentCart.checkoutList.map { item ->
                if (item.itemName == newItem.itemName && item.variantsMap == newItem.variantsMap) {
                    item.copy(
                        quantity = item.quantity + newItem.quantity,
                        totalPrice = item.totalPrice + newItem.totalPrice
                    )
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
        val updatedList = currentCart.checkoutList.filter { it.id != itemToRemove.id }

        if (updatedList.size == currentCart.checkoutList.size) {
            return Result.failure(IllegalArgumentException("Item not found in cart"))
        }

        return Result.success(currentCart.copy(checkoutList = updatedList))
    }

    /**
     * Update quantity of an existing cart item
     * @return Result with updated cart or failure with error message
     */
    fun updateQuantity(
        currentCart: ReceiptData,
        itemId: kotlin.uuid.Uuid,
        newQuantity: Int
    ): Result<ReceiptData> {
        // Validate quantity is positive
        if (newQuantity <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be greater than zero"))
        }

        // Find the item in cart
        val itemIndex = currentCart.checkoutList.indexOfFirst { it.id == itemId }
        if (itemIndex == -1) {
            return Result.failure(IllegalArgumentException("Item not found in cart"))
        }

        val item = currentCart.checkoutList[itemIndex]

        // Prevent division by zero
        if (item.quantity <= 0) {
            return Result.failure(IllegalStateException("Cart item has invalid quantity"))
        }

        // Calculate price per unit (safe from division by zero)
        val pricePerUnit = item.totalPrice / item.quantity

        // Validate price is positive
        if (pricePerUnit <= 0) {
            return Result.failure(IllegalStateException("Cart item has invalid price"))
        }

        // Update item with new quantity and recalculate total price
        val updatedItem = item.copy(
            quantity = newQuantity,
            totalPrice = pricePerUnit * newQuantity
        )

        val updatedList = currentCart.checkoutList.toMutableList().apply {
            set(itemIndex, updatedItem)
        }

        return Result.success(currentCart.copy(checkoutList = updatedList))
    }

    /**
     * Clear all items from cart
     * @return Result with empty cart
     */
    fun clearCart(currentCart: ReceiptData): Result<ReceiptData> {
        return Result.success(currentCart.copy(checkoutList = emptyList()))
    }

    /**
     * Calculate total price of all items in cart
     * @return Total price as Double
     */
    fun calculateTotal(cart: ReceiptData): Double {
        return cart.checkoutList.sumOf { it.totalPrice }
    }

    /**
     * Get total number of items in cart (sum of quantities)
     * @return Total item count
     */
    fun getItemCount(cart: ReceiptData): Int {
        return cart.checkoutList.sumOf { it.quantity }
    }

    /**
     * Validate cart before checkout
     * @return Result.success if valid, Result.failure with error message if invalid
     */
    fun validateCart(cart: ReceiptData): Result<Unit> {
        if (cart.checkoutList.isEmpty()) {
            return Result.failure(IllegalStateException("Cart is empty"))
        }

        if (cart.checkoutList.any { it.quantity <= 0 }) {
            return Result.failure(IllegalStateException("Invalid item quantity"))
        }

        if (cart.checkoutList.any { it.totalPrice <= 0 }) {
            return Result.failure(IllegalStateException("Invalid item price"))
        }

        if (cart.checkoutList.any { it.itemName.isBlank() }) {
            return Result.failure(IllegalStateException("Invalid item name"))
        }

        return Result.success(Unit)
    }

    /**
     * Check if cart contains a specific item
     * @return true if item exists in cart, false otherwise
     */
    fun containsItem(cart: ReceiptData, itemName: String, options: Map<String, String>): Boolean {
        return cart.checkoutList.any {
            it.itemName == itemName && it.variantsMap == options
        }
    }

    /**
     * Get item from cart by ID
     * @return Result with CheckoutItem or failure if not found
     */
    fun getItemById(cart: ReceiptData, itemId: kotlin.uuid.Uuid): Result<CheckoutItem> {
        val item = cart.checkoutList.find { it.id == itemId }
        return if (item != null) {
            Result.success(item)
        } else {
            Result.failure(IllegalArgumentException("Item not found in cart"))
        }
    }
}

