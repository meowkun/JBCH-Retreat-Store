package com.example.jbchretreatstore.bookstore.presentation.shared

import app.cash.turbine.test
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CartStateHolderTest {

    private lateinit var cartStateHolder: CartStateHolder

    @BeforeTest
    fun setup() {
        cartStateHolder = CartStateHolder()
    }

    // ============= INITIAL STATE TESTS =============

    @Test
    fun `initial state is empty ReceiptData`() = runTest {
        cartStateHolder.cartState.test {
            val cart = awaitItem()
            assertEquals("Unknown", cart.buyerName)
            assertTrue(cart.checkoutList.isEmpty())
            assertEquals(CheckoutStatus.PENDING, cart.checkoutStatus)
            assertEquals(PaymentMethod.CASH, cart.paymentMethod)
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= UPDATE CART TESTS =============

    @Test
    fun `updateCart updates the cart state`() = runTest {
        val newCart = ReceiptData(
            buyerName = "Test Buyer",
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 2, totalPrice = 40.0)
            ),
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = PaymentMethod.CREDIT_CARD
        )

        cartStateHolder.updateCart(newCart)

        cartStateHolder.cartState.test {
            val cart = awaitItem()
            assertEquals("Test Buyer", cart.buyerName)
            assertEquals(1, cart.checkoutList.size)
            assertEquals("Book", cart.checkoutList[0].itemName)
            assertEquals(CheckoutStatus.CHECKED_OUT, cart.checkoutStatus)
            assertEquals(PaymentMethod.CREDIT_CARD, cart.paymentMethod)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart with multiple items`() = runTest {
        val items = listOf(
            CheckoutItem(itemName = "Book 1", quantity = 1, totalPrice = 10.0),
            CheckoutItem(itemName = "Book 2", quantity = 2, totalPrice = 20.0),
            CheckoutItem(itemName = "Book 3", quantity = 3, totalPrice = 30.0)
        )
        val cart = ReceiptData(buyerName = "Test", checkoutList = items)

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals(3, result.checkoutList.size)
            assertEquals(60.0, result.checkoutList.sumOf { it.totalPrice })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart replaces previous cart completely`() = runTest {
        val firstCart = ReceiptData(
            buyerName = "First Buyer",
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 1", quantity = 5, totalPrice = 50.0)
            )
        )
        cartStateHolder.updateCart(firstCart)

        val secondCart = ReceiptData(
            buyerName = "Second Buyer",
            checkoutList = listOf(
                CheckoutItem(itemName = "Item 2", quantity = 1, totalPrice = 10.0)
            )
        )
        cartStateHolder.updateCart(secondCart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals("Second Buyer", result.buyerName)
            assertEquals(1, result.checkoutList.size)
            assertEquals("Item 2", result.checkoutList[0].itemName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart with empty checkout list`() = runTest {
        val cart = ReceiptData(buyerName = "Empty Cart Buyer", checkoutList = emptyList())

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals("Empty Cart Buyer", result.buyerName)
            assertTrue(result.checkoutList.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart preserves checkout item variants`() = runTest {
        val variants = listOf(
            CheckoutItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "M"
            ),
            CheckoutItem.Variant(
                key = "Color",
                valueList = listOf("Red", "Blue"),
                selectedValue = "Blue"
            )
        )
        val item = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 50.0
        )
        val cart = ReceiptData(buyerName = "Variant Buyer", checkoutList = listOf(item))

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            val savedItem = result.checkoutList[0]
            assertEquals(2, savedItem.variants.size)
            assertEquals("M", savedItem.variants[0].selectedValue)
            assertEquals("Blue", savedItem.variants[1].selectedValue)
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= CLEAR CART TESTS =============

    @Test
    fun `clearCart resets to empty ReceiptData`() = runTest {
        val cart = ReceiptData(
            buyerName = "Test Buyer",
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 2, totalPrice = 40.0)
            ),
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = PaymentMethod.VENMO
        )
        cartStateHolder.updateCart(cart)

        cartStateHolder.clearCart()

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals("Unknown", result.buyerName)
            assertTrue(result.checkoutList.isEmpty())
            assertEquals(CheckoutStatus.PENDING, result.checkoutStatus)
            assertEquals(PaymentMethod.CASH, result.paymentMethod)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearCart on already empty cart`() = runTest {
        cartStateHolder.clearCart()

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertTrue(result.checkoutList.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearCart generates new id`() = runTest {
        val originalCart = ReceiptData(
            buyerName = "Test",
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
            )
        )
        cartStateHolder.updateCart(originalCart)
        val originalId = cartStateHolder.cartState.value.id

        cartStateHolder.clearCart()

        val newId = cartStateHolder.cartState.value.id
        assertTrue(originalId != newId, "clearCart should generate a new receipt ID")
    }

    // ============= STATE FLOW EMISSION TESTS =============

    @Test
    fun `cartState emits updates correctly`() = runTest {
        cartStateHolder.cartState.test {
            // Initial state
            val initial = awaitItem()
            assertTrue(initial.checkoutList.isEmpty())

            // First update
            val firstCart = ReceiptData(
                buyerName = "First",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Item 1",
                        quantity = 1,
                        totalPrice = 10.0
                    )
                )
            )
            cartStateHolder.updateCart(firstCart)
            val first = awaitItem()
            assertEquals("First", first.buyerName)

            // Second update
            val secondCart = ReceiptData(
                buyerName = "Second",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Item 2",
                        quantity = 2,
                        totalPrice = 20.0
                    )
                )
            )
            cartStateHolder.updateCart(secondCart)
            val second = awaitItem()
            assertEquals("Second", second.buyerName)

            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= EDGE CASES =============

    @Test
    fun `updateCart with very large checkout list`() = runTest {
        val items = (1..100).map { i ->
            CheckoutItem(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
        }
        val cart = ReceiptData(buyerName = "Bulk Buyer", checkoutList = items)

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals(100, result.checkoutList.size)
            assertEquals(50500.0, result.checkoutList.sumOf { it.totalPrice })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart with unicode buyer name`() = runTest {
        val cart = ReceiptData(
            buyerName = "张三 李四",
            checkoutList = listOf(
                CheckoutItem(itemName = "经书", quantity = 1, totalPrice = 15.0)
            )
        )

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals("张三 李四", result.buyerName)
            assertEquals("经书", result.checkoutList[0].itemName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart with special characters`() = runTest {
        val cart = ReceiptData(
            buyerName = "José García-López O'Connor",
            checkoutList = listOf(
                CheckoutItem(itemName = "Book: 50% Off!", quantity = 1, totalPrice = 10.0)
            )
        )

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals("José García-López O'Connor", result.buyerName)
            assertEquals("Book: 50% Off!", result.checkoutList[0].itemName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart preserves item IDs`() = runTest {
        val item = CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
        val cart = ReceiptData(buyerName = "Test", checkoutList = listOf(item))

        cartStateHolder.updateCart(cart)

        cartStateHolder.cartState.test {
            val result = awaitItem()
            assertEquals(item.id, result.checkoutList[0].id)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCart with all payment methods`() = runTest {
        PaymentMethod.entries.forEach { method ->
            val cart = ReceiptData(
                buyerName = "Test",
                paymentMethod = method,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
                )
            )

            cartStateHolder.updateCart(cart)

            cartStateHolder.cartState.test {
                val result = awaitItem()
                assertEquals(method, result.paymentMethod)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `updateCart with all checkout statuses`() = runTest {
        CheckoutStatus.entries.forEach { status ->
            val cart = ReceiptData(
                buyerName = "Test",
                checkoutStatus = status,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
                )
            )

            cartStateHolder.updateCart(cart)

            cartStateHolder.cartState.test {
                val result = awaitItem()
                assertEquals(status, result.checkoutStatus)
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}

