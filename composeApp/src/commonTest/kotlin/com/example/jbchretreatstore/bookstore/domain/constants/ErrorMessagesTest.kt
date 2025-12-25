package com.example.jbchretreatstore.bookstore.domain.constants

import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorMessagesTest {

    // Checkout errors
    @Test
    fun `CHECKOUT_EMPTY_CART has correct message`() {
        assertEquals("Cannot checkout with empty cart", ErrorMessages.CHECKOUT_EMPTY_CART)
    }

    @Test
    fun `CHECKOUT_INVALID_ITEMS has correct message`() {
        assertEquals("Cart contains invalid items", ErrorMessages.CHECKOUT_INVALID_ITEMS)
    }

    // Cart validation errors
    @Test
    fun `CART_EMPTY has correct message`() {
        assertEquals("Cart is empty", ErrorMessages.CART_EMPTY)
    }

    @Test
    fun `CART_ITEM_NOT_FOUND has correct message`() {
        assertEquals("Item not found in cart", ErrorMessages.CART_ITEM_NOT_FOUND)
    }

    @Test
    fun `CART_INVALID_QUANTITY has correct message`() {
        assertEquals("Cart item has invalid quantity", ErrorMessages.CART_INVALID_QUANTITY)
    }

    @Test
    fun `CART_INVALID_PRICE has correct message`() {
        assertEquals("Cart item has invalid price", ErrorMessages.CART_INVALID_PRICE)
    }

    // Item validation errors
    @Test
    fun `ITEM_NAME_EMPTY has correct message`() {
        assertEquals("Item name cannot be empty", ErrorMessages.ITEM_NAME_EMPTY)
    }

    @Test
    fun `ITEM_NAME_INVALID has correct message`() {
        assertEquals("Invalid item name", ErrorMessages.ITEM_NAME_INVALID)
    }

    @Test
    fun `ITEM_QUANTITY_INVALID has correct message`() {
        assertEquals("Quantity must be greater than zero", ErrorMessages.ITEM_QUANTITY_INVALID)
    }

    @Test
    fun `ITEM_QUANTITY_VALIDATION_FAILED has correct message`() {
        assertEquals("Invalid item quantity", ErrorMessages.ITEM_QUANTITY_VALIDATION_FAILED)
    }

    @Test
    fun `ITEM_PRICE_INVALID has correct message`() {
        assertEquals("Price must be greater than zero", ErrorMessages.ITEM_PRICE_INVALID)
    }

    @Test
    fun `ITEM_PRICE_VALIDATION_FAILED has correct message`() {
        assertEquals("Invalid item price", ErrorMessages.ITEM_PRICE_VALIDATION_FAILED)
    }

    @Test
    fun `ITEM_NOT_FOUND has correct message`() {
        assertEquals("Item not found", ErrorMessages.ITEM_NOT_FOUND)
    }

    // Display item errors
    @Test
    fun `itemAlreadyExists returns formatted message with item name`() {
        val result = ErrorMessages.itemAlreadyExists("Bible")
        assertEquals("Item with name 'Bible' already exists", result)
    }

    @Test
    fun `itemAlreadyExists handles special characters in name`() {
        val result = ErrorMessages.itemAlreadyExists("Test's Item")
        assertEquals("Item with name 'Test's Item' already exists", result)
    }

    @Test
    fun `itemAlreadyExists handles empty name`() {
        val result = ErrorMessages.itemAlreadyExists("")
        assertEquals("Item with name '' already exists", result)
    }

    // Data source errors
    @Test
    fun `SAVE_DISPLAY_ITEMS_FAILED has correct message`() {
        assertEquals("Failed to save display items", ErrorMessages.SAVE_DISPLAY_ITEMS_FAILED)
    }

    @Test
    fun `SAVE_RECEIPTS_FAILED has correct message`() {
        assertEquals("Failed to save receipts", ErrorMessages.SAVE_RECEIPTS_FAILED)
    }
}

class LogMessagesTest {

