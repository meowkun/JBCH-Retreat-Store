package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PurchaseHistoryUiStateTest {

    // ==================== Tests for totalAmount derived property ====================

    @Test
    fun `totalAmount returns zero for empty purchase history`() {
        val uiState = PurchaseHistoryUiState(purchasedHistory = emptyList())

        assertEquals(0.0, uiState.totalAmount)
    }

    @Test
    fun `totalAmount sums all receipt totals`() {
        val receipts = listOf(
            ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(totalPrice = 10.0),
                    CheckoutItem(totalPrice = 20.0)
                )
            ),
            ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(totalPrice = 30.0)
                )
            )
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(60.0, uiState.totalAmount)
    }

    @Test
    fun `totalAmount handles large amounts`() {
        val receipts = listOf(
            ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 99999.99))),
            ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 88888.88)))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(188888.87, uiState.totalAmount)
    }

    @Test
    fun `totalAmount handles decimal precision`() {
        val receipts = listOf(
            ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 0.01))),
            ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 0.02))),
            ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 0.03)))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(0.06, uiState.totalAmount)
    }

    @Test
    fun `formattedTotalAmount returns formatted currency string`() {
        val receipts = listOf(
            ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(totalPrice = 10.50),
                    CheckoutItem(totalPrice = 20.25)
                )
            )
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals("$30.75", uiState.formattedTotalAmount)
    }

    @Test
    fun `formattedTotalAmount handles zero`() {
        val uiState = PurchaseHistoryUiState(purchasedHistory = emptyList())

        assertEquals("$0.00", uiState.formattedTotalAmount)
    }

    // ==================== Tests for hasReceiptData derived property ====================

    @Test
    fun `hasReceiptData returns false for empty purchase history`() {
        val uiState = PurchaseHistoryUiState(purchasedHistory = emptyList())

        assertFalse(uiState.hasReceiptData)
    }

    @Test
    fun `hasReceiptData returns false when all receipts have empty checkout lists`() {
        val receipts = listOf(
            ReceiptData(checkoutList = emptyList()),
            ReceiptData(checkoutList = emptyList())
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertFalse(uiState.hasReceiptData)
    }

    @Test
    fun `hasReceiptData returns true when at least one receipt has items`() {
        val receipts = listOf(
            ReceiptData(checkoutList = emptyList()),
            ReceiptData(checkoutList = listOf(CheckoutItem(itemName = "Item")))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertTrue(uiState.hasReceiptData)
    }

    @Test
    fun `hasReceiptData returns true when all receipts have items`() {
        val receipts = listOf(
            ReceiptData(checkoutList = listOf(CheckoutItem(itemName = "Item1"))),
            ReceiptData(checkoutList = listOf(CheckoutItem(itemName = "Item2")))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertTrue(uiState.hasReceiptData)
    }

    // ==================== Tests for groupedReceipts derived property ====================

    @Test
    fun `groupedReceipts returns empty list for empty purchase history`() {
        val uiState = PurchaseHistoryUiState(purchasedHistory = emptyList())

        assertTrue(uiState.groupedReceipts.isEmpty())
    }

    @Test
    fun `groupedReceipts groups receipts by date`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 14, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 24, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(2, uiState.groupedReceipts.size)
    }

    @Test
    fun `groupedReceipts sorts dates descending (most recent first)`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 20, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 22, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)
        val dates = uiState.groupedReceipts.map { it.first }

        assertEquals(25, dates[0].dayOfMonth)
        assertEquals(22, dates[1].dayOfMonth)
        assertEquals(20, dates[2].dayOfMonth)
    }

    @Test
    fun `groupedReceipts sorts receipts within same date by time descending`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 8, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 16, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 12, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(1, uiState.groupedReceipts.size)
        val receiptsForDate = uiState.groupedReceipts[0].second

        assertEquals(16, receiptsForDate[0].dateTime.hour)
        assertEquals(12, receiptsForDate[1].dateTime.hour)
        assertEquals(8, receiptsForDate[2].dateTime.hour)
    }

    @Test
    fun `groupedReceipts returns correct receipt count per date`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 14, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 18, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 24, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        // First group (Dec 25) should have 3 receipts
        assertEquals(3, uiState.groupedReceipts[0].second.size)
        // Second group (Dec 24) should have 1 receipt
        assertEquals(1, uiState.groupedReceipts[1].second.size)
    }

    @Test
    fun `groupedReceipts handles receipts across years`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 31, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2025, 1, 1, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2023, 6, 15, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(3, uiState.groupedReceipts.size)
        assertEquals(2025, uiState.groupedReceipts[0].first.year)
        assertEquals(2024, uiState.groupedReceipts[1].first.year)
        assertEquals(2023, uiState.groupedReceipts[2].first.year)
    }

    // ==================== Tests for groupedByMonth derived property ====================

    @Test
    fun `groupedByMonth returns empty list for empty purchase history`() {
        val uiState = PurchaseHistoryUiState(purchasedHistory = emptyList())

        assertTrue(uiState.groupedByMonth.isEmpty())
    }

    @Test
    fun `groupedByMonth groups receipts by month`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 11, 25, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(2, uiState.groupedByMonth.size)
    }

    @Test
    fun `groupedByMonth sorts months descending`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 6, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 9, 15, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(Month.DECEMBER, uiState.groupedByMonth[0].yearMonth.month)
        assertEquals(Month.SEPTEMBER, uiState.groupedByMonth[1].yearMonth.month)
        assertEquals(Month.JUNE, uiState.groupedByMonth[2].yearMonth.month)
    }

    @Test
    fun `groupedByMonth handles multiple years`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2025, 1, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2023, 12, 15, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(3, uiState.groupedByMonth.size)
        assertEquals(2025, uiState.groupedByMonth[0].yearMonth.year)
        assertEquals(2024, uiState.groupedByMonth[1].yearMonth.year)
        assertEquals(2023, uiState.groupedByMonth[2].yearMonth.year)
    }

    @Test
    fun `groupedByMonth includes custom names when set`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            customMonthNames = mapOf("2024_11" to "Christmas Sales") // December is month index 11
        )

        assertEquals("Christmas Sales", uiState.groupedByMonth[0].customName)
    }

    @Test
    fun `groupedByMonth respects collapsed state`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 11, 15, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            collapsedMonths = setOf("2024_11") // December
        )

        assertFalse(uiState.groupedByMonth[0].isExpanded) // December collapsed
        assertTrue(uiState.groupedByMonth[1].isExpanded) // November expanded
    }

    // ==================== Tests for MonthGroup ====================

    @Test
    fun `MonthGroup displayName returns custom name when set`() {
        val monthGroup = MonthGroup(
            yearMonth = YearMonth(2024, Month.DECEMBER),
            customName = "Holiday Sales"
        )

        assertEquals("Holiday Sales", monthGroup.displayName)
    }

    @Test
    fun `MonthGroup displayName returns formatted month year when no custom name`() {
        val monthGroup = MonthGroup(
            yearMonth = YearMonth(2024, Month.DECEMBER),
            customName = null
        )

        assertEquals("December 2024", monthGroup.displayName)
    }

    @Test
    fun `MonthGroup totalPrice sums all receipt totals`() {
        val monthGroup = MonthGroup(
            yearMonth = YearMonth(2024, Month.DECEMBER),
            receipts = listOf(
                ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 10.0))),
                ReceiptData(checkoutList = listOf(CheckoutItem(totalPrice = 20.0)))
            )
        )

        assertEquals(30.0, monthGroup.totalPrice)
    }

    @Test
    fun `MonthGroup receiptCount returns correct count`() {
        val monthGroup = MonthGroup(
            yearMonth = YearMonth(2024, Month.DECEMBER),
            receipts = listOf(
                ReceiptData(),
                ReceiptData(),
                ReceiptData()
            )
        )

        assertEquals(3, monthGroup.receiptCount)
    }

    @Test
    fun `MonthGroup groupedByDate groups receipts by date within month`() {
        val monthGroup = MonthGroup(
            yearMonth = YearMonth(2024, Month.DECEMBER),
            receipts = listOf(
                ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 10, 0)),
                ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 14, 0)),
                ReceiptData(dateTime = LocalDateTime(2024, 12, 20, 10, 0))
            )
        )

        assertEquals(2, monthGroup.groupedByDate.size)
        assertEquals(25, monthGroup.groupedByDate[0].date.dayOfMonth)
        assertEquals(20, monthGroup.groupedByDate[1].date.dayOfMonth)
    }

    @Test
    fun `MonthGroup headerKey is unique for each month`() {
        val group1 = MonthGroup(yearMonth = YearMonth(2024, Month.DECEMBER))
        val group2 = MonthGroup(yearMonth = YearMonth(2024, Month.NOVEMBER))

        assertTrue(group1.headerKey != group2.headerKey)
        assertTrue(group1.headerKey.contains("month_header"))
    }

    // ==================== Tests for YearMonth ====================

    @Test
    fun `YearMonth key is unique for year and month`() {
        val ym1 = YearMonth(2024, Month.DECEMBER)
        val ym2 = YearMonth(2024, Month.NOVEMBER)
        val ym3 = YearMonth(2023, Month.DECEMBER)

        assertTrue(ym1.key != ym2.key)
        assertTrue(ym1.key != ym3.key)
        assertTrue(ym2.key != ym3.key)
    }

    @Test
    fun `YearMonth compareTo sorts by year then month`() {
        val dec2024 = YearMonth(2024, Month.DECEMBER)
        val nov2024 = YearMonth(2024, Month.NOVEMBER)
        val dec2023 = YearMonth(2023, Month.DECEMBER)

        assertTrue(dec2024 > nov2024)
        assertTrue(dec2024 > dec2023)
        assertTrue(nov2024 > dec2023)
    }

    @Test
    fun `YearMonth compareTo returns zero for equal values`() {
        val ym1 = YearMonth(2024, Month.DECEMBER)
        val ym2 = YearMonth(2024, Month.DECEMBER)

        assertEquals(0, ym1.compareTo(ym2))
    }

    // ==================== Tests for default state ====================

    @Test
    fun `default state has empty purchase history`() {
        val uiState = PurchaseHistoryUiState()

        assertTrue(uiState.purchasedHistory.isEmpty())
    }

    @Test
    fun `default state is loading`() {
        val uiState = PurchaseHistoryUiState()

        assertTrue(uiState.isLoading)
    }

    @Test
    fun `default state has no dialogs or bottom sheets shown`() {
        val uiState = PurchaseHistoryUiState()

        assertFalse(uiState.showRemoveDialog)
        assertFalse(uiState.showEditBottomSheet)
        assertFalse(uiState.showEditBuyerNameDialog)
        assertFalse(uiState.showEditMonthNameDialog)
    }

    @Test
    fun `default state has empty collapsed months set`() {
        val uiState = PurchaseHistoryUiState()

        assertTrue(uiState.collapsedMonths.isEmpty())
    }

    @Test
    fun `default state has empty custom month names map`() {
        val uiState = PurchaseHistoryUiState()

        assertTrue(uiState.customMonthNames.isEmpty())
    }

    // ==================== Tests for extension properties ====================

    @Test
    fun `ReceiptData formattedTotalPrice returns formatted currency`() {
        val receipt = ReceiptData(
            checkoutList = listOf(
                CheckoutItem(totalPrice = 25.99),
                CheckoutItem(totalPrice = 14.01)
            )
        )

        assertEquals("$40.00", receipt.formattedTotalPrice)
    }

    @Test
    fun `CheckoutItem formattedTotalPrice returns formatted currency`() {
        val item = CheckoutItem(totalPrice = 99.50)

        assertEquals("$99.50", item.formattedTotalPrice)
    }

    @Test
    fun `CheckoutItem formattedUnitPrice returns formatted currency`() {
        val item = CheckoutItem(totalPrice = 100.0, quantity = 4)

        assertEquals("$25.00", item.formattedUnitPrice)
    }

    @Test
    fun `CheckoutItem formattedUnitPrice handles quantity of 1`() {
        val item = CheckoutItem(totalPrice = 50.0, quantity = 1)

        assertEquals("$50.00", item.formattedUnitPrice)
    }

    @Test
    fun `ReceiptData uniqueKey combines id and dateTime`() {
        val receipt = ReceiptData(dateTime = LocalDateTime(2024, 12, 25, 10, 30))

        assertTrue(receipt.uniqueKey.contains(receipt.id.toString()))
        assertTrue(receipt.uniqueKey.contains("2024-12-25"))
    }

    @Test
    fun `LocalDate headerKey returns header prefix with date`() {
        val date = LocalDate(2024, 12, 25)

        assertEquals("header_2024-12-25", date.headerKey)
    }

    // ==================== Tests for editBottomSheetData derived property ====================

    @Test
    fun `editBottomSheetData returns data when all conditions met`() {
        val receipt = ReceiptData()
        val item = CheckoutItem()
        val uiState = PurchaseHistoryUiState(
            showEditBottomSheet = true,
            receiptToEdit = receipt,
            purchaseHistoryItemToEdit = item
        )

        val data = uiState.editBottomSheetData
        assertNotNull(data)
        assertEquals(receipt, data.receipt)
        assertEquals(item, data.item)
    }

    @Test
    fun `editBottomSheetData returns null when showEditBottomSheet is false`() {
        val receipt = ReceiptData()
        val item = CheckoutItem()
        val uiState = PurchaseHistoryUiState(
            showEditBottomSheet = false,
            receiptToEdit = receipt,
            purchaseHistoryItemToEdit = item
        )

        assertNull(uiState.editBottomSheetData)
    }

    @Test
    fun `editBottomSheetData returns null when receiptToEdit is null`() {
        val item = CheckoutItem()
        val uiState = PurchaseHistoryUiState(
            showEditBottomSheet = true,
            receiptToEdit = null,
            purchaseHistoryItemToEdit = item
        )

        assertNull(uiState.editBottomSheetData)
    }

    @Test
    fun `editBottomSheetData returns null when purchaseHistoryItemToEdit is null`() {
        val receipt = ReceiptData()
        val uiState = PurchaseHistoryUiState(
            showEditBottomSheet = true,
            receiptToEdit = receipt,
            purchaseHistoryItemToEdit = null
        )

        assertNull(uiState.editBottomSheetData)
    }

    @Test
    fun `editBottomSheetData returns null when all conditions are null or false`() {
        val uiState = PurchaseHistoryUiState(
            showEditBottomSheet = false,
            receiptToEdit = null,
            purchaseHistoryItemToEdit = null
        )

        assertNull(uiState.editBottomSheetData)
    }

    // ==================== Tests for editBuyerNameDialogData derived property ====================

    @Test
    fun `editBuyerNameDialogData returns receipt when all conditions met`() {
        val receipt = ReceiptData()
        val uiState = PurchaseHistoryUiState(
            showEditBuyerNameDialog = true,
            receiptToEditBuyerName = receipt
        )

        assertEquals(receipt, uiState.editBuyerNameDialogData)
    }

    @Test
    fun `editBuyerNameDialogData returns null when showEditBuyerNameDialog is false`() {
        val receipt = ReceiptData()
        val uiState = PurchaseHistoryUiState(
            showEditBuyerNameDialog = false,
            receiptToEditBuyerName = receipt
        )

        assertNull(uiState.editBuyerNameDialogData)
    }

    @Test
    fun `editBuyerNameDialogData returns null when receiptToEditBuyerName is null`() {
        val uiState = PurchaseHistoryUiState(
            showEditBuyerNameDialog = true,
            receiptToEditBuyerName = null
        )

        assertNull(uiState.editBuyerNameDialogData)
    }

    // ==================== Tests for editMonthNameDialogData derived property ====================

    @Test
    fun `editMonthNameDialogData returns data when all conditions met`() {
        val yearMonth = YearMonth(2024, Month.DECEMBER)
        val uiState = PurchaseHistoryUiState(
            showEditMonthNameDialog = true,
            monthToEditName = yearMonth,
            customMonthNames = mapOf(yearMonth.key to "Custom Name")
        )

        val data = uiState.editMonthNameDialogData
        assertNotNull(data)
        assertEquals(yearMonth, data.first)
        assertEquals("Custom Name", data.second)
    }

    @Test
    fun `editMonthNameDialogData returns null custom name when not set`() {
        val yearMonth = YearMonth(2024, Month.DECEMBER)
        val uiState = PurchaseHistoryUiState(
            showEditMonthNameDialog = true,
            monthToEditName = yearMonth,
            customMonthNames = emptyMap()
        )

        val data = uiState.editMonthNameDialogData
        assertNotNull(data)
        assertEquals(yearMonth, data.first)
        assertNull(data.second)
    }

    @Test
    fun `editMonthNameDialogData returns null when showEditMonthNameDialog is false`() {
        val yearMonth = YearMonth(2024, Month.DECEMBER)
        val uiState = PurchaseHistoryUiState(
            showEditMonthNameDialog = false,
            monthToEditName = yearMonth
        )

        assertNull(uiState.editMonthNameDialogData)
    }

    @Test
    fun `editMonthNameDialogData returns null when monthToEditName is null`() {
        val uiState = PurchaseHistoryUiState(
            showEditMonthNameDialog = true,
            monthToEditName = null
        )

        assertNull(uiState.editMonthNameDialogData)
    }

    // ==================== Tests for state with receipts having various payment methods ====================

    @Test
    fun `state handles receipts with different payment methods`() {
        val receipts = listOf(
            ReceiptData(paymentMethod = PaymentMethod.CASH),
            ReceiptData(paymentMethod = PaymentMethod.CREDIT_CARD),
            ReceiptData(paymentMethod = PaymentMethod.ZELLE)
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(3, uiState.purchasedHistory.size)
        assertFalse(uiState.hasReceiptData) // Empty checkout lists
    }

    // ==================== Tests for state with multiple items per receipt ====================

    @Test
    fun `totalAmount handles receipts with multiple items`() {
        val receipts = listOf(
            ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(itemName = "Item1", totalPrice = 10.0, quantity = 2),
                    CheckoutItem(itemName = "Item2", totalPrice = 15.0, quantity = 1),
                    CheckoutItem(itemName = "Item3", totalPrice = 25.0, quantity = 3)
                )
            )
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(50.0, uiState.totalAmount)
    }

    // ==================== Tests for state immutability ====================

    @Test
    fun `state copy creates new instance with updated values`() {
        val originalState = PurchaseHistoryUiState()
        val newState = originalState.copy(isLoading = false)

        assertTrue(originalState.isLoading)
        assertFalse(newState.isLoading)
    }

    @Test
    fun `state copy preserves other values`() {
        val receipts = listOf(ReceiptData())
        val originalState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            isLoading = false,
            showRemoveDialog = true
        )
        val newState = originalState.copy(showEditBottomSheet = true)

        assertEquals(receipts, newState.purchasedHistory)
        assertFalse(newState.isLoading)
        assertTrue(newState.showRemoveDialog)
        assertTrue(newState.showEditBottomSheet)
    }

    // ==================== Tests for collapsed months functionality ====================

    @Test
    fun `all months are expanded by default`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 11, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 10, 15, 10, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertTrue(uiState.groupedByMonth.all { it.isExpanded })
    }

    @Test
    fun `collapsed months are marked as not expanded`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 11, 15, 10, 0))
        )
        val decemberKey = YearMonth(2024, Month.DECEMBER).key
        val uiState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            collapsedMonths = setOf(decemberKey)
        )

        val decemberGroup = uiState.groupedByMonth.find { it.yearMonth.month == Month.DECEMBER }
        val novemberGroup = uiState.groupedByMonth.find { it.yearMonth.month == Month.NOVEMBER }

        assertNotNull(decemberGroup)
        assertNotNull(novemberGroup)
        assertFalse(decemberGroup.isExpanded)
        assertTrue(novemberGroup.isExpanded)
    }

    @Test
    fun `multiple months can be collapsed`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 11, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 10, 15, 10, 0))
        )
        val decemberKey = YearMonth(2024, Month.DECEMBER).key
        val octoberKey = YearMonth(2024, Month.OCTOBER).key
        val uiState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            collapsedMonths = setOf(decemberKey, octoberKey)
        )

        val groups = uiState.groupedByMonth
        assertFalse(groups.find { it.yearMonth.month == Month.DECEMBER }!!.isExpanded)
        assertTrue(groups.find { it.yearMonth.month == Month.NOVEMBER }!!.isExpanded)
        assertFalse(groups.find { it.yearMonth.month == Month.OCTOBER }!!.isExpanded)
    }

    // ==================== Tests for custom month names ====================

    @Test
    fun `custom month names are applied to month groups`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0))
        )
        val decemberKey = YearMonth(2024, Month.DECEMBER).key
        val uiState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            customMonthNames = mapOf(decemberKey to "Holiday Sales 2024")
        )

        assertEquals("Holiday Sales 2024", uiState.groupedByMonth[0].displayName)
    }

    @Test
    fun `months without custom names use default format`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 15, 10, 0)),
            ReceiptData(dateTime = LocalDateTime(2024, 11, 15, 10, 0))
        )
        val decemberKey = YearMonth(2024, Month.DECEMBER).key
        val uiState = PurchaseHistoryUiState(
            purchasedHistory = receipts,
            customMonthNames = mapOf(decemberKey to "Custom Name")
        )

        assertEquals("Custom Name", uiState.groupedByMonth[0].displayName)
        assertEquals("November 2024", uiState.groupedByMonth[1].displayName)
    }

    // ==================== Tests for edge cases ====================

    @Test
    fun `handles leap year dates`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 2, 29, 10, 0)) // 2024 is a leap year
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(1, uiState.groupedByMonth.size)
        assertEquals(Month.FEBRUARY, uiState.groupedByMonth[0].yearMonth.month)
    }

    @Test
    fun `handles year boundary`() {
        val receipts = listOf(
            ReceiptData(dateTime = LocalDateTime(2024, 12, 31, 23, 59)),
            ReceiptData(dateTime = LocalDateTime(2025, 1, 1, 0, 0))
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(2, uiState.groupedByMonth.size)
        assertEquals(2025, uiState.groupedByMonth[0].yearMonth.year)
        assertEquals(2024, uiState.groupedByMonth[1].yearMonth.year)
    }

    @Test
    fun `handles single receipt`() {
        val receipts = listOf(
            ReceiptData(
                dateTime = LocalDateTime(2024, 12, 25, 10, 0),
                checkoutList = listOf(CheckoutItem(totalPrice = 100.0))
            )
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(1, uiState.groupedByMonth.size)
        assertEquals(1, uiState.groupedReceipts.size)
        assertEquals(100.0, uiState.totalAmount)
    }

    @Test
    fun `handles receipts with same timestamp`() {
        val sameTime = LocalDateTime(2024, 12, 25, 10, 0, 0)
        val receipts = listOf(
            ReceiptData(dateTime = sameTime),
            ReceiptData(dateTime = sameTime),
            ReceiptData(dateTime = sameTime)
        )
        val uiState = PurchaseHistoryUiState(purchasedHistory = receipts)

        assertEquals(1, uiState.groupedReceipts.size)
        assertEquals(3, uiState.groupedReceipts[0].second.size)
    }
}
