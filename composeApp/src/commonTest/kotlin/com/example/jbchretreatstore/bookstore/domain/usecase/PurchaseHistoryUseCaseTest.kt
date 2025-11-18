package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
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
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PurchaseHistoryUseCaseTest {

    private lateinit var repository: TestBookStoreRepository
    private lateinit var useCase: PurchaseHistoryUseCase

    @BeforeTest
    fun setup() {
        repository = TestBookStoreRepository()
        useCase = PurchaseHistoryUseCase(repository)
    }

    private fun createReceipt(
        buyerName: String,
        status: CheckoutStatus,
        totalPrice: Double
    ): ReceiptData {
        return ReceiptData(
            id = Uuid.random(),
            buyerName = buyerName,
            checkoutList = listOf(
                CheckoutItem(Uuid.random(), "Item", 1, emptyMap(), totalPrice)
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = status,
            dateTime = LocalDateTime(2025, 11, 17, 10, 0, 0)
        )
    }

    // ========== getAllReceipts Tests ==========

    @Test
    fun `getAllReceipts should return all receipts`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Bob", CheckoutStatus.CHECKED_OUT, 75.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getAllReceipts().first()
        assertEquals(3, result.size)
    }

    @Test
    fun `getAllReceipts with empty repository should return empty list`() = runTest {
        // When & Then
        val result = useCase.getAllReceipts().first()
        assertEquals(0, result.size)
    }

    // ========== getPurchaseHistory Tests ==========

    @Test
    fun `getPurchaseHistory should return only CHECKED_OUT receipts`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Bob", CheckoutStatus.CHECKED_OUT, 75.0),
            createReceipt("Alice", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getPurchaseHistory().first()
        assertEquals(2, result.size)
        assertTrue(result.all { it.checkoutStatus == CheckoutStatus.CHECKED_OUT })
    }

    @Test
    fun `getPurchaseHistory with no checked out items should return empty list`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Alice", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getPurchaseHistory().first()
        assertEquals(0, result.size)
    }

    // ========== getSavedForLater Tests ==========

    @Test
    fun `getSavedForLater should return only SAVE_FOR_LATER receipts`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Bob", CheckoutStatus.SAVE_FOR_LATER, 75.0),
            createReceipt("Alice", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getSavedForLater().first()
        assertEquals(2, result.size)
        assertTrue(result.all { it.checkoutStatus == CheckoutStatus.SAVE_FOR_LATER })
    }

    @Test
    fun `getSavedForLater with no saved items should return empty list`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Alice", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getSavedForLater().first()
        assertEquals(0, result.size)
    }

    // ========== calculateTotalRevenue Tests ==========

    @Test
    fun `calculateTotalRevenue should sum only CHECKED_OUT receipts`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Bob", CheckoutStatus.CHECKED_OUT, 75.0),
            createReceipt("Alice", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When
        val revenue = useCase.calculateTotalRevenue()

        // Then
        assertEquals(175.0, revenue)
    }

    @Test
    fun `calculateTotalRevenue with no purchases should return zero`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Alice", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When
        val revenue = useCase.calculateTotalRevenue()

        // Then
        assertEquals(0.0, revenue)
    }

    @Test
    fun `calculateTotalRevenue with empty repository should return zero`() = runTest {
        // When
        val revenue = useCase.calculateTotalRevenue()

        // Then
        assertEquals(0.0, revenue)
    }

    @Test
    fun `calculateTotalRevenue should sum all items in receipt`() = runTest {
        // Given
        val receipt = ReceiptData(
            id = Uuid.random(),
            buyerName = "John",
            checkoutList = listOf(
                CheckoutItem(Uuid.random(), "Item 1", 2, emptyMap(), 40.0),
                CheckoutItem(Uuid.random(), "Item 2", 1, emptyMap(), 25.0),
                CheckoutItem(Uuid.random(), "Item 3", 3, emptyMap(), 60.0)
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2025, 11, 17, 10, 0, 0)
        )
        repository.setReceipts(listOf(receipt))

        // When
        val revenue = useCase.calculateTotalRevenue()

        // Then
        assertEquals(125.0, revenue)
    }

    // ========== getReceiptsByBuyer Tests ==========

    @Test
    fun `getReceiptsByBuyer should return matching receipts case insensitive`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John Doe", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane Smith", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("John Smith", CheckoutStatus.CHECKED_OUT, 75.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getReceiptsByBuyer("john").first()
        assertEquals(2, result.size)
        assertTrue(result.all { it.buyerName.contains("John", ignoreCase = true) })
    }

    @Test
    fun `getReceiptsByBuyer with no matches should return empty list`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John Doe", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane Smith", CheckoutStatus.SAVE_FOR_LATER, 50.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getReceiptsByBuyer("Bob").first()
        assertEquals(0, result.size)
    }

    @Test
    fun `getReceiptsByBuyer should match partial names`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John Doe", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane Smith", CheckoutStatus.SAVE_FOR_LATER, 50.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getReceiptsByBuyer("Doe").first()
        assertEquals(1, result.size)
        assertEquals("John Doe", result[0].buyerName)
    }

    @Test
    fun `getReceiptsByBuyer with empty string should return all receipts`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John Doe", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane Smith", CheckoutStatus.SAVE_FOR_LATER, 50.0)
        )
        repository.setReceipts(receipts)

        // When & Then
        val result = useCase.getReceiptsByBuyer("").first()
        assertEquals(2, result.size)
    }

    // ========== getReceiptCount Tests ==========

    @Test
    fun `getReceiptCount should return correct count`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Bob", CheckoutStatus.CHECKED_OUT, 75.0)
        )
        repository.setReceipts(receipts)

        // When
        val count = useCase.getReceiptCount()

        // Then
        assertEquals(3, count)
    }

    @Test
    fun `getReceiptCount with empty repository should return zero`() = runTest {
        // When
        val count = useCase.getReceiptCount()

        // Then
        assertEquals(0, count)
    }

    @Test
    fun `getReceiptCount should count all statuses`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT, 100.0),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER, 50.0),
            createReceipt("Bob", CheckoutStatus.PENDING, 25.0)
        )
        repository.setReceipts(receipts)

        // When
        val count = useCase.getReceiptCount()

        // Then
        assertEquals(3, count)
    }
}

@OptIn(ExperimentalUuidApi::class)
class TestBookStoreRepository : BookStoreRepository {
    private var displayItems = mutableListOf<com.example.jbchretreatstore.bookstore.domain.model.DisplayItem>()
    private var receiptList = mutableListOf<ReceiptData>()

    fun setReceipts(receipts: List<ReceiptData>) {
        receiptList = receipts.toMutableList()
    }

    override suspend fun updateDisplayItems(items: List<com.example.jbchretreatstore.bookstore.domain.model.DisplayItem>) {
        displayItems = items.toMutableList()
    }

    override fun fetchDisplayItems(): Flow<List<com.example.jbchretreatstore.bookstore.domain.model.DisplayItem>> {
        return flowOf(displayItems)
    }

    override suspend fun updateReceiptList(items: List<ReceiptData>) {
        receiptList = items.toMutableList()
    }

    override fun fetchReceiptList(): Flow<List<ReceiptData>> {
        return flowOf(receiptList)
    }
}
