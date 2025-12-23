package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ManageCartUseCaseTest {

    private val useCase = ManageCartUseCase()

    // ============= ADD TO CART TESTS =============

    @Test
    fun `addToCart succeeds with valid item`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "Test Book",
            quantity = 2,
            totalPrice = 40.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().checkoutList.size)
        assertEquals("Test Book", result.getOrThrow().checkoutList[0].itemName)
    }

    @Test
    fun `addToCart fails with empty item name`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "",
            quantity = 1,
            totalPrice = 10.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart fails with blank item name`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "   ",
            quantity = 1,
            totalPrice = 10.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart fails with zero quantity`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "Test",
            quantity = 0,
            totalPrice = 10.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart fails with negative quantity`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "Test",
            quantity = -1,
            totalPrice = 10.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart fails with zero price`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "Test",
            quantity = 1,
            totalPrice = 0.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isFailure)
        assertEquals("Price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart fails with negative price`() {
        val cart = ReceiptData()
        val newItem = CheckoutItem(
            itemName = "Test",
            quantity = 1,
            totalPrice = -10.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isFailure)
        assertEquals("Price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart updates existing item with same name and variants`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val existingItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 40.0
        )
        val cart = ReceiptData(checkoutList = listOf(existingItem))

        val newItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 3,
            variants = variants,
            totalPrice = 60.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().checkoutList.size)
        assertEquals(5, result.getOrThrow().checkoutList[0].quantity) // 2 + 3
        assertEquals(100.0, result.getOrThrow().checkoutList[0].totalPrice) // 40 + 60
    }

    @Test
    fun `addToCart adds new item when name matches but variants differ`() {
        val existingVariants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val existingItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = existingVariants,
            totalPrice = 40.0
        )
        val cart = ReceiptData(checkoutList = listOf(existingItem))

        val newVariants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("L"), selectedValue = "L")
        )
        val newItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 1,
            variants = newVariants,
            totalPrice = 20.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().checkoutList.size)
    }

    @Test
    fun `addToCart adds new item when variants match but name differs`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val existingItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 40.0
        )
        val cart = ReceiptData(checkoutList = listOf(existingItem))

        val newItem = CheckoutItem(
            itemName = "Polo Shirt",
            quantity = 1,
            variants = variants,
            totalPrice = 25.0
        )

        val result = useCase.addToCart(cart, newItem)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().checkoutList.size)
    }

    // ============= REMOVE FROM CART TESTS =============

    @Test
    fun `removeFromCart succeeds with existing item`() {
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 1,
            totalPrice = 10.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.removeFromCart(cart, item)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().checkoutList.isEmpty())
    }

    @Test
    fun `removeFromCart fails when item not found`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Existing", quantity = 1, totalPrice = 10.0)
            )
        )
        val nonExistentItem = CheckoutItem(
            id = Uuid.random(),
            itemName = "Non-existent",
            quantity = 1,
            totalPrice = 10.0
        )

        val result = useCase.removeFromCart(cart, nonExistentItem)

        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `removeFromCart removes only the specified item`() {
        val itemToRemove = CheckoutItem(itemName = "Remove Me", quantity = 1, totalPrice = 10.0)
        val itemToKeep = CheckoutItem(itemName = "Keep Me", quantity = 2, totalPrice = 20.0)
        val cart = ReceiptData(checkoutList = listOf(itemToRemove, itemToKeep))

        val result = useCase.removeFromCart(cart, itemToRemove)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().checkoutList.size)
        assertEquals("Keep Me", result.getOrThrow().checkoutList[0].itemName)
    }

    // ============= UPDATE QUANTITY TESTS =============

    @Test
    fun `updateQuantity succeeds with valid quantity`() {
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 2,
            totalPrice = 40.0 // $20 per unit
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.updateQuantity(cart, itemId, 5)

        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrThrow().checkoutList[0].quantity)
        assertEquals(100.0, result.getOrThrow().checkoutList[0].totalPrice) // 5 * $20
    }

    @Test
    fun `updateQuantity fails with zero quantity`() {
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 2,
            totalPrice = 40.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.updateQuantity(cart, itemId, 0)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity fails with negative quantity`() {
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 2,
            totalPrice = 40.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.updateQuantity(cart, itemId, -1)

        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity fails when item not found`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Existing", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = useCase.updateQuantity(cart, Uuid.random(), 5)

        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity fails when item has zero quantity edge case`() {
        val itemId = Uuid.random()
        // This is an edge case where an item might have 0 quantity due to a bug
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 0, // Invalid state
            totalPrice = 40.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.updateQuantity(cart, itemId, 5)

        assertTrue(result.isFailure)
        assertEquals("Cart item has invalid quantity", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity calculates price correctly with decimals`() {
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 3,
            totalPrice = 29.97 // $9.99 per unit
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.updateQuantity(cart, itemId, 2)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().checkoutList[0].quantity)
        // 9.99 * 2 = 19.98
        assertEquals(19.98, result.getOrThrow().checkoutList[0].totalPrice, 0.01)
    }

    // ============= CLEAR CART TESTS =============

    @Test
    fun `clearCart removes all items`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.0),
                CheckoutItem(itemName = "Item 2", quantity = 2, totalPrice = 20.0),
                CheckoutItem(itemName = "Item 3", quantity = 3, totalPrice = 30.0)
            )
        )

        val result = useCase.clearCart(cart)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().checkoutList.isEmpty())
    }

    @Test
    fun `clearCart on empty cart returns empty cart`() {
        val cart = ReceiptData()

        val result = useCase.clearCart(cart)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().checkoutList.isEmpty())
    }

    @Test
    fun `clearCart preserves other receipt data`() {
        val cart = ReceiptData(
            buyerName = "Test Buyer",
            checkoutList = listOf(CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 10.0))
        )

        val result = useCase.clearCart(cart)

        assertTrue(result.isSuccess)
        assertEquals("Test Buyer", result.getOrThrow().buyerName)
        assertTrue(result.getOrThrow().checkoutList.isEmpty())
    }

    // ============= CALCULATE TOTAL TESTS =============

    @Test
    fun `calculateTotal returns sum of all item prices`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.0),
                CheckoutItem(itemName = "Item 2", quantity = 2, totalPrice = 25.0),
                CheckoutItem(itemName = "Item 3", quantity = 3, totalPrice = 15.0)
            )
        )

        val total = useCase.calculateTotal(cart)

        assertEquals(50.0, total)
    }

    @Test
    fun `calculateTotal returns zero for empty cart`() {
        val cart = ReceiptData()

        val total = useCase.calculateTotal(cart)

        assertEquals(0.0, total)
    }

    @Test
    fun `calculateTotal handles decimal precision`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.99),
                CheckoutItem(itemName = "Item 2", quantity = 1, totalPrice = 5.01)
            )
        )

        val total = useCase.calculateTotal(cart)

        assertEquals(16.0, total, 0.001)
    }

    // ============= GET ITEM COUNT TESTS =============

    @Test
    fun `getItemCount returns total quantity of all items`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 1", quantity = 2, totalPrice = 10.0),
                CheckoutItem(itemName = "Item 2", quantity = 3, totalPrice = 20.0),
                CheckoutItem(itemName = "Item 3", quantity = 5, totalPrice = 30.0)
            )
        )

        val count = useCase.getItemCount(cart)

        assertEquals(10, count) // 2 + 3 + 5
    }

    @Test
    fun `getItemCount returns zero for empty cart`() {
        val cart = ReceiptData()

        val count = useCase.getItemCount(cart)

        assertEquals(0, count)
    }

    // ============= VALIDATE CART TESTS =============

    @Test
    fun `validateCart succeeds with valid cart`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `validateCart fails with empty cart`() {
        val cart = ReceiptData()

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Cart is empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails when item has zero quantity`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item", quantity = 0, totalPrice = 10.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item quantity", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails when item has negative quantity`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item", quantity = -1, totalPrice = 10.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item quantity", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails when item has zero price`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 0.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item price", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails when item has negative price`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Item", quantity = 1, totalPrice = -5.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item price", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails when item name is blank`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "   ", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item name", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails when item name is empty`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
        assertEquals("Invalid item name", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart fails on first invalid item`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Valid", quantity = 1, totalPrice = 10.0),
                CheckoutItem(itemName = "", quantity = 1, totalPrice = 10.0), // Invalid
                CheckoutItem(itemName = "Also Valid", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = useCase.validateCart(cart)

        assertTrue(result.isFailure)
    }

    // ============= CONTAINS ITEM TESTS =============

    @Test
    fun `containsItem returns true when item exists with matching variants`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val item = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 1,
            variants = variants,
            totalPrice = 20.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.containsItem(cart, "T-Shirt", mapOf("Size" to "M"))

        assertTrue(result)
    }

    @Test
    fun `containsItem returns false when item name doesn't match`() {
        val item = CheckoutItem(itemName = "T-Shirt", quantity = 1, totalPrice = 20.0)
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.containsItem(cart, "Polo Shirt", emptyMap())

        assertFalse(result)
    }

    @Test
    fun `containsItem returns false when variants don't match`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val item = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 1,
            variants = variants,
            totalPrice = 20.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.containsItem(cart, "T-Shirt", mapOf("Size" to "L"))

        assertFalse(result)
    }

    @Test
    fun `containsItem returns false for empty cart`() {
        val cart = ReceiptData()

        val result = useCase.containsItem(cart, "Item", emptyMap())

        assertFalse(result)
    }

    // ============= GET ITEM BY ID TESTS =============

    @Test
    fun `getItemById succeeds when item exists`() {
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Test",
            quantity = 1,
            totalPrice = 10.0
        )
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.getItemById(cart, itemId)

        assertTrue(result.isSuccess)
        assertEquals(item, result.getOrThrow())
    }

    @Test
    fun `getItemById fails when item doesn't exist`() {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Existing", quantity = 1, totalPrice = 10.0)
            )
        )

        val result = useCase.getItemById(cart, Uuid.random())

        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getItemById fails on empty cart`() {
        val cart = ReceiptData()

        val result = useCase.getItemById(cart, Uuid.random())

        assertTrue(result.isFailure)
    }
}