    // Purchase history operations
    @Test
    fun `SHARE_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to share purchase history", LogMessages.SHARE_FAILED_PREFIX)
    }

    @Test
    fun `REMOVE_RECEIPT_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to remove receipt", LogMessages.REMOVE_RECEIPT_FAILED_PREFIX)
    }

    @Test
    fun `UPDATE_CHECKOUT_ITEM_FAILED_PREFIX has correct message`() {
        assertEquals(
            "Failed to update checkout item",
            LogMessages.UPDATE_CHECKOUT_ITEM_FAILED_PREFIX
        )
    }

    @Test
    fun `UPDATE_BUYER_NAME_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to update buyer name", LogMessages.UPDATE_BUYER_NAME_FAILED_PREFIX)
    }

    // Shop operations
    @Test
    fun `ADD_ITEM_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to add item", LogMessages.ADD_ITEM_FAILED_PREFIX)
    }

    @Test
    fun `REMOVE_ITEM_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to remove item", LogMessages.REMOVE_ITEM_FAILED_PREFIX)
    }

    @Test
    fun `UPDATE_ITEM_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to update item", LogMessages.UPDATE_ITEM_FAILED_PREFIX)
    }

    @Test
    fun `ADD_TO_CART_FAILED_PREFIX has correct message`() {
        assertEquals("Failed to add to cart", LogMessages.ADD_TO_CART_FAILED_PREFIX)
    }

    // Checkout operations
    @Test
    fun `CHECKOUT_FAILED_PREFIX has correct message`() {
        assertEquals("Checkout failed", LogMessages.CHECKOUT_FAILED_PREFIX)
    }

    // Data source operations
    @Test
    fun `SAVE_DISPLAY_ITEMS_ERROR_PREFIX has correct message`() {
        assertEquals("Error saving display items", LogMessages.SAVE_DISPLAY_ITEMS_ERROR_PREFIX)
    }

    @Test
    fun `LOAD_DISPLAY_ITEMS_ERROR_PREFIX has correct message`() {
        assertEquals("Error loading display items", LogMessages.LOAD_DISPLAY_ITEMS_ERROR_PREFIX)
    }

    @Test
    fun `SAVE_RECEIPTS_ERROR_PREFIX has correct message`() {
        assertEquals("Error saving receipts", LogMessages.SAVE_RECEIPTS_ERROR_PREFIX)
    }

    @Test
    fun `LOAD_RECEIPTS_ERROR_PREFIX has correct message`() {
        assertEquals("Error loading receipts", LogMessages.LOAD_RECEIPTS_ERROR_PREFIX)
    }

    // Test data loader messages
    @Test
    fun `TEST_DATA_DISABLED has correct message`() {
        assertEquals(
            "TestDataLoader: Test data loading is disabled",
            LogMessages.TEST_DATA_DISABLED
        )
    }

    @Test
    fun `TEST_DATA_LOADING_ALL has correct message`() {
        assertEquals("TestDataLoader: Loading all test data...", LogMessages.TEST_DATA_LOADING_ALL)
    }

    @Test
    fun `TEST_DATA_LOADED_SUCCESS has correct message`() {
        assertEquals(
            "TestDataLoader: Test data loaded successfully",
            LogMessages.TEST_DATA_LOADED_SUCCESS
        )
    }

    @Test
    fun `TEST_DATA_ALREADY_LOADED has correct message`() {
        assertEquals(
            "TestDataLoader: Test data already loaded, skipping",
            LogMessages.TEST_DATA_ALREADY_LOADED
        )
    }

    @Test
    fun `TEST_DATA_LOADING_FIRST_TIME has correct message`() {
        assertEquals(
            "TestDataLoader: Loading test data for the first time...",
            LogMessages.TEST_DATA_LOADING_FIRST_TIME
        )
    }

    @Test
    fun `TEST_DATA_CLEARED has correct message`() {
        assertEquals("TestDataLoader: All data cleared", LogMessages.TEST_DATA_CLEARED)
    }

    @Test
    fun `TEST_DATA_FLAG_RESET has correct message`() {
        assertEquals("TestDataLoader: Test data flag reset", LogMessages.TEST_DATA_FLAG_RESET)
    }

    // Helper function tests
    @Test
    fun `withError appends error message to prefix`() {
        val result = LogMessages.withError("Failed to load", "Network error")
        assertEquals("Failed to load: Network error", result)
    }

    @Test
    fun `withError handles null error message`() {
        val result = LogMessages.withError("Failed to load", null)
        assertEquals("Failed to load: null", result)
    }

    @Test
    fun `withError handles empty prefix`() {
        val result = LogMessages.withError("", "Some error")
        assertEquals(": Some error", result)
    }

    @Test
    fun `withError handles empty error message`() {
        val result = LogMessages.withError("Prefix", "")
        assertEquals("Prefix: ", result)
    }
}

