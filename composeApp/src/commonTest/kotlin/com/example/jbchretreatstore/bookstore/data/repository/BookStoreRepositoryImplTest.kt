package com.example.jbchretreatstore.bookstore.data.repository

import com.example.jbchretreatstore.bookstore.data.datasource.BookStoreLocalDataSource
import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BookStoreRepositoryImplTest {

    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var repository: BookStoreRepositoryImpl

    @BeforeTest
    fun setup() {
        localDataSource = FakeLocalDataSource()
        repository = BookStoreRepositoryImpl(localDataSource)
    }

    // ========== DisplayItems Tests ==========

    @Test
    fun `updateDisplayItems should save items to local data source`() = runTest {
        // Given
        val items = listOf(
            DisplayItem(Uuid.random(), "Item 1", 10.0, emptyList(), false),
            DisplayItem(Uuid.random(), "Item 2", 20.0, emptyList(), true)
        )

        // When
        repository.updateDisplayItems(items)

        // Then
        assertEquals(2, localDataSource.savedDisplayItems.size)
        assertEquals("Item 1", localDataSource.savedDisplayItems[0].name)
        assertEquals("Item 2", localDataSource.savedDisplayItems[1].name)
    }

    @Test
    fun `updateDisplayItems with empty list should save empty list`() = runTest {
        // When
        repository.updateDisplayItems(emptyList())

        // Then
        assertEquals(0, localDataSource.savedDisplayItems.size)
    }

    @Test
    fun `updateDisplayItems should map options correctly`() = runTest {
        // Given
        val items = listOf(
            DisplayItem(
                id = Uuid.random(),
                name = "Item with Options",
                price = 30.0,
                variants = listOf(
                    DisplayItem.Variant("size", listOf("S", "M", "L"))
                ),
                isInCart = false
            )
        )

        // When
        repository.updateDisplayItems(items)

        // Then
        assertEquals(1, localDataSource.savedDisplayItems[0].options.size)
        assertEquals("size", localDataSource.savedDisplayItems[0].options[0].optionKey)
        assertEquals(3, localDataSource.savedDisplayItems[0].options[0].optionValueList.size)
    }

    @Test
    fun `fetchDisplayItems should return items from local data source`() = runTest {
        // Given
        localDataSource.displayItems = listOf(
            DisplayItemDto(Uuid.random(), "Item 1", 10.0, emptyList(), false),
            DisplayItemDto(Uuid.random(), "Item 2", 20.0, emptyList(), true)
        )

        // When & Then
        val items = repository.fetchDisplayItems().first()
        assertEquals(2, items.size)
        assertEquals("Item 1", items[0].name)
        assertEquals("Item 2", items[1].name)
        assertFalse(items[0].isInCart)
        assertTrue(items[1].isInCart)
    }

    @Test
    fun `fetchDisplayItems with empty data source should return empty list`() = runTest {
        // Given
        localDataSource.displayItems = emptyList()

        // When & Then
        val items = repository.fetchDisplayItems().first()
        assertEquals(0, items.size)
    }

    @Test
    fun `fetchDisplayItems should map options correctly`() = runTest {
        // Given
        localDataSource.displayItems = listOf(
            DisplayItemDto(
                id = Uuid.random(),
                name = "Item",
                price = 30.0,
                options = listOf(
                    DisplayItemDto.OptionDto("color", listOf("Red", "Blue"))
                ),
                isInCart = false
            )
        )

        // When & Then
        val items = repository.fetchDisplayItems().first()
        assertEquals(1, items[0].variants.size)
        assertEquals("color", items[0].variants[0].key)
        assertEquals(2, items[0].variants[0].valueList.size)
    }

    // ========== ReceiptList Tests ==========

    @Test
    fun `updateReceiptList should save receipts to local data source`() = runTest {
        // Given
        val receipts = listOf(
            createReceipt("John", CheckoutStatus.CHECKED_OUT),
            createReceipt("Jane", CheckoutStatus.SAVE_FOR_LATER)
        )

        // When
        repository.updateReceiptList(receipts)

        // Then
        assertEquals(2, localDataSource.savedReceipts.size)
        assertEquals("John", localDataSource.savedReceipts[0].buyerName)
        assertEquals("Jane", localDataSource.savedReceipts[1].buyerName)
        assertEquals("CHECKED_OUT", localDataSource.savedReceipts[0].checkoutStatus)
        assertEquals("SAVE_FOR_LATER", localDataSource.savedReceipts[1].checkoutStatus)
    }

    @Test
    fun `updateReceiptList with empty list should save empty list`() = runTest {
        // When
        repository.updateReceiptList(emptyList())

        // Then
        assertEquals(0, localDataSource.savedReceipts.size)
    }

    @Test
    fun `updateReceiptList should map checkout items correctly`() = runTest {
        // Given
        val receipt = ReceiptData(
            id = Uuid.random(),
            buyerName = "Test",
            checkoutList = listOf(
                CheckoutItem(Uuid.random(), "Item 1", 2, mapOf("size" to "L"), 20.0),
                CheckoutItem(Uuid.random(), "Item 2", 1, emptyMap(), 10.0)
            ),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = LocalDateTime(2025, 11, 17, 10, 0, 0)
        )

        // When
        repository.updateReceiptList(listOf(receipt))

        // Then
        assertEquals(2, localDataSource.savedReceipts[0].checkoutList.size)
        assertEquals("Item 1", localDataSource.savedReceipts[0].checkoutList[0].itemName)
        assertEquals(2, localDataSource.savedReceipts[0].checkoutList[0].quantity)
    }

    @Test
    fun `fetchReceiptList should return receipts from local data source`() = runTest {
        // Given
        localDataSource.receipts = listOf(
            createReceiptDto("John", "CHECKED_OUT"),
            createReceiptDto("Jane", "SAVE_FOR_LATER")
        )

        // When & Then
        val receipts = repository.fetchReceiptList().first()
        assertEquals(2, receipts.size)
        assertEquals("John", receipts[0].buyerName)
        assertEquals("Jane", receipts[1].buyerName)
        assertEquals(CheckoutStatus.CHECKED_OUT, receipts[0].checkoutStatus)
        assertEquals(CheckoutStatus.SAVE_FOR_LATER, receipts[1].checkoutStatus)
    }

    @Test
    fun `fetchReceiptList with empty data source should return empty list`() = runTest {
        // Given
        localDataSource.receipts = emptyList()

        // When & Then
        val receipts = repository.fetchReceiptList().first()
        assertEquals(0, receipts.size)
    }

    @Test
    fun `fetchReceiptList should map all payment methods correctly`() = runTest {
        // Given
        localDataSource.receipts = listOf(
            createReceiptDto("Buyer1", "CHECKED_OUT", paymentMethod = "CASH"),
            createReceiptDto("Buyer2", "CHECKED_OUT", paymentMethod = "CREDIT_CARD"),
            createReceiptDto("Buyer3", "CHECKED_OUT", paymentMethod = "DEBIT_CARD"),
            createReceiptDto("Buyer4", "CHECKED_OUT", paymentMethod = "E_WALLET")
        )

        // When & Then
        val receipts = repository.fetchReceiptList().first()
        assertEquals(PaymentMethod.CASH, receipts[0].paymentMethod)
        assertEquals(PaymentMethod.valueOf("CREDIT_CARD"), receipts[1].paymentMethod)
        assertEquals(PaymentMethod.valueOf("DEBIT_CARD"), receipts[2].paymentMethod)
        assertEquals(PaymentMethod.valueOf("E_WALLET"), receipts[3].paymentMethod)
    }

    @Test
    fun `fetchReceiptList should map checkout items correctly`() = runTest {
        // Given
        localDataSource.receipts = listOf(
            ReceiptDataDto(
                id = Uuid.random(),
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItemDto(Uuid.random(), "Item 1", 2, mapOf("size" to "L"), 20.0),
                    CheckoutItemDto(Uuid.random(), "Item 2", 1, emptyMap(), 10.0)
                ),
                paymentMethod = "CASH",
                checkoutStatus = "CHECKED_OUT",
                dateTime = "2025-11-17T10:00:00"
            )
        )

        // When & Then
        val receipts = repository.fetchReceiptList().first()
        assertEquals(2, receipts[0].checkoutList.size)
        assertEquals("Item 1", receipts[0].checkoutList[0].itemName)
        assertEquals(2, receipts[0].checkoutList[0].quantity)
        assertEquals(1, receipts[0].checkoutList[0].variantsMap.size)
    }

    // ========== Integration Tests ==========

    @Test
    fun `updating and fetching display items should work end-to-end`() = runTest {
        // Given
        val originalItems = listOf(
            DisplayItem(Uuid.random(), "Test Item", 15.0, emptyList(), false)
        )

        // When
        repository.updateDisplayItems(originalItems)

        // Then
        val fetchedItems = repository.fetchDisplayItems().first()
        assertEquals(1, fetchedItems.size)
        assertEquals("Test Item", fetchedItems[0].name)
        assertEquals(15.0, fetchedItems[0].price)
    }

    @Test
    fun `updating and fetching receipts should work end-to-end`() = runTest {
        // Given
        val originalReceipts = listOf(
            createReceipt("Test Buyer", CheckoutStatus.CHECKED_OUT)
        )

        // When
        repository.updateReceiptList(originalReceipts)

        // Then
        val fetchedReceipts = repository.fetchReceiptList().first()
        assertEquals(1, fetchedReceipts.size)
        assertEquals("Test Buyer", fetchedReceipts[0].buyerName)
    }

    // Helper functions
    private fun createReceipt(buyerName: String, status: CheckoutStatus): ReceiptData {
        return ReceiptData(
            id = Uuid.random(),
            buyerName = buyerName,
            checkoutList = emptyList(),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = status,
            dateTime = LocalDateTime(2025, 11, 17, 10, 0, 0)
        )
    }

    private fun createReceiptDto(
        buyerName: String,
        status: String,
        paymentMethod: String = "CASH"
    ): ReceiptDataDto {
        return ReceiptDataDto(
            id = Uuid.random(),
            buyerName = buyerName,
            checkoutList = emptyList(),
            paymentMethod = paymentMethod,
            checkoutStatus = status,
            dateTime = "2025-11-17T10:00:00"
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
class FakeLocalDataSource : BookStoreLocalDataSource {
    var displayItems = listOf<DisplayItemDto>()
    var receipts = listOf<ReceiptDataDto>()
    var savedDisplayItems = listOf<DisplayItemDto>()
    var savedReceipts = listOf<ReceiptDataDto>()

    override suspend fun saveDisplayItems(items: List<DisplayItemDto>) {
        savedDisplayItems = items
        displayItems = items
    }

    override fun getDisplayItems(): Flow<List<DisplayItemDto>> {
        return flowOf(displayItems)
    }

    override suspend fun saveReceipts(receipts: List<ReceiptDataDto>) {
        savedReceipts = receipts
        this.receipts = receipts
    }

    override fun getReceipts(): Flow<List<ReceiptDataDto>> {
        return flowOf(receipts)
    }
}
