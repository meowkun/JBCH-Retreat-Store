package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutUseCaseTest {

    private lateinit var repository: BookStoreRepository
    private lateinit var useCase: CheckoutUseCase
    private lateinit var validCart: ReceiptData

    @BeforeTest
    fun setup() {
        repository = FakeBookStoreRepository()
        useCase = CheckoutUseCase(repository)

        validCart = ReceiptData(
            id = Uuid.random(),
            buyerName = "",
            checkoutList = listOf(
                CheckoutItem(
                    id = Uuid.random(),
                    itemName = "Test Item",
                    quantity = 2,
                    variantsMap = emptyMap(),
                    totalPrice = 20.0
                )
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.PENDING,
            dateTime = LocalDateTime(2025, 11, 17, 10, 0, 0)
        )
    }

    // ========== processCheckout Tests ==========

    @Test
    fun `processCheckout with valid data should succeed`() = runTest {
        // When
        val result = useCase.processCheckout(validCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertEquals("John Doe", receipt.buyerName)
        assertEquals(CheckoutStatus.CHECKED_OUT, receipt.checkoutStatus)
        val count = repository.fetchReceiptList().first().size
        assertEquals(1, count)
    }

    @Test
    fun `processCheckout with empty buyer name should fail`() = runTest {
        // When
        val result = useCase.processCheckout(validCart, "", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout with blank buyer name should fail`() = runTest {
        // When
        val result = useCase.processCheckout(validCart, "   ", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout with empty cart should fail`() = runTest {
        // Given
        val emptyCart = validCart.copy(checkoutList = emptyList())

        // When
        val result = useCase.processCheckout(emptyCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cannot checkout with empty cart", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout with zero quantity item should fail`() = runTest {
        // Given
        val invalidCart = validCart.copy(
            checkoutList = listOf(
                CheckoutItem(
                    id = Uuid.random(),
                    itemName = "Item",
                    quantity = 0,
                    variantsMap = emptyMap(),
                    totalPrice = 10.0
                )
            )
        )

        // When
        val result = useCase.processCheckout(invalidCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout with negative quantity should fail`() = runTest {
        // Given
        val invalidCart = validCart.copy(
            checkoutList = listOf(
                CheckoutItem(
                    id = Uuid.random(),
                    itemName = "Item",
                    quantity = -1,
                    variantsMap = emptyMap(),
                    totalPrice = 10.0
                )
            )
        )

        // When
        val result = useCase.processCheckout(invalidCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout with zero price should fail`() = runTest {
        // Given
        val invalidCart = validCart.copy(
            checkoutList = listOf(
                CheckoutItem(
                    id = Uuid.random(),
                    itemName = "Item",
                    quantity = 1,
                    variantsMap = emptyMap(),
                    totalPrice = 0.0
                )
            )
        )

        // When
        val result = useCase.processCheckout(invalidCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout with negative price should fail`() = runTest {
        // Given
        val invalidCart = validCart.copy(
            checkoutList = listOf(
                CheckoutItem(
                    id = Uuid.random(),
                    itemName = "Item",
                    quantity = 1,
                    variantsMap = emptyMap(),
                    totalPrice = -10.0
                )
            )
        )

        // When
        val result = useCase.processCheckout(invalidCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cart contains invalid items", result.exceptionOrNull()?.message)
    }

    @Test
    fun `processCheckout should add receipt to existing receipts`() = runTest {
        // Given
        val existingReceipt = ReceiptData(
            id = Uuid.random(),
            buyerName = "Jane Doe",
            checkoutList = emptyList(),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2025, 11, 16, 10, 0, 0)
        )
        repository.updateReceiptList(listOf(existingReceipt))

        // When
        val result = useCase.processCheckout(validCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isSuccess)
        val count = repository.fetchReceiptList().first().size
        assertEquals(2, count)
    }

    @Test
    fun `processCheckout should set timestamp`() = runTest {
        // When
        val result = useCase.processCheckout(validCart, "John Doe", CheckoutStatus.CHECKED_OUT)

        // Then
        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertNotNull(receipt.dateTime)
    }

    // ========== saveForLater Tests ==========

    @Test
    fun `saveForLater should set status to SAVE_FOR_LATER`() = runTest {
        // When
        val result = useCase.saveForLater(validCart, "John Doe")

        // Then
        assertTrue(result.isSuccess)
        val receipt = result.getOrNull()
        assertNotNull(receipt)
        assertEquals(CheckoutStatus.SAVE_FOR_LATER, receipt.checkoutStatus)
    }

    @Test
    fun `saveForLater with empty buyer name should fail`() = runTest {
        // When
        val result = useCase.saveForLater(validCart, "")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Buyer name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveForLater with empty cart should fail`() = runTest {
        // Given
        val emptyCart = validCart.copy(checkoutList = emptyList())

        // When
        val result = useCase.saveForLater(emptyCart, "John Doe")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Cannot checkout with empty cart", result.exceptionOrNull()?.message)
    }

}

// Fake repository for testing
@OptIn(ExperimentalUuidApi::class)
class FakeBookStoreRepository : BookStoreRepository {
    private var displayItems = mutableListOf<DisplayItem>()
    private var receiptList = mutableListOf<ReceiptData>()

    override suspend fun updateDisplayItems(items: List<DisplayItem>) {
        displayItems = items.toMutableList()
    }

    override fun fetchDisplayItems(): Flow<List<DisplayItem>> {
        return flowOf(displayItems)
    }

    override suspend fun updateReceiptList(items: List<ReceiptData>) {
        receiptList = items.toMutableList()
    }

    override fun fetchReceiptList(): Flow<List<ReceiptData>> {
        return flowOf(receiptList)
    }
}
