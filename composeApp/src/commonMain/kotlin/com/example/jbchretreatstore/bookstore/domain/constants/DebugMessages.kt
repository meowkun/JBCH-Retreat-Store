package com.example.jbchretreatstore.bookstore.domain.constants

/**
 * Centralized error messages for exceptions and validation failures.
 * These messages are used in Result.failure() and thrown exceptions.
 */
object ErrorMessages {
    // Checkout errors
    const val CHECKOUT_EMPTY_CART = "Cannot checkout with empty cart"
    const val CHECKOUT_INVALID_ITEMS = "Cart contains invalid items"

    // Cart validation errors
    const val CART_EMPTY = "Cart is empty"
    const val CART_ITEM_NOT_FOUND = "Item not found in cart"
    const val CART_INVALID_QUANTITY = "Cart item has invalid quantity"
    const val CART_INVALID_PRICE = "Cart item has invalid price"

    // Item validation errors
    const val ITEM_NAME_EMPTY = "Item name cannot be empty"
    const val ITEM_NAME_INVALID = "Invalid item name"
    const val ITEM_QUANTITY_INVALID = "Quantity must be greater than zero"
    const val ITEM_QUANTITY_VALIDATION_FAILED = "Invalid item quantity"
    const val ITEM_PRICE_INVALID = "Price must be greater than zero"
    const val ITEM_PRICE_VALIDATION_FAILED = "Invalid item price"
    const val ITEM_NOT_FOUND = "Item not found"

    // Display item errors
    fun itemAlreadyExists(itemName: String) = "Item with name '$itemName' already exists"

    // Data source errors
    const val SAVE_DISPLAY_ITEMS_FAILED = "Failed to save display items"
    const val SAVE_RECEIPTS_FAILED = "Failed to save receipts"
}

/**
 * Centralized debug/log messages for println statements.
 * These messages are used for debugging and logging purposes.
 */
object LogMessages {
    // Purchase history operations
    const val SHARE_FAILED_PREFIX = "Failed to share purchase history"
    const val REMOVE_RECEIPT_FAILED_PREFIX = "Failed to remove receipt"
    const val UPDATE_CHECKOUT_ITEM_FAILED_PREFIX = "Failed to update checkout item"
    const val UPDATE_BUYER_NAME_FAILED_PREFIX = "Failed to update buyer name"

    // Shop operations
    const val ADD_ITEM_FAILED_PREFIX = "Failed to add item"
    const val REMOVE_ITEM_FAILED_PREFIX = "Failed to remove item"
    const val UPDATE_ITEM_FAILED_PREFIX = "Failed to update item"
    const val ADD_TO_CART_FAILED_PREFIX = "Failed to add to cart"

    // Checkout operations
    const val CHECKOUT_FAILED_PREFIX = "Checkout failed"

    // Data source operations
    const val SAVE_DISPLAY_ITEMS_ERROR_PREFIX = "Error saving display items"
    const val LOAD_DISPLAY_ITEMS_ERROR_PREFIX = "Error loading display items"
    const val SAVE_RECEIPTS_ERROR_PREFIX = "Error saving receipts"
    const val LOAD_RECEIPTS_ERROR_PREFIX = "Error loading receipts"

    // Test data loader
    const val TEST_DATA_DISABLED = "TestDataLoader: Test data loading is disabled"
    const val TEST_DATA_LOADING_ALL = "TestDataLoader: Loading all test data..."
    const val TEST_DATA_LOADED_SUCCESS = "TestDataLoader: Test data loaded successfully"
    const val TEST_DATA_ALREADY_LOADED = "TestDataLoader: Test data already loaded, skipping"
    const val TEST_DATA_LOADING_FIRST_TIME =
        "TestDataLoader: Loading test data for the first time..."
    const val TEST_DATA_CLEARED = "TestDataLoader: All data cleared"
    const val TEST_DATA_FLAG_RESET = "TestDataLoader: Test data flag reset"

    // Helper function to append error message
    fun withError(prefix: String, errorMessage: String?) = "$prefix: $errorMessage"
}

