package com.example.jbchretreatstore.bookstore.data.repository

import app.cash.turbine.test
import com.example.jbchretreatstore.bookstore.data.datasource.BookStoreLocalDataSource
import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class BookStoreRepositoryImplTest {

    private lateinit var fakeDataSource: FakeLocalDataSource
    private lateinit var repository: BookStoreRepositoryImpl

    @BeforeTest
    fun setup() {
        fakeDataSource = FakeLocalDataSource()
        repository = BookStoreRepositoryImpl(fakeDataSource)
    }

    // ============= DISPLAY ITEMS TESTS =============

    @Test
    fun `updateDisplayItems converts domain to dto and saves`() = runTest {
        val items = listOf(
            DisplayItem(name = "Book 1", price = 10.0),
            DisplayItem(name = "Book 2", price = 20.0)
        )

        repository.updateDisplayItems(items)

        assertTrue(fakeDataSource.saveDisplayItemsCalled)
        assertEquals(2, fakeDataSource.lastSavedDisplayItems?.size)
        assertEquals("Book 1", fakeDataSource.lastSavedDisplayItems?.get(0)?.name)
    }

    @Test
    fun `fetchDisplayItems returns flow of mapped domain items`() = runTest {
        val dtos = listOf(
            DisplayItemDto(name = "DTO Book 1", price = 15.0),
            DisplayItemDto(name = "DTO Book 2", price = 25.0)
        )
        fakeDataSource.setDisplayItems(dtos)

        repository.fetchDisplayItems().test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("DTO Book 1", items[0].name)
            assertEquals(15.0, items[0].price)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetchDisplayItems returns empty list when no items`() = runTest {
        repository.fetchDisplayItems().test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateDisplayItems with empty list`() = runTest {
        repository.updateDisplayItems(emptyList())

        assertTrue(fakeDataSource.saveDisplayItemsCalled)
        assertTrue(fakeDataSource.lastSavedDisplayItems?.isEmpty() == true)
    }

    @Test
    fun `displayItems preserves variants during mapping`() = runTest {
        val items = listOf(
            DisplayItem(
                name = "T-Shirt",
                price = 25.0,
                variants = listOf(
                    DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))
                )
            )
        )

        repository.updateDisplayItems(items)

        assertEquals(1, fakeDataSource.lastSavedDisplayItems?.first()?.options?.size)
        assertEquals(
            "Size",
            fakeDataSource.lastSavedDisplayItems?.first()?.options?.first()?.optionKey
        )
    }

    // ============= RECEIPT LIST TESTS =============

    @Test
    fun `updateReceiptList converts domain to dto and saves`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)),
            ReceiptData(buyerName = "Buyer 2", dateTime = LocalDateTime(2024, 1, 2, 0, 0, 0, 0))
        )

        repository.updateReceiptList(receipts)

        assertTrue(fakeDataSource.saveReceiptsCalled)
        assertEquals(2, fakeDataSource.lastSavedReceipts?.size)
        assertEquals("Buyer 1", fakeDataSource.lastSavedReceipts?.get(0)?.buyerName)
    }

    @Test
    fun `fetchReceiptList returns flow of mapped domain items`() = runTest {
        val dtos = listOf(
            ReceiptDataDto(
                buyerName = "DTO Buyer",
                paymentMethod = "CASH",
                checkoutStatus = "CHECKED_OUT",
                dateTime = "2024-01-01T10:30:00"
            )
        )
        fakeDataSource.setReceipts(dtos)

        repository.fetchReceiptList().test {
            val receipts = awaitItem()
            assertEquals(1, receipts.size)
            assertEquals("DTO Buyer", receipts[0].buyerName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetchReceiptList returns empty list when no receipts`() = runTest {
        repository.fetchReceiptList().test {
            val receipts = awaitItem()
            assertTrue(receipts.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateReceiptList with empty list`() = runTest {
        repository.updateReceiptList(emptyList())

        assertTrue(fakeDataSource.saveReceiptsCalled)
        assertTrue(fakeDataSource.lastSavedReceipts?.isEmpty() == true)
    }

    // ============= INTEGRATION TESTS =============

    @Test
    fun `round trip for display items`() = runTest {
        val originalItems = listOf(
            DisplayItem(
                name = "Test Book",
                price = 19.99,
                variants = listOf(
                    DisplayItem.Variant(
                        key = "Format",
                        valueList = listOf("Paperback", "Hardcover")
                    )
                ),
                isInCart = true
            )
        )

        repository.updateDisplayItems(originalItems)

        // Fetch what was saved through the fake data source
        repository.fetchDisplayItems().test {
            val fetchedItems = awaitItem()
            assertEquals(1, fetchedItems.size)
            assertEquals("Test Book", fetchedItems[0].name)
            assertEquals(19.99, fetchedItems[0].price)
            assertTrue(fetchedItems[0].isInCart)
            assertEquals(1, fetchedItems[0].variants.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `round trip for receipts`() = runTest {
        val originalReceipts = listOf(
            ReceiptData(
                buyerName = "Test Buyer",
                dateTime = LocalDateTime(2024, 6, 15, 14, 30, 0, 0)
            )
        )

        repository.updateReceiptList(originalReceipts)

        repository.fetchReceiptList().test {
            val fetchedReceipts = awaitItem()
            assertEquals(1, fetchedReceipts.size)
            assertEquals("Test Buyer", fetchedReceipts[0].buyerName)
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= FAKE DATA SOURCE =============

    private class FakeLocalDataSource : BookStoreLocalDataSource {
        private val _displayItems = MutableStateFlow<List<DisplayItemDto>>(emptyList())
        private val _receipts = MutableStateFlow<List<ReceiptDataDto>>(emptyList())
        private var testDataLoaded = false

        var saveDisplayItemsCalled = false
            private set
        var saveReceiptsCalled = false
            private set
        var lastSavedDisplayItems: List<DisplayItemDto>? = null
            private set
        var lastSavedReceipts: List<ReceiptDataDto>? = null
            private set

        override suspend fun saveDisplayItems(items: List<DisplayItemDto>) {
            saveDisplayItemsCalled = true
            lastSavedDisplayItems = items
            _displayItems.value = items
        }

        override fun getDisplayItems(): Flow<List<DisplayItemDto>> {
            return _displayItems.asStateFlow()
        }

        override suspend fun saveReceipts(receipts: List<ReceiptDataDto>) {
            saveReceiptsCalled = true
            lastSavedReceipts = receipts
            _receipts.value = receipts
        }

        override fun getReceipts(): Flow<List<ReceiptDataDto>> {
            return _receipts.asStateFlow()
        }

        override suspend fun isTestDataLoaded(): Boolean {
            return testDataLoaded
        }

        override suspend fun setTestDataLoaded(loaded: Boolean) {
            testDataLoaded = loaded
        }

        fun setDisplayItems(items: List<DisplayItemDto>) {
            _displayItems.value = items
        }

        fun setReceipts(receipts: List<ReceiptDataDto>) {
            _receipts.value = receipts
        }
    }
}

