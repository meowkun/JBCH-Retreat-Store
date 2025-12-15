package com.example.jbchretreatstore.bookstore.domain.model

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ReceiptDataTest {

    @Test
    fun `default constructor creates receipt with default values`() {
        val receipt = ReceiptData()

        assertEquals("", receipt.buyerName)
        assertTrue(receipt.checkoutList.isEmpty())
        assertEquals(PaymentMethod.CASH, receipt.paymentMethod)
        assertEquals(CheckoutStatus.PENDING, receipt.checkoutStatus)
    }

    @Test
    fun `constructor with parameters creates receipt correctly`() {
        val checkoutList = listOf(
            CheckoutItem(itemName = "Book", quantity = 2, totalPrice = 40.0)
        )
        val dateTime = LocalDateTime(2024, 12, 25, 10, 30, 0, 0)

        val receipt = ReceiptData(
            buyerName = "John Doe",
            checkoutList = checkoutList,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            dateTime = dateTime
        )

        assertEquals("John Doe", receipt.buyerName)
        assertEquals(1, receipt.checkoutList.size)
        assertEquals(PaymentMethod.CREDIT_CARD, receipt.paymentMethod)
        assertEquals(CheckoutStatus.CHECKED_OUT, receipt.checkoutStatus)
        assertEquals(dateTime, receipt.dateTime)
    }

    @Test
    fun `each receipt has unique id by default`() {
        val receipt1 = ReceiptData()
        val receipt2 = ReceiptData()

        assertNotEquals(receipt1.id, receipt2.id)
    }

    @Test
    fun `receipt can have custom id`() {
        val customId = Uuid.random()
        val receipt = ReceiptData(id = customId)

        assertEquals(customId, receipt.id)
    }

    @Test
    fun `receipt with multiple checkout items`() {
        val checkoutList = listOf(
            CheckoutItem(itemName = "Book 1", quantity = 1, totalPrice = 20.0),
            CheckoutItem(itemName = "Book 2", quantity = 3, totalPrice = 45.0),
            CheckoutItem(itemName = "Book 3", quantity = 2, totalPrice = 30.0)
        )
        val receipt = ReceiptData(checkoutList = checkoutList)

        assertEquals(3, receipt.checkoutList.size)
        assertEquals(95.0, receipt.checkoutList.sumOf { it.totalPrice })
    }

    @Test
    fun `all payment methods are supported`() {
        PaymentMethod.entries.forEach { method ->
            val receipt = ReceiptData(paymentMethod = method)
            assertEquals(method, receipt.paymentMethod)
        }
    }

    @Test
    fun `all checkout statuses are supported`() {
        CheckoutStatus.entries.forEach { status ->
            val receipt = ReceiptData(checkoutStatus = status)
            assertEquals(status, receipt.checkoutStatus)
        }
    }

    @Test
    fun `copy preserves id when not specified`() {
        val original = ReceiptData(buyerName = "Original")
        val copied = original.copy(buyerName = "Copied")

        assertEquals(original.id, copied.id)
        assertEquals("Copied", copied.buyerName)
    }

    @Test
    fun `buyer name with special characters`() {
        val receipt = ReceiptData(buyerName = "José García-López")

        assertEquals("José García-López", receipt.buyerName)
    }

    @Test
    fun `buyer name with unicode characters`() {
        val receipt = ReceiptData(buyerName = "张三 李四")

        assertEquals("张三 李四", receipt.buyerName)
    }

    @Test
    fun `empty buyer name is allowed`() {
        val receipt = ReceiptData(buyerName = "")

        assertEquals("", receipt.buyerName)
    }

    @Test
    fun `whitespace only buyer name is allowed but represents edge case`() {
        val receipt = ReceiptData(buyerName = "   ")

        assertEquals("   ", receipt.buyerName)
    }

    @Test
    fun `dateTime is set automatically when not specified`() {
        val receipt = ReceiptData()

        // Just verify it's not null and has reasonable values
        assertTrue(receipt.dateTime.year >= 2020)
    }

    @Test
    fun `dateTime with specific time`() {
        val dateTime = LocalDateTime(2024, 1, 15, 14, 30, 45, 0)
        val receipt = ReceiptData(dateTime = dateTime)

        assertEquals(2024, receipt.dateTime.year)
        assertEquals(1, receipt.dateTime.monthNumber)
        assertEquals(15, receipt.dateTime.dayOfMonth)
        assertEquals(14, receipt.dateTime.hour)
        assertEquals(30, receipt.dateTime.minute)
        assertEquals(45, receipt.dateTime.second)
    }

    @Test
    fun `receipts with same data are equal`() {
        val id = Uuid.random()
        val dateTime = LocalDateTime(2024, 1, 1, 0, 0, 0, 0)

        val receipt1 = ReceiptData(
            id = id,
            buyerName = "Test",
            checkoutList = emptyList(),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.PENDING,
            dateTime = dateTime
        )
        val receipt2 = ReceiptData(
            id = id,
            buyerName = "Test",
            checkoutList = emptyList(),
            paymentMethod = PaymentMethod.CASH,
            checkoutStatus = CheckoutStatus.PENDING,
            dateTime = dateTime
        )

        assertEquals(receipt1, receipt2)
    }

    @Test
    fun `checkout list maintains order`() {
        val checkoutList = listOf(
            CheckoutItem(itemName = "First", quantity = 1, totalPrice = 10.0),
            CheckoutItem(itemName = "Second", quantity = 1, totalPrice = 20.0),
            CheckoutItem(itemName = "Third", quantity = 1, totalPrice = 30.0)
        )
        val receipt = ReceiptData(checkoutList = checkoutList)

        assertEquals("First", receipt.checkoutList[0].itemName)
        assertEquals("Second", receipt.checkoutList[1].itemName)
        assertEquals("Third", receipt.checkoutList[2].itemName)
    }

    @Test
    fun `receipt with very long buyer name`() {
        val longName = "A".repeat(1000)
        val receipt = ReceiptData(buyerName = longName)

        assertEquals(1000, receipt.buyerName.length)
    }

    @Test
    fun `receipt with many items`() {
        val checkoutList = (1..100).map { i ->
            CheckoutItem(itemName = "Item $i", quantity = i, totalPrice = i * 10.0)
        }
        val receipt = ReceiptData(checkoutList = checkoutList)

        assertEquals(100, receipt.checkoutList.size)
    }
}

