package com.example.jbchretreatstore

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

/**
 * High-level integration tests for the ComposeApp module.
 * Tests the basic functionality and data model integrity.
 */
@OptIn(ExperimentalUuidApi::class)
class ComposeAppCommonTest {

    @Test
    fun `basic arithmetic sanity check`() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun `domain models can be instantiated with defaults`() {
        val checkoutItem = CheckoutItem()
        val displayItem = DisplayItem()
        val receiptData = ReceiptData()

        assertEquals("", checkoutItem.itemName)
        assertEquals("", displayItem.name)
        assertEquals("Unknown", receiptData.buyerName)
    }

    @Test
    fun `payment methods have expected values`() {
        assertEquals(6, PaymentMethod.entries.size)
        assertTrue(PaymentMethod.entries.any { it.methodName == "Cash" })
        assertTrue(PaymentMethod.entries.any { it.methodName == "Credit Card" })
    }

    @Test
    fun `checkout statuses have expected values`() {
        assertEquals(3, CheckoutStatus.entries.size)
        assertTrue(CheckoutStatus.entries.contains(CheckoutStatus.CHECKED_OUT))
        assertTrue(CheckoutStatus.entries.contains(CheckoutStatus.SAVE_FOR_LATER))
        assertTrue(CheckoutStatus.entries.contains(CheckoutStatus.PENDING))
    }

    @Test
    fun `uuid generation creates unique ids`() {
        val item1 = CheckoutItem()
        val item2 = CheckoutItem()

        assertNotEquals(item1.id, item2.id)
    }

    @Test
    fun `receipt data can contain checkout items`() {
        val item = CheckoutItem(itemName = "Test", quantity = 2, totalPrice = 40.0)
        val receipt = ReceiptData(checkoutList = listOf(item))

        assertEquals(1, receipt.checkoutList.size)
        assertEquals("Test", receipt.checkoutList.first().itemName)
    }

    @Test
    fun `display item can have variants`() {
        val variants = listOf(
            DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))
        )
        val item = DisplayItem(name = "T-Shirt", price = 25.0, variants = variants)

        assertEquals(1, item.variants.size)
        assertEquals("Size", item.variants.first().key)
        assertEquals(3, item.variants.first().valueList.size)
    }

    @Test
    fun `checkout item variants map is derived correctly`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M"),
            CheckoutItem.Variant(key = "Color", valueList = listOf("Blue"), selectedValue = "Blue")
        )
        val item = CheckoutItem(variants = variants)

        assertEquals(mapOf("Size" to "M", "Color" to "Blue"), item.variantsMap)
    }
}