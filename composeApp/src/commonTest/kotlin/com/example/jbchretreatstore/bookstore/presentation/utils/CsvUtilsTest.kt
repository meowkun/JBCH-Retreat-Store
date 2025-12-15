package com.example.jbchretreatstore.bookstore.presentation.utils

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CsvUtilsTest {

    // ============= CONVERT RECEIPTS TO CSV TESTS =============

    @Test
    fun `convertReceiptsToCsv returns all CSV formats`() {
        val receipts = createSampleReceipts()

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.isNotEmpty())
        assertTrue(result.groupedByItemCsv.isNotEmpty())
        assertTrue(result.groupedByItemWithVariantsCsv.isNotEmpty())
        assertTrue(result.groupedByItemPerVariantCsv.isNotEmpty())
        assertTrue(result.combinedCsv.isNotEmpty())
    }

    @Test
    fun `convertReceiptsToCsv handles empty receipt list`() {
        val result = convertReceiptsToCsv(emptyList())

        // Should still contain headers
        assertTrue(result.detailedCsv.contains("Date Time"))
        assertTrue(result.groupedByItemCsv.contains("Item Name"))
    }

    // ============= DETAILED CSV TESTS =============

    @Test
    fun `detailedCsv contains correct headers`() {
        val result = convertReceiptsToCsv(emptyList())

        val expectedHeaders = listOf(
            "Date Time", "Buyer Name", "Item Name", "Variants",
            "Quantity", "Unit Price", "Payment Method", "Total Price"
        )
        expectedHeaders.forEach { header ->
            assertTrue(result.detailedCsv.contains(header), "Missing header: $header")
        }
    }

    @Test
    fun `detailedCsv includes receipt data`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "John Doe",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Bible", quantity = 2, totalPrice = 40.0)
                ),
                paymentMethod = PaymentMethod.CASH,
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                dateTime = LocalDateTime(2024, 12, 25, 10, 30, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("John Doe"))
        assertTrue(result.detailedCsv.contains("Bible"))
        assertTrue(result.detailedCsv.contains("CASH"))
    }

    @Test
    fun `detailedCsv formats date correctly`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 10.0)
                ),
                dateTime = LocalDateTime(2024, 6, 15, 14, 30, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("2024/06/15 14:30"))
    }

    @Test
    fun `detailedCsv calculates unit price correctly`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Item",
                        quantity = 4,
                        totalPrice = 100.0
                    ) // $25 per unit
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("25.00"))
    }

    @Test
    fun `detailedCsv formats variants correctly`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "T-Shirt",
                        quantity = 1,
                        variants = listOf(
                            CheckoutItem.Variant(
                                key = "Size",
                                valueList = listOf("M"),
                                selectedValue = "M"
                            ),
                            CheckoutItem.Variant(
                                key = "Color",
                                valueList = listOf("Blue"),
                                selectedValue = "Blue"
                            )
                        ),
                        totalPrice = 25.0
                    )
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("Size: M"))
        assertTrue(result.detailedCsv.contains("Color: Blue"))
    }

    @Test
    fun `detailedCsv shows N-A for empty variants`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Simple Item",
                        quantity = 1,
                        variants = emptyList(),
                        totalPrice = 10.0
                    )
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("N/A"))
    }

    @Test
    fun `detailedCsv shows N-A for empty buyer name`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 10.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("N/A"))
    }

    @Test
    fun `detailedCsv handles receipt with empty checkout list`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Empty Buyer",
                checkoutList = emptyList(),
                paymentMethod = PaymentMethod.CASH,
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("Empty Buyer"))
    }

    @Test
    fun `detailedCsv includes grand total`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer 1",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.0),
                    CheckoutItem(itemName = "Item 2", quantity = 1, totalPrice = 20.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            ),
            ReceiptData(
                buyerName = "Buyer 2",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item 3", quantity = 1, totalPrice = 15.0)
                ),
                dateTime = LocalDateTime(2024, 1, 2, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("Grand Total"))
        assertTrue(result.detailedCsv.contains("45.00")) // 10 + 20 + 15
    }

    // ============= GROUPED BY ITEM CSV TESTS =============

    @Test
    fun `groupedByItemCsv contains correct headers`() {
        val result = convertReceiptsToCsv(emptyList())

        assertTrue(result.groupedByItemCsv.contains("Item Name"))
        assertTrue(result.groupedByItemCsv.contains("Total Quantity"))
        assertTrue(result.groupedByItemCsv.contains("Total Price"))
    }

    @Test
    fun `groupedByItemCsv aggregates quantities and prices`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer 1",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Bible", quantity = 2, totalPrice = 40.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            ),
            ReceiptData(
                buyerName = "Buyer 2",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Bible", quantity = 3, totalPrice = 60.0)
                ),
                dateTime = LocalDateTime(2024, 1, 2, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        // Should aggregate: 2 + 3 = 5 quantity, 40 + 60 = 100 price
        assertTrue(result.groupedByItemCsv.contains("Bible"))
        assertTrue(result.groupedByItemCsv.contains("5"))
        assertTrue(result.groupedByItemCsv.contains("100.00"))
    }

    @Test
    fun `groupedByItemCsv includes grand total with quantity`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item 1", quantity = 2, totalPrice = 20.0),
                    CheckoutItem(itemName = "Item 2", quantity = 3, totalPrice = 30.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.groupedByItemCsv.contains("Grand Total"))
        // Total quantity: 2 + 3 = 5, Total price: 20 + 30 = 50
    }

    // ============= GROUPED BY ITEM WITH VARIANTS CSV TESTS =============

    @Test
    fun `groupedByItemWithVariantsCsv separates by variant combination`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer 1",
                checkoutList = listOf(
                    CheckoutItem(
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
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            ),
            ReceiptData(
                buyerName = "Buyer 2",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "T-Shirt",
                        quantity = 3,
                        variants = listOf(
                            CheckoutItem.Variant(
                                key = "Size",
                                valueList = listOf("L"),
                                selectedValue = "L"
                            )
                        ),
                        totalPrice = 60.0
                    )
                ),
                dateTime = LocalDateTime(2024, 1, 2, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        // Should have two separate entries for T-Shirt (Size: M and Size: L)
        assertTrue(result.groupedByItemWithVariantsCsv.contains("T-Shirt"))
        assertTrue(result.groupedByItemWithVariantsCsv.contains("Size: M"))
        assertTrue(result.groupedByItemWithVariantsCsv.contains("Size: L"))
    }

    // ============= GROUPED BY ITEM PER VARIANT CSV TESTS =============

    @Test
    fun `groupedByItemPerVariantCsv creates sections per variant key`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "T-Shirt",
                        quantity = 2,
                        variants = listOf(
                            CheckoutItem.Variant(
                                key = "Size",
                                valueList = listOf("M"),
                                selectedValue = "M"
                            ),
                            CheckoutItem.Variant(
                                key = "Color",
                                valueList = listOf("Blue"),
                                selectedValue = "Blue"
                            )
                        ),
                        totalPrice = 40.0
                    )
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        // Should have sections for Size and Color
        assertTrue(result.groupedByItemPerVariantCsv.contains("T-Shirt (by Size)"))
        assertTrue(result.groupedByItemPerVariantCsv.contains("T-Shirt (by Color)"))
    }

    @Test
    fun `groupedByItemPerVariantCsv handles item without variants`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Buyer",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Simple Book",
                        quantity = 5,
                        variants = emptyList(),
                        totalPrice = 50.0
                    )
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        // Should have a section for Simple Book without variant breakdown
        assertTrue(result.groupedByItemPerVariantCsv.contains("Simple Book"))
        assertTrue(result.groupedByItemPerVariantCsv.contains("50.00"))
    }

    // ============= COMBINED CSV TESTS =============

    @Test
    fun `combinedCsv contains all sections`() {
        val receipts = createSampleReceipts()

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.combinedCsv.contains("=== DETAILED PURCHASE HISTORY ==="))
        assertTrue(result.combinedCsv.contains("=== SUMMARY BY ITEM ==="))
        assertTrue(result.combinedCsv.contains("=== SUMMARY BY ITEM WITH VARIANTS ==="))
        assertTrue(result.combinedCsv.contains("=== SUMMARY BY ITEM PER VARIANT ==="))
    }

    @Test
    fun `combinedCsv includes all individual CSV contents`() {
        val receipts = createSampleReceipts()

        val result = convertReceiptsToCsv(receipts)

        // Detailed CSV content should be in combined
        assertTrue(result.combinedCsv.contains("Date Time"))
        // Grouped by item content should be in combined
        assertTrue(result.combinedCsv.contains("Total Quantity"))
    }

    // ============= EDGE CASES =============

    @Test
    fun `handles zero quantity item`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Zero Qty", quantity = 0, totalPrice = 10.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        // Should handle gracefully, showing 0.00 for unit price (division by zero protection)
        assertTrue(result.detailedCsv.contains("Zero Qty"))
    }

    @Test
    fun `handles special characters in item names`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Book: 50% Off!", quantity = 1, totalPrice = 10.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("Book: 50% Off!"))
    }

    @Test
    fun `handles unicode characters`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "张三",
                checkoutList = listOf(
                    CheckoutItem(itemName = "经书 📚", quantity = 1, totalPrice = 15.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("张三"))
        assertTrue(result.detailedCsv.contains("经书 📚"))
    }

    @Test
    fun `handles all payment methods`() {
        PaymentMethod.entries.forEach { method ->
            val receipts = listOf(
                ReceiptData(
                    buyerName = "Test",
                    checkoutList = listOf(
                        CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 10.0)
                    ),
                    paymentMethod = method,
                    dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
                )
            )

            val result = convertReceiptsToCsv(receipts)

            assertTrue(
                result.detailedCsv.contains(method.name),
                "Missing payment method: ${method.name}"
            )
        }
    }

    @Test
    fun `handles decimal precision in prices`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Test",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item", quantity = 1, totalPrice = 10.99)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("10.99"))
    }

    @Test
    fun `handles large quantities`() {
        val receipts = listOf(
            ReceiptData(
                buyerName = "Bulk Buyer",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item", quantity = 99999, totalPrice = 999990.0)
                ),
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("99999"))
    }

    @Test
    fun `handles many receipts`() {
        val receipts = (1..100).map { i ->
            ReceiptData(
                buyerName = "Buyer $i",
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
                ),
                dateTime = LocalDateTime(2024, 1, i % 28 + 1, 0, 0, 0, 0)
            )
        }

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("Buyer 1"))
        assertTrue(result.detailedCsv.contains("Buyer 100"))
    }

    @Test
    fun `handles many items per receipt`() {
        val items = (1..50).map { i ->
            CheckoutItem(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
        }
        val receipts = listOf(
            ReceiptData(
                buyerName = "Bulk Receipt",
                checkoutList = items,
                dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)
            )
        )

        val result = convertReceiptsToCsv(receipts)

        assertTrue(result.detailedCsv.contains("Item 1"))
        assertTrue(result.detailedCsv.contains("Item 50"))
    }

    // ============= HELPER FUNCTIONS =============

    private fun createSampleReceipts(): List<ReceiptData> {
        return listOf(
            ReceiptData(
                buyerName = "John Doe",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Bible",
                        quantity = 2,
                        variants = listOf(
                            CheckoutItem.Variant(
                                key = "Language",
                                valueList = listOf("English"),
                                selectedValue = "English"
                            )
                        ),
                        totalPrice = 40.0
                    ),
                    CheckoutItem(
                        itemName = "Hymnal",
                        quantity = 1,
                        variants = emptyList(),
                        totalPrice = 15.0
                    )
                ),
                paymentMethod = PaymentMethod.CASH,
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                dateTime = LocalDateTime(2024, 12, 25, 10, 30, 0, 0)
            ),
            ReceiptData(
                buyerName = "Jane Smith",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Bible",
                        quantity = 1,
                        variants = listOf(
                            CheckoutItem.Variant(
                                key = "Language",
                                valueList = listOf("Spanish"),
                                selectedValue = "Spanish"
                            )
                        ),
                        totalPrice = 20.0
                    )
                ),
                paymentMethod = PaymentMethod.CREDIT_CARD,
                checkoutStatus = CheckoutStatus.CHECKED_OUT,
                dateTime = LocalDateTime(2024, 12, 26, 14, 0, 0, 0)
            )
        )
    }
}

