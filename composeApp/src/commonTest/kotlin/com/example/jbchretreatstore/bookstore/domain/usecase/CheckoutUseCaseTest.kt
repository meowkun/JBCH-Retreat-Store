package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.fake.FakeBookStoreRepository
import com.example.jbchretreatstore.bookstore.presentation.CheckoutState
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CheckoutUseCaseTest {

    private lateinit var repository: FakeBookStoreRepository
    private lateinit var useCase: CheckoutUseCase

    @BeforeTest
    fun setup() {
        repository = FakeBookStoreRepository()
        useCase = CheckoutUseCase(repository)
    }

    // ============= PROCESS CHECKOUT (SIMPLE API) TESTS =============

    @Test
    fun `processCheckout succeeds with valid data`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 2, totalPrice = 40.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = PaymentMethod.CASH
        )

        assertTrue(result.isSuccess)
        assertEquals("John Doe", result.getOrThrow().buyerName)
        assertEquals(CheckoutStatus.CHECKED_OUT, result.getOrThrow().checkoutStatus)
        assertEquals(PaymentMethod.CASH, result.getOrThrow().paymentMethod)
    }

    @Test
    fun `processCheckout saves receipt to repository`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        useCase.processCheckout(
            cart = cart,
            buyerName = "Jane Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(repository.updateReceiptListCalled)
        assertNotNull(repository.lastSavedReceipts)
        assertEquals(1, repository.lastSavedReceipts?.size)
    }

    @Test
    fun `processCheckout appends to existing receipts`() = runTest {
        val existingReceipt = ReceiptData(
            buyerName = "Existing",
            checkoutList = listOf(CheckoutItem(itemName = "Old", quantity = 1, totalPrice = 10.0)),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(existingReceipt))

        val newCart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "New", quantity = 1, totalPrice = 20.0)
            )
        )

        useCase.processCheckout(
            cart = newCart,
            buyerName = "New Buyer",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertEquals(2, repository.lastSavedReceipts?.size)
    }

    @Test
    fun `processCheckout fails with empty buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout fails with blank buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "   ",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout fails with empty cart`() = runTest {
        val cart = ReceiptData(checkoutList = emptyList())

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals("Cannot checkout with empty cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout fails when cart contains zero quantity item`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 0, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout fails when cart contains negative quantity item`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = -1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout fails when cart contains zero price item`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 0.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout fails when cart contains negative price item`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = -10.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout sets timestamp`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrThrow().dateTime)
        assertTrue(result.getOrThrow().dateTime.year >= 2020)
    }

    @Test
    fun `processCheckout supports all payment methods`() = runTest {
        PaymentMethod.entries.forEach { method ->
            repository.reset()

            val cart = ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
                )
            )

            val result = useCase.processCheckout(
                cart = cart,
                buyerName = "John Doe",
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                paymentMethod = method
            )

            assertTrue(result.isSuccess, "Failed for payment method: $method")
            assertEquals(method, result.getOrThrow().paymentMethod)
        }
    }

    @Test
    fun `processCheckout supports all checkout statuses`() = runTest {
        CheckoutStatus.entries.forEach { status ->
            repository.reset()

            val cart = ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
                )
            )

            val result = useCase.processCheckout(
                cart = cart,
                buyerName = "John Doe",
                checkoutStatus = status
            )

            assertTrue(result.isSuccess, "Failed for status: $status")
            assertEquals(status, result.getOrThrow().checkoutStatus)
        }
    }

    @Test
    fun `processCheckout defaults to CASH payment method`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertEquals(PaymentMethod.CASH, result.getOrThrow().paymentMethod)
    }

    // ============= PROCESS CHECKOUT (WITH CHECKOUT STATE) TESTS =============

    @Test
    fun `processCheckout with CheckoutState succeeds`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )
        val checkoutState = CheckoutState(
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = PaymentMethod.CREDIT_CARD
        )

        val result = useCase.processCheckout(cart, checkoutState)

        assertTrue(result.isSuccess)
        assertEquals("John Doe", result.getOrThrow().buyerName)
        assertEquals(CheckoutStatus.CHECKED_OUT, result.getOrThrow().checkoutStatus)
        assertEquals(PaymentMethod.CREDIT_CARD, result.getOrThrow().paymentMethod)
    }

    @Test
    fun `processCheckout with CheckoutState validates buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )
        val checkoutState = CheckoutState(
            buyerName = "",
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = PaymentMethod.CASH
        )

        val result = useCase.processCheckout(cart, checkoutState)

        assertTrue(result.isFailure)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    // ============= SAVE FOR LATER TESTS =============

    @Test
    fun `saveForLater succeeds with valid data`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.saveForLater(cart, "John Doe")

        assertTrue(result.isSuccess)
        assertEquals("John Doe", result.getOrThrow().buyerName)
        assertEquals(CheckoutStatus.SAVE_FOR_LATER, result.getOrThrow().checkoutStatus)
        assertEquals(PaymentMethod.CASH, result.getOrThrow().paymentMethod)
    }

    @Test
    fun `saveForLater fails with empty buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.saveForLater(cart, "")

        assertTrue(result.isFailure)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveForLater fails with empty cart`() = runTest {
        val cart = ReceiptData(checkoutList = emptyList())

        val result = useCase.saveForLater(cart, "John Doe")

        assertTrue(result.isFailure)
        assertEquals("Cannot checkout with empty cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveForLater saves to repository`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        useCase.saveForLater(cart, "John Doe")

        assertTrue(repository.updateReceiptListCalled)
        assertEquals(1, repository.lastSavedReceipts?.size)
        assertEquals(
            CheckoutStatus.SAVE_FOR_LATER,
            repository.lastSavedReceipts?.first()?.checkoutStatus
        )
    }

    // ============= EDGE CASES =============

    @Test
    fun `processCheckout with mixed valid and invalid items fails`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Valid", quantity = 1, totalPrice = 20.0),
                CheckoutItem(itemName = "Invalid", quantity = 0, totalPrice = 20.0) // Invalid
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `processCheckout with very long buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )
        val longName = "A".repeat(1000)

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = longName,
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertEquals(1000, result.getOrThrow().buyerName.length)
    }

    @Test
    fun `processCheckout with unicode buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "张三 李四",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertEquals("张三 李四", result.getOrThrow().buyerName)
    }

    @Test
    fun `processCheckout with special characters in buyer name`() = runTest {
        val cart = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
            )
        )

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "José García-López O'Connor",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertEquals("José García-López O'Connor", result.getOrThrow().buyerName)
    }

    @Test
    fun `processCheckout with many items`() = runTest {
        val items = (1..100).map { i ->
            CheckoutItem(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
        }
        val cart = ReceiptData(checkoutList = items)

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "Bulk Buyer",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrThrow().checkoutList.size)
    }

    @Test
    fun `processCheckout preserves cart item ids`() = runTest {
        val item = CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 20.0)
        val cart = ReceiptData(checkoutList = listOf(item))

        val result = useCase.processCheckout(
            cart = cart,
            buyerName = "John Doe",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        assertTrue(result.isSuccess)
        assertEquals(item.id, result.getOrThrow().checkoutList[0].id)
    }
}

