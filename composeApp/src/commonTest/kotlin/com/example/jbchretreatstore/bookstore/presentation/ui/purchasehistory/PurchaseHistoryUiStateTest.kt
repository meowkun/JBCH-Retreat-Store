package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PurchaseHistoryUiStateTest {

    // Tests for totalAmount derived property

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

    // Tests for hasReceiptData derived property

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

    // Tests for groupedReceipts derived property

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

    // Tests for default state

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

        assertFalse(uiState.showRemoveBottomSheet)
        assertFalse(uiState.showEditBottomSheet)
        assertFalse(uiState.showEditBuyerNameDialog)
    }

    // Tests for extension properties

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

    // Tests for editBottomSheetData derived property

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

    // Tests for editBuyerNameDialogData derived property

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
}

