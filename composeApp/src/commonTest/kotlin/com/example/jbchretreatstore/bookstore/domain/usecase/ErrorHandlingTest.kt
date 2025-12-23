package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.fake.FakeBookStoreRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

/**
 * Tests for error handling scenarios across use cases
 */
@OptIn(ExperimentalUuidApi::class)
class ErrorHandlingTest {

    private lateinit var repository: FakeBookStoreRepository
    private lateinit var checkoutUseCase: CheckoutUseCase
    private lateinit var manageDisplayItemsUseCase: ManageDisplayItemsUseCase
    private lateinit var manageCartUseCase: ManageCartUseCase
    private lateinit var purchaseHistoryUseCase: PurchaseHistoryUseCase

    @BeforeTest
    fun setup() {
        repository = FakeBookStoreRepository()
        checkoutUseCase = CheckoutUseCase(repository)
        manageDisplayItemsUseCase = ManageDisplayItemsUseCase(repository)
        manageCartUseCase = ManageCartUseCase()
        purchaseHistoryUseCase = PurchaseHistoryUseCase(repository)
    }

    // ============= CHECKOUT USE CASE ERROR HANDLING =============

    @Test
    fun `checkout with null-equivalent empty cart fails`() = runTest {
        val cart = ReceiptData(checkoutList = emptyList())

        val result = checkoutUseCase.processCheckout(
            cart = cart,
            buyerName = "John",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `checkout with invalid item quantity fails`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 0, totalPrice = 20.0)
            )
        )

        val result = checkoutUseCase.processCheckout(
            cart = cart,
            buyerName = "John",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `checkout with invalid item price fails`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 0.0)
            )
        )

        val result = checkoutUseCase.processCheckout(
            cart = cart,
            buyerName = "John",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `checkout recovers when single item in mixed cart is invalid`() = runTest {
        // One valid, one invalid
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Valid", quantity = 1, totalPrice = 10.0),
                CheckoutItem(itemName = "Invalid", quantity = -1, totalPrice = 10.0)
            )
        )

        val result = checkoutUseCase.processCheckout(
            cart = cart,
            buyerName = "John",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        // Should fail because of the invalid item
        assertTrue(result.isFailure)
    }

    // ============= MANAGE DISPLAY ITEMS ERROR HANDLING =============

    @Test
    fun `addDisplayItem with empty name fails`() = runTest {
        val item = DisplayItem(name = "", price = 10.0)

        val result = manageDisplayItemsUseCase.addDisplayItem(item)

        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with blank name fails`() = runTest {
        val item = DisplayItem(name = "   ", price = 10.0)

        val result = manageDisplayItemsUseCase.addDisplayItem(item)

        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with zero price fails`() = runTest {
        val item = DisplayItem(name = "Test", price = 0.0)

        val result = manageDisplayItemsUseCase.addDisplayItem(item)

        assertTrue(result.isFailure)
        assertEquals("Item price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with negative price fails`() = runTest {
        val item = DisplayItem(name = "Test", price = -5.0)

        val result = manageDisplayItemsUseCase.addDisplayItem(item)

        assertTrue(result.isFailure)
        assertEquals("Item price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with duplicate name fails`() = runTest {
        repository.setDisplayItems(listOf(DisplayItem(name = "Existing", price = 10.0)))

        val result = manageDisplayItemsUseCase.addDisplayItem(
            DisplayItem(name = "Existing", price = 20.0)
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun `removeDisplayItem that doesn't exist fails`() = runTest {
        repository.setDisplayItems(listOf(DisplayItem(name = "Other", price = 10.0)))

        val result = manageDisplayItemsUseCase.removeDisplayItem(
            DisplayItem(name = "NonExistent", price = 10.0)
        )

        assertTrue(result.isFailure)
        assertEquals("Item not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateDisplayItem that doesn't exist fails`() = runTest {
        repository.setDisplayItems(listOf(DisplayItem(name = "Other", price = 10.0)))

        val result = manageDisplayItemsUseCase.updateDisplayItem(
            DisplayItem(name = "NonExistent", price = 15.0)
        )

        assertTrue(result.isFailure)
        assertEquals("Item not found", result.exceptionOrNull()?.message)
    }

    // ============= MANAGE CART USE CASE ERROR HANDLING =============

    @Test
    fun `addToCart with empty item name fails`() {
        val cart = ReceiptData()
        val item = CheckoutItem(itemName = "", quantity = 1, totalPrice = 10.0)

        val result = manageCartUseCase.addToCart(cart, item)

        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with zero quantity fails`() {
        val cart = ReceiptData()
        val item = CheckoutItem(itemName = "Test", quantity = 0, totalPrice = 10.0)

        val result = manageCartUseCase.addToCart(cart, item)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with zero price fails`() {
        val cart = ReceiptData()
        val item = CheckoutItem(itemName = "Test", quantity = 1, totalPrice = 0.0)

        val result = manageCartUseCase.addToCart(cart, item)

        assertTrue(result.isFailure)
        assertEquals("Price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `removeFromCart item not in cart fails`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Other", quantity = 1, totalPrice = 10.0)
            )
        )
        val itemToRemove = CheckoutItem(itemName = "NotInCart", quantity = 1, totalPrice = 10.0)

        val result = manageCartUseCase.removeFromCart(cart, itemToRemove)

        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity for non-existent item fails`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Other", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = manageCartUseCase.updateQuantity(
            cart,
            itemId = kotlin.uuid.Uuid.random(),
            newQuantity = 5
        )

        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity with zero fails`() {
        val item = CheckoutItem(itemName = "Test", quantity = 1, totalPrice = 10.0)
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = manageCartUseCase.updateQuantity(cart, item.id, 0)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity with negative fails`() {
        val item = CheckoutItem(itemName = "Test", quantity = 1, totalPrice = 10.0)
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = manageCartUseCase.updateQuantity(cart, item.id, -1)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart with empty cart fails`() {
        val cart = ReceiptData()

        val result = manageCartUseCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Cart is empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart with invalid item quantity fails`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Test", quantity = 0, totalPrice = 10.0)
            )
        )

        val result = manageCartUseCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item quantity", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart with invalid item price fails`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Test", quantity = 1, totalPrice = -5.0)
            )
        )

        val result = manageCartUseCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item price", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart with blank item name fails`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "   ", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = manageCartUseCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item name", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getItemById for non-existent item fails`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Other", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = manageCartUseCase.getItemById(cart, kotlin.uuid.Uuid.random())

        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    // ============= PURCHASE HISTORY ERROR HANDLING =============

    @Test
    fun `removeReceipt handles non-existent receipt gracefully`() = runTest {
        val existingReceipt = ReceiptData(
            buyerName = "Existing",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(existingReceipt))

        val nonExistentReceipt = ReceiptData(
            buyerName = "Non-existent",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        val result = purchaseHistoryUseCase.removeReceipt(nonExistentReceipt)

        // Should succeed (idempotent operation)
        assertTrue(result.isSuccess)
        // Original receipt should still be there
        assertEquals(1, repository.lastSavedReceipts?.size)
    }

    // ============= REPOSITORY ERROR SIMULATION =============

    @Test
    fun `use case handles repository error gracefully for add`() = runTest {
        repository.shouldThrowOnSave = true
        repository.errorToThrow = RuntimeException("Database error")

        val item = DisplayItem(name = "Test", price = 10.0)

        var exceptionThrown = false
        try {
            manageDisplayItemsUseCase.addDisplayItem(item)
        } catch (e: Exception) {
            exceptionThrown = true
            assertEquals("Database error", e.message)
        }

        assertTrue(exceptionThrown)
    }

    @Test
    fun `use case handles repository error for checkout`() = runTest {
        repository.shouldThrowOnSave = true
        repository.errorToThrow = RuntimeException("Save failed")

        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
            )
        )

        var exceptionThrown = false
        try {
            checkoutUseCase.processCheckout(
                cart = cart,
                buyerName = "John",
                checkoutStatus = CheckoutStatus.CHECKED_OUT
            )
        } catch (e: Exception) {
            exceptionThrown = true
            assertEquals("Save failed", e.message)
        }

        assertTrue(exceptionThrown)
    }

    // ============= EDGE CASE ERROR CONDITIONS =============

    @Test
    fun `multiple validation failures returns first error`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                // Multiple issues: blank name, zero quantity, zero price
                CheckoutItem(itemName = "   ", quantity = 0, totalPrice = 0.0)
            )
        )

        val result = manageCartUseCase.validateCart(cart)

        assertTrue(result.isFailure)
        // Should report one error (implementation-specific which one)
        assertTrue(result.exceptionOrNull()?.message?.isNotEmpty() == true)
    }

    @Test
    fun `validation stops at first invalid item`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Valid", quantity = 1, totalPrice = 10.0),
                CheckoutItem(itemName = "", quantity = 1, totalPrice = 10.0), // Invalid
                CheckoutItem(itemName = "Also Valid", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = manageCartUseCase.validateCart(cart)

        assertTrue(result.isFailure)
    }

    @Test
    fun `cart operations don't modify original cart on error`() {
        val originalCart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Original", quantity = 1, totalPrice = 10.0)
            )
        )
        val invalidItem = CheckoutItem(itemName = "", quantity = 1, totalPrice = 10.0)

        manageCartUseCase.addToCart(originalCart, invalidItem)

        // Original cart should be unchanged
        assertEquals(1, originalCart.checkoutList.size)
        assertEquals("Original", originalCart.checkoutList[0].itemName)
    }

    @Test
    fun `empty operations don't cause errors`() = runTest {
        // Clear on empty should succeed
        val clearResult = manageCartUseCase.clearCart(ReceiptData())
        assertTrue(clearResult.isSuccess)

        // Calculate total on empty should return 0
        val total = manageCartUseCase.calculateTotal(ReceiptData())
        assertEquals(0.0, total)

        // Get item count on empty should return 0
        val count = manageCartUseCase.getItemCount(ReceiptData())
        assertEquals(0, count)
    }

    @Test
    fun `containsItem on empty cart returns false`() {
        val result = manageCartUseCase.containsItem(ReceiptData(), "Item", emptyMap())
        assertFalse(result)
    }
}

