package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime
import kotlin.test.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ManageCartUseCaseTest {

    private lateinit var useCase: ManageCartUseCase
    private lateinit var emptyCart: ReceiptData

    @BeforeTest
    fun setup() {
        useCase = ManageCartUseCase()
        emptyCart = ReceiptData(
            id = Uuid.random(),
            buyerName = "",
            checkoutList = emptyList(),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.PENDING,
            dateTime = LocalDateTime(2025, 11, 17, 10, 0, 0)
        )
    }

    // ========== addToCart Tests ==========

    @Test
    fun `addToCart with valid item should add to empty cart`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "Test Item",
            quantity = 2,
            optionsMap = emptyMap(),
            totalPrice = 20.0
        )

        // When
        val result = useCase.addToCart(emptyCart, item)

        // Then
        assertTrue(result.isSuccess)
        val cart = result.getOrNull()
        assertNotNull(cart)
        assertEquals(1, cart.checkoutList.size)
        assertEquals("Test Item", cart.checkoutList[0].itemName)
        assertEquals(2, cart.checkoutList[0].quantity)
        assertEquals(20.0, cart.checkoutList[0].totalPrice)
    }

    @Test
    fun `addToCart with empty item name should fail`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val result = useCase.addToCart(emptyCart, item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with zero quantity should fail`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item",
            quantity = 0,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val result = useCase.addToCart(emptyCart, item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with negative quantity should fail`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item",
            quantity = -1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val result = useCase.addToCart(emptyCart, item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with zero price should fail`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 0.0
        )

        // When
        val result = useCase.addToCart(emptyCart, item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with negative price should fail`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = -10.0
        )

        // When
        val result = useCase.addToCart(emptyCart, item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToCart with existing item should merge quantities and prices`() {
        // Given
        val existingItem = CheckoutItem(
            id = Uuid.random(),
            itemName = "Coffee",
            quantity = 2,
            optionsMap = mapOf("size" to "Large"),
            totalPrice = 10.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(existingItem))

        val newItem = CheckoutItem(
            id = Uuid.random(),
            itemName = "Coffee",
            quantity = 1,
            optionsMap = mapOf("size" to "Large"),
            totalPrice = 5.0
        )

        // When
        val result = useCase.addToCart(cart, newItem)

        // Then
        assertTrue(result.isSuccess)
        val updatedCart = result.getOrNull()
        assertNotNull(updatedCart)
        assertEquals(1, updatedCart.checkoutList.size)
        assertEquals(3, updatedCart.checkoutList[0].quantity)
        assertEquals(15.0, updatedCart.checkoutList[0].totalPrice)
    }

    @Test
    fun `addToCart with different options should add as new item`() {
        // Given
        val existingItem = CheckoutItem(
            id = Uuid.random(),
            itemName = "Coffee",
            quantity = 1,
            optionsMap = mapOf("size" to "Small"),
            totalPrice = 5.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(existingItem))

        val newItem = CheckoutItem(
            id = Uuid.random(),
            itemName = "Coffee",
            quantity = 1,
            optionsMap = mapOf("size" to "Large"),
            totalPrice = 7.0
        )

        // When
        val result = useCase.addToCart(cart, newItem)

        // Then
        assertTrue(result.isSuccess)
        val updatedCart = result.getOrNull()
        assertNotNull(updatedCart)
        assertEquals(2, updatedCart.checkoutList.size)
    }

    // ========== removeFromCart Tests ==========

    @Test
    fun `removeFromCart with existing item should succeed`() {
        // Given
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Item",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(item))

        // When
        val result = useCase.removeFromCart(cart, item)

        // Then
        assertTrue(result.isSuccess)
        val updatedCart = result.getOrNull()
        assertNotNull(updatedCart)
        assertEquals(0, updatedCart.checkoutList.size)
    }

    @Test
    fun `removeFromCart with non-existing item should fail`() {
        // Given
        val item = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val result = useCase.removeFromCart(emptyCart, item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `removeFromCart should remove correct item from multiple items`() {
        // Given
        val item1 = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item 1",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )
        val item2 = CheckoutItem(
            id = Uuid.random(),
            itemName = "Item 2",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 20.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(item1, item2))

        // When
        val result = useCase.removeFromCart(cart, item1)

        // Then
        assertTrue(result.isSuccess)
        val updatedCart = result.getOrNull()
        assertNotNull(updatedCart)
        assertEquals(1, updatedCart.checkoutList.size)
        assertEquals("Item 2", updatedCart.checkoutList[0].itemName)
    }

    // ========== updateQuantity Tests ==========

    @Test
    fun `updateQuantity with valid new quantity should succeed`() {
        // Given
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Item",
            quantity = 2,
            optionsMap = emptyMap(),
            totalPrice = 20.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(item))

        // When
        val result = useCase.updateQuantity(cart, itemId, 5)

        // Then
        assertTrue(result.isSuccess)
        val updatedCart = result.getOrNull()
        assertNotNull(updatedCart)
        assertEquals(5, updatedCart.checkoutList[0].quantity)
        assertEquals(50.0, updatedCart.checkoutList[0].totalPrice)
    }

    @Test
    fun `updateQuantity with zero should fail`() {
        // Given
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Item",
            quantity = 2,
            optionsMap = emptyMap(),
            totalPrice = 20.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(item))

        // When
        val result = useCase.updateQuantity(cart, itemId, 0)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity with negative should fail`() {
        // Given
        val itemId = Uuid.random()
        val item = CheckoutItem(
            id = itemId,
            itemName = "Item",
            quantity = 2,
            optionsMap = emptyMap(),
            totalPrice = 20.0
        )
        val cart = emptyCart.copy(checkoutList = listOf(item))

        // When
        val result = useCase.updateQuantity(cart, itemId, -1)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Quantity must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateQuantity with non-existing item should fail`() {
        // When
        val result = useCase.updateQuantity(emptyCart, Uuid.random(), 5)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item not found in cart", result.exceptionOrNull()?.message)
    }

    // ========== clearCart Tests ==========

    @Test
    fun `clearCart should remove all items`() {
        // Given
        val items = listOf(
            CheckoutItem(Uuid.random(), "Item 1", 1, emptyMap(), 10.0),
            CheckoutItem(Uuid.random(), "Item 2", 2, emptyMap(), 20.0)
        )
        val cart = emptyCart.copy(checkoutList = items)

        // When
        val result = useCase.clearCart(cart)

        // Then
        assertTrue(result.isSuccess)
        val clearedCart = result.getOrNull()
        assertNotNull(clearedCart)
        assertEquals(0, clearedCart.checkoutList.size)
    }

    @Test
    fun `clearCart on empty cart should succeed`() {
        // When
        val result = useCase.clearCart(emptyCart)

        // Then
        assertTrue(result.isSuccess)
        val clearedCart = result.getOrNull()
        assertNotNull(clearedCart)
        assertEquals(0, clearedCart.checkoutList.size)
    }

    // ========== calculateTotal Tests ==========

    @Test
    fun `calculateTotal with empty cart should return zero`() {
        // When
        val total = useCase.calculateTotal(emptyCart)

        // Then
        assertEquals(0.0, total)
    }

    @Test
    fun `calculateTotal with items should return sum of prices`() {
        // Given
        val items = listOf(
            CheckoutItem(Uuid.random(), "Item 1", 2, emptyMap(), 20.0),
            CheckoutItem(Uuid.random(), "Item 2", 1, emptyMap(), 15.0),
            CheckoutItem(Uuid.random(), "Item 3", 3, emptyMap(), 30.0)
        )
        val cart = emptyCart.copy(checkoutList = items)

        // When
        val total = useCase.calculateTotal(cart)

        // Then
        assertEquals(65.0, total)
    }

    // ========== getItemCount Tests ==========

    @Test
    fun `getItemCount with empty cart should return zero`() {
        // When
        val count = useCase.getItemCount(emptyCart)

        // Then
        assertEquals(0, count)
    }

    @Test
    fun `getItemCount should return sum of quantities`() {
        // Given
        val items = listOf(
            CheckoutItem(Uuid.random(), "Item 1", 2, emptyMap(), 20.0),
            CheckoutItem(Uuid.random(), "Item 2", 1, emptyMap(), 15.0),
            CheckoutItem(Uuid.random(), "Item 3", 3, emptyMap(), 30.0)
        )
        val cart = emptyCart.copy(checkoutList = items)

        // When
        val count = useCase.getItemCount(cart)

        // Then
        assertEquals(6, count)
    }

    // ========== validateCart Tests ==========

    @Test
    fun `validateCart with empty cart should fail`() {
        // When
        val result = useCase.validateCart(emptyCart)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cart is empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart with valid cart should succeed`() {
        // Given
        val items = listOf(
            CheckoutItem(Uuid.random(), "Item 1", 2, emptyMap(), 20.0)
        )
        val cart = emptyCart.copy(checkoutList = items)

        // When
        val result = useCase.validateCart(cart)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `validateCart with zero quantity should fail`() {
        // Given
        val items = listOf(
            CheckoutItem(Uuid.random(), "Item 1", 0, emptyMap(), 20.0)
        )
        val cart = emptyCart.copy(checkoutList = items)

        // When
        val result = useCase.validateCart(cart)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid item quantity", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validateCart with negative quantity should fail`() {
        // Given
        val items = listOf(
            CheckoutItem(Uuid.random(), "Item 1", -1, emptyMap(), 20.0)
        )
        val cart = emptyCart.copy(checkoutList = items)

        // When
        val result = useCase.validateCart(cart)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Invalid item quantity", result.exceptionOrNull()?.message)
    }
}
