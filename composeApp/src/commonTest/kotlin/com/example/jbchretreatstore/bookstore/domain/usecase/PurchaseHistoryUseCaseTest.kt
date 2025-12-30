package com.example.jbchretreatstore.bookstore.domain.usecase

import app.cash.turbine.test
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.fake.FakeBookStoreRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PurchaseHistoryUseCaseTest {

    private lateinit var repository: FakeBookStoreRepository
    private lateinit var useCase: PurchaseHistoryUseCase

    @BeforeTest
    fun setup() {
        repository = FakeBookStoreRepository()
        useCase = PurchaseHistoryUseCase(repository)
    }

    // ============= GET ALL RECEIPTS TESTS =============

    @Test
    fun `getAllReceipts returns all receipts`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 3", checkoutStatus = CheckoutStatus.PENDING)
        )
        repository.setReceipts(receipts)

        useCase.getAllReceipts().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getAllReceipts returns empty list when no receipts`() = runTest {
        useCase.getAllReceipts().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= GET PURCHASE HISTORY TESTS =============

    @Test
    fun `getPurchaseHistory returns only checked out receipts`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 3", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Buyer 4", checkoutStatus = CheckoutStatus.PENDING)
        )
        repository.setReceipts(receipts)

        useCase.getPurchaseHistory().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.checkoutStatus == CheckoutStatus.CHECKED_OUT })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getPurchaseHistory returns empty when no checked out receipts`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.PENDING)
        )
        repository.setReceipts(receipts)

        useCase.getPurchaseHistory().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= GET SAVED FOR LATER TESTS =============

    @Test
    fun `getSavedForLater returns only saved for later receipts`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 3", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 4", checkoutStatus = CheckoutStatus.PENDING)
        )
        repository.setReceipts(receipts)

        useCase.getSavedForLater().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.checkoutStatus == CheckoutStatus.SAVE_FOR_LATER })
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getSavedForLater returns empty when none saved`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        )
        repository.setReceipts(receipts)

        useCase.getSavedForLater().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= CALCULATE TOTAL REVENUE TESTS =============

    @Test
    fun `calculateTotalRevenue returns sum of checked out items only`() = runTest {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer 1",
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.0),
                    CheckoutItem(itemName = "Item 2", quantity = 2, totalPrice = 20.0)
                )
            ),
            ReceiptData(
                buyerName = "Buyer 2",
                checkoutStatus = CheckoutStatus.SAVE_FOR_LATER,
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Item 3",
                        quantity = 1,
                        totalPrice = 100.0
                    ) // Should not count
                )
            ),
            ReceiptData(
                buyerName = "Buyer 3",
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item 4", quantity = 1, totalPrice = 15.0)
                )
            )
        )
        repository.setReceipts(receipts)

        val revenue = useCase.calculateTotalRevenue()

        assertEquals(45.0, revenue) // 10 + 20 + 15, not including the 100 from SAVE_FOR_LATER
    }

    @Test
    fun `calculateTotalRevenue returns zero when no purchases`() = runTest {
        val revenue = useCase.calculateTotalRevenue()

        assertEquals(0.0, revenue)
    }

    @Test
    fun `calculateTotalRevenue handles decimal precision`() = runTest {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer",
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.99),
                    CheckoutItem(itemName = "Item 2", quantity = 1, totalPrice = 5.01)
                )
            )
        )
        repository.setReceipts(receipts)

        val revenue = useCase.calculateTotalRevenue()

        assertEquals(16.0, revenue, 0.001)
    }

    // ============= GET RECEIPTS BY BUYER TESTS =============

    @Test
    fun `getReceiptsByBuyer finds matching receipts case insensitive`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "John Doe", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "JOHN DOE", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Jane Doe", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        )
        repository.setReceipts(receipts)

        useCase.getReceiptsByBuyer("john").test {
            val result = awaitItem()
            assertEquals(2, result.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getReceiptsByBuyer returns empty when no match`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "John Doe", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        )
        repository.setReceipts(receipts)

        useCase.getReceiptsByBuyer("Alice").test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getReceiptsByBuyer finds partial matches`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "John Smith", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(
                buyerName = "Johnny Appleseed",
                checkoutStatus = CheckoutStatus.CHECKED_OUT
            ),
            ReceiptData(buyerName = "Jane Doe", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        )
        repository.setReceipts(receipts)

        useCase.getReceiptsByBuyer("John").test {
            val result = awaitItem()
            assertEquals(2, result.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= GET RECEIPT COUNT TESTS =============

    @Test
    fun `getReceiptCount returns correct count`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 3", checkoutStatus = CheckoutStatus.PENDING)
        )
        repository.setReceipts(receipts)

        val count = useCase.getReceiptCount()

        assertEquals(3, count)
    }

    @Test
    fun `getReceiptCount returns zero when empty`() = runTest {
        val count = useCase.getReceiptCount()

        assertEquals(0, count)
    }

    // ============= REMOVE RECEIPT TESTS =============

    @Test
    fun `removeReceipt succeeds with existing receipt`() = runTest {
        val receiptId = Uuid.random()
        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Test",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val result = useCase.removeReceipt(receipt)

        assertTrue(result.isSuccess)
        assertTrue(repository.lastSavedReceipts?.isEmpty() == true)
    }

    @Test
    fun `removeReceipt removes only specified receipt`() = runTest {
        val receipt1 = ReceiptData(buyerName = "Keep", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        val receipt2 =
            ReceiptData(buyerName = "Remove", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        repository.setReceipts(listOf(receipt1, receipt2))

        val result = useCase.removeReceipt(receipt2)

        assertTrue(result.isSuccess)
        assertEquals(1, repository.lastSavedReceipts?.size)
        assertEquals("Keep", repository.lastSavedReceipts?.first()?.buyerName)
    }

    // ============= UPDATE CHECKOUT ITEM BY VARIANTS TESTS =============

    @Test
    fun `updateCheckoutItemByVariants updates item correctly`() = runTest {
        val receiptId = Uuid.random()
        val originalItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("M"),
                    selectedValue = "M"
                )
            ),
            totalPrice = 40.0
        )
        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Test",
            checkoutList = listOf(originalItem),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val updatedItem = originalItem.copy(
            quantity = 3,
            totalPrice = 60.0
        )

        val result = useCase.updateCheckoutItemByVariants(receipt, originalItem, updatedItem)

        assertTrue(result.isSuccess)
        val savedReceipt = repository.lastSavedReceipts?.first()
        assertEquals(3, savedReceipt?.checkoutList?.first()?.quantity)
        assertEquals(60.0, savedReceipt?.checkoutList?.first()?.totalPrice)
    }

    @Test
    fun `updateCheckoutItemByVariants merges items with same variants`() = runTest {
        val receiptId = Uuid.random()
        val variant =
            listOf(CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M"))

        // Two items with different variants
        val item1 = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("S"),
                    selectedValue = "S"
                )
            ),
            totalPrice = 40.0
        )
        val item2 = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 3,
            variants = variant,
            totalPrice = 60.0
        )

        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Test",
            checkoutList = listOf(item1, item2),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        // Update item1 to have same variants as item2 (should merge)
        val updatedItem1 = item1.copy(
            variants = variant,
            quantity = 1,
            totalPrice = 20.0
        )

        val result = useCase.updateCheckoutItemByVariants(receipt, item1, updatedItem1)

        assertTrue(result.isSuccess)
        val savedReceipt = repository.lastSavedReceipts?.first()
        // Should have merged: item2 (3) + updatedItem1 (1) = 4
        assertEquals(1, savedReceipt?.checkoutList?.size)
        assertEquals(4, savedReceipt?.checkoutList?.first()?.quantity)
    }

    @Test
    fun `updateCheckoutItemByVariants handles different receipt`() = runTest {
        val receipt1 = ReceiptData(
            buyerName = "Receipt 1",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Item 1",
                    quantity = 1,
                    totalPrice = 10.0
                )
            ),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        val receipt2 = ReceiptData(
            buyerName = "Receipt 2",
            checkoutList = listOf(
                CheckoutItem(
                    itemName = "Item 2",
                    quantity = 2,
                    totalPrice = 20.0
                )
            ),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt1, receipt2))

        val originalItem = receipt2.checkoutList.first()
        val updatedItem = originalItem.copy(quantity = 5, totalPrice = 50.0)

        val result = useCase.updateCheckoutItemByVariants(receipt2, originalItem, updatedItem)

        assertTrue(result.isSuccess)
        // Receipt 1 should be unchanged
        assertEquals(
            "Item 1",
            repository.lastSavedReceipts?.first()?.checkoutList?.first()?.itemName
        )
        // Receipt 2 should be updated
        assertEquals(5, repository.lastSavedReceipts?.get(1)?.checkoutList?.first()?.quantity)
    }

    @Test
    fun `updateCheckoutItemByVariants handles zero quantity edge case`() = runTest {
        val receiptId = Uuid.random()
        val originalItem = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 0, // Edge case: zero quantity
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("M"),
                    selectedValue = "M"
                )
            ),
            totalPrice = 40.0
        )
        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Test",
            checkoutList = listOf(originalItem),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val updatedItem = originalItem.copy(quantity = 2, totalPrice = 40.0)

        val result = useCase.updateCheckoutItemByVariants(receipt, originalItem, updatedItem)

        assertTrue(result.isSuccess)
    }

    // ============= EDGE CASES =============

    @Test
    fun `operations with large number of receipts`() = runTest {
        val receipts = (1..1000).map { i ->
            ReceiptData(
                buyerName = "Buyer $i",
                checkoutStatus = if (i % 2 == 0) CheckoutStatus.CHECKED_OUT else CheckoutStatus.SAVE_FOR_LATER,
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Item $i",
                        quantity = 1,
                        totalPrice = i.toDouble()
                    )
                )
            )
        }
        repository.setReceipts(receipts)

        val count = useCase.getReceiptCount()
        assertEquals(1000, count)

        useCase.getPurchaseHistory().test {
            val result = awaitItem()
            assertEquals(500, result.size) // Half are CHECKED_OUT
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `calculateTotalRevenue with many items per receipt`() = runTest {
        val items = (1..100).map { i ->
            CheckoutItem(itemName = "Item $i", quantity = 1, totalPrice = 1.0)
        }
        val receipt = ReceiptData(
            buyerName = "Bulk Buyer",
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            checkoutList = items
        )
        repository.setReceipts(listOf(receipt))

        val revenue = useCase.calculateTotalRevenue()

        assertEquals(100.0, revenue)
    }

    @Test
    fun `getReceiptsByBuyer with unicode name`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "张三", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "李四", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        )
        repository.setReceipts(receipts)

        useCase.getReceiptsByBuyer("张").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("张三", result.first().buyerName)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `revenue calculation with very large amounts`() = runTest {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Big Buyer",
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Expensive", quantity = 1, totalPrice = 999999999.99)
                )
            )
        )
        repository.setReceipts(receipts)

        val revenue = useCase.calculateTotalRevenue()

        assertEquals(999999999.99, revenue, 0.01)
    }

    // ============= UPDATE BUYER NAME TESTS =============

    @Test
    fun `updateBuyerName succeeds with valid name`() = runTest {
        val receiptId = Uuid.random()
        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Original Name",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val result = useCase.updateBuyerName(receipt, "New Name")

        assertTrue(result.isSuccess)
        assertEquals("New Name", repository.lastSavedReceipts?.first()?.buyerName)
    }

    @Test
    fun `updateBuyerName uses NA for empty name`() = runTest {
        val receiptId = Uuid.random()
        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Original Name",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val result = useCase.updateBuyerName(receipt, "")

        assertTrue(result.isSuccess)
        assertEquals("NA", repository.lastSavedReceipts?.first()?.buyerName)
    }

    @Test
    fun `updateBuyerName uses NA for blank name`() = runTest {
        val receiptId = Uuid.random()
        val receipt = ReceiptData(
            id = receiptId,
            buyerName = "Original Name",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val result = useCase.updateBuyerName(receipt, "   ")

        assertTrue(result.isSuccess)
        assertEquals("NA", repository.lastSavedReceipts?.first()?.buyerName)
    }

    @Test
    fun `updateBuyerName only updates specified receipt`() = runTest {
        val receipt1 =
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        val receipt2 =
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        repository.setReceipts(listOf(receipt1, receipt2))

        val result = useCase.updateBuyerName(receipt1, "Updated Buyer 1")

        assertTrue(result.isSuccess)
        assertEquals("Updated Buyer 1", repository.lastSavedReceipts?.first()?.buyerName)
        assertEquals("Buyer 2", repository.lastSavedReceipts?.get(1)?.buyerName)
    }

    @Test
    fun `updateBuyerName with unicode characters`() = runTest {
        val receipt =
            ReceiptData(buyerName = "Original", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        repository.setReceipts(listOf(receipt))

        val result = useCase.updateBuyerName(receipt, "张三 李四")

        assertTrue(result.isSuccess)
        assertEquals("张三 李四", repository.lastSavedReceipts?.first()?.buyerName)
    }

    @Test
    fun `updateBuyerName with special characters`() = runTest {
        val receipt =
            ReceiptData(buyerName = "Original", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        repository.setReceipts(listOf(receipt))

        val result = useCase.updateBuyerName(receipt, "José García-López O'Connor")

        assertTrue(result.isSuccess)
        assertEquals("José García-López O'Connor", repository.lastSavedReceipts?.first()?.buyerName)
    }

    @Test
    fun `updateBuyerName with very long name`() = runTest {
        val receipt =
            ReceiptData(buyerName = "Original", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        repository.setReceipts(listOf(receipt))
        val longName = "A".repeat(1000)

        val result = useCase.updateBuyerName(receipt, longName)

        assertTrue(result.isSuccess)
        assertEquals(1000, repository.lastSavedReceipts?.first()?.buyerName?.length)
    }

    // ============= CLEAR ALL RECEIPTS TESTS =============

    @Test
    fun `clearAllReceipts removes all receipts`() = runTest {
        val receipts = listOf(
            ReceiptData(buyerName = "Buyer 1", checkoutStatus = CheckoutStatus.CHECKED_OUT),
            ReceiptData(buyerName = "Buyer 2", checkoutStatus = CheckoutStatus.SAVE_FOR_LATER),
            ReceiptData(buyerName = "Buyer 3", checkoutStatus = CheckoutStatus.PENDING)
        )
        repository.setReceipts(receipts)

        useCase.clearAllReceipts()

        assertTrue(repository.lastSavedReceipts?.isEmpty() == true)
    }

    @Test
    fun `clearAllReceipts on empty repository`() = runTest {
        useCase.clearAllReceipts()

        assertTrue(repository.lastSavedReceipts?.isEmpty() == true)
    }

    // ============= REMOVE RECEIPT EDGE CASES =============

    @Test
    fun `removeReceipt handles non-existent receipt gracefully`() = runTest {
        val existingReceipt =
            ReceiptData(buyerName = "Existing", checkoutStatus = CheckoutStatus.CHECKED_OUT)
        repository.setReceipts(listOf(existingReceipt))

        val nonExistentReceipt = ReceiptData(
            id = Uuid.random(),
            buyerName = "Non-existent",
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )

        val result = useCase.removeReceipt(nonExistentReceipt)

        // Should succeed but not remove anything (item wasn't there)
        assertTrue(result.isSuccess)
        assertEquals(1, repository.lastSavedReceipts?.size)
    }

    @Test
    fun `removeReceipt from empty repository`() = runTest {
        val receipt = ReceiptData(buyerName = "Test", checkoutStatus = CheckoutStatus.CHECKED_OUT)

        val result = useCase.removeReceipt(receipt)

        assertTrue(result.isSuccess)
        assertTrue(repository.lastSavedReceipts?.isEmpty() == true)
    }

    // ============= UPDATE CHECKOUT ITEM ADDITIONAL TESTS =============

    @Test
    fun `updateCheckoutItemByVariants with item not in receipt`() = runTest {
        val receipt = ReceiptData(
            buyerName = "Test",
            checkoutList = listOf(
                CheckoutItem(itemName = "Existing Item", quantity = 1, totalPrice = 10.0)
            ),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val nonExistentItem = CheckoutItem(
            itemName = "Non-existent",
            quantity = 1,
            totalPrice = 10.0
        )
        val updatedItem = nonExistentItem.copy(quantity = 2, totalPrice = 20.0)

        val result = useCase.updateCheckoutItemByVariants(receipt, nonExistentItem, updatedItem)

        // Should succeed but not change anything
        assertTrue(result.isSuccess)
        assertEquals(1, repository.lastSavedReceipts?.first()?.checkoutList?.size)
        assertEquals(
            "Existing Item",
            repository.lastSavedReceipts?.first()?.checkoutList?.first()?.itemName
        )
    }

    @Test
    fun `updateCheckoutItemByVariants preserves receipt metadata`() = runTest {
        val originalItem = CheckoutItem(
            itemName = "Book",
            quantity = 1,
            totalPrice = 10.0
        )
        val receipt = ReceiptData(
            buyerName = "Test Buyer",
            checkoutList = listOf(originalItem),
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod.VENMO
        )
        repository.setReceipts(listOf(receipt))

        val updatedItem = originalItem.copy(quantity = 5, totalPrice = 50.0)

        val result = useCase.updateCheckoutItemByVariants(receipt, originalItem, updatedItem)

        assertTrue(result.isSuccess)
        val savedReceipt = repository.lastSavedReceipts?.first()
        assertEquals("Test Buyer", savedReceipt?.buyerName)
        assertEquals(CheckoutStatus.CHECKED_OUT, savedReceipt?.checkoutStatus)
        assertEquals(
            com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod.VENMO,
            savedReceipt?.paymentMethod
        )
    }

    @Test
    fun `updateCheckoutItemByVariants with empty variants`() = runTest {
        val originalItem = CheckoutItem(
            itemName = "Simple Book",
            quantity = 1,
            variants = emptyList(),
            totalPrice = 10.0
        )
        val receipt = ReceiptData(
            buyerName = "Test",
            checkoutList = listOf(originalItem),
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        )
        repository.setReceipts(listOf(receipt))

        val updatedItem = originalItem.copy(quantity = 3, totalPrice = 30.0)

        val result = useCase.updateCheckoutItemByVariants(receipt, originalItem, updatedItem)

        assertTrue(result.isSuccess)
        assertEquals(3, repository.lastSavedReceipts?.first()?.checkoutList?.first()?.quantity)
    }

    // ============= FLOW REACTIVITY TESTS =============

    @Test
    fun `getPurchaseHistory updates when repository changes`() = runTest {
        useCase.getPurchaseHistory().test {
            // Initial empty state
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            // Add a receipt
            val newReceipt = ReceiptData(
                buyerName = "New Buyer",
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
                )
            )
            repository.setReceipts(listOf(newReceipt))

            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals("New Buyer", updated.first().buyerName)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getSavedForLater updates when repository changes`() = runTest {
        useCase.getSavedForLater().test {
            // Initial empty state
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            // Add a saved receipt
            val savedReceipt = ReceiptData(
                buyerName = "Saved Buyer",
                checkoutStatus = CheckoutStatus.SAVE_FOR_LATER,
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book", quantity = 1, totalPrice = 10.0)
                )
            )
            repository.setReceipts(listOf(savedReceipt))

            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals("Saved Buyer", updated.first().buyerName)

            cancelAndConsumeRemainingEvents()
        }
    }
}

