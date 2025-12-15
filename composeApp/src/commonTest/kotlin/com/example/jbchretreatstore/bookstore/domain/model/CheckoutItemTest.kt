package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutItemTest {

    @Test
    fun `default constructor creates item with empty values`() {
        val item = CheckoutItem()

        assertEquals("", item.itemName)
        assertEquals(1, item.quantity)
        assertEquals(0.0, item.totalPrice)
        assertTrue(item.variants.isEmpty())
    }

    @Test
    fun `constructor with parameters creates item correctly`() {
        val variants = listOf(
            CheckoutItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "M"
            )
        )
        val item = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 50.0
        )

        assertEquals("T-Shirt", item.itemName)
        assertEquals(2, item.quantity)
        assertEquals(50.0, item.totalPrice)
        assertEquals(1, item.variants.size)
        assertEquals("Size", item.variants[0].key)
        assertEquals("M", item.variants[0].selectedValue)
    }

    @Test
    fun `variantsMap returns correct map from variants`() {
        val variants = listOf(
            CheckoutItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "L"
            ),
            CheckoutItem.Variant(
                key = "Color",
                valueList = listOf("Red", "Blue"),
                selectedValue = "Blue"
            )
        )
        val item = CheckoutItem(variants = variants)

        val expectedMap = mapOf("Size" to "L", "Color" to "Blue")
        assertEquals(expectedMap, item.variantsMap)
    }

    @Test
    fun `variantsMap returns empty map when no variants`() {
        val item = CheckoutItem()

        assertTrue(item.variantsMap.isEmpty())
    }

    @Test
    fun `each item has unique id by default`() {
        val item1 = CheckoutItem()
        val item2 = CheckoutItem()

        assertNotEquals(item1.id, item2.id)
    }

    @Test
    fun `item can have custom id`() {
        val customId = Uuid.random()
        val item = CheckoutItem(id = customId)

        assertEquals(customId, item.id)
    }

    @Test
    fun `copy preserves id when not specified`() {
        val original = CheckoutItem(itemName = "Original")
        val copied = original.copy(itemName = "Copied")

        assertEquals(original.id, copied.id)
        assertEquals("Copied", copied.itemName)
    }

    @Test
    fun `variant with empty values is valid`() {
        val variant = CheckoutItem.Variant()

        assertEquals("", variant.key)
        assertTrue(variant.valueList.isEmpty())
        assertEquals("", variant.selectedValue)
    }

    @Test
    fun `variant selectedValue can be different from valueList`() {
        // This tests edge case where selectedValue might not be in valueList
        // (e.g., custom input or legacy data)
        val variant = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("S", "M"),
            selectedValue = "XL"
        )

        assertEquals("XL", variant.selectedValue)
        assertTrue("XL" !in variant.valueList)
    }

    @Test
    fun `items with same data are equal`() {
        val id = Uuid.random()
        val variants =
            listOf(CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M"))

        val item1 = CheckoutItem(
            id = id,
            itemName = "Test",
            quantity = 1,
            variants = variants,
            totalPrice = 10.0
        )
        val item2 = CheckoutItem(
            id = id,
            itemName = "Test",
            quantity = 1,
            variants = variants,
            totalPrice = 10.0
        )

        assertEquals(item1, item2)
    }

    @Test
    fun `zero quantity is allowed but represents edge case`() {
        val item = CheckoutItem(quantity = 0)

        assertEquals(0, item.quantity)
    }

    @Test
    fun `negative quantity is allowed but represents edge case`() {
        val item = CheckoutItem(quantity = -1)

        assertEquals(-1, item.quantity)
    }

    @Test
    fun `negative price is allowed but represents edge case`() {
        val item = CheckoutItem(totalPrice = -10.0)

        assertEquals(-10.0, item.totalPrice)
    }

    @Test
    fun `very large quantity is handled`() {
        val item = CheckoutItem(quantity = Int.MAX_VALUE)

        assertEquals(Int.MAX_VALUE, item.quantity)
    }

    @Test
    fun `very large price is handled`() {
        val item = CheckoutItem(totalPrice = Double.MAX_VALUE)

        assertEquals(Double.MAX_VALUE, item.totalPrice)
    }

    @Test
    fun `item with special characters in name`() {
        val item = CheckoutItem(itemName = "Test Item with special chars: @#$%^&*()")

        assertEquals("Test Item with special chars: @#\$%^&*()", item.itemName)
    }

    @Test
    fun `item with unicode characters in name`() {
        val item = CheckoutItem(itemName = "测试项目 📚")

        assertEquals("测试项目 📚", item.itemName)
    }

    @Test
    fun `multiple variants maintain order`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "First", valueList = emptyList(), selectedValue = "1"),
            CheckoutItem.Variant(key = "Second", valueList = emptyList(), selectedValue = "2"),
            CheckoutItem.Variant(key = "Third", valueList = emptyList(), selectedValue = "3")
        )
        val item = CheckoutItem(variants = variants)

        assertEquals("First", item.variants[0].key)
        assertEquals("Second", item.variants[1].key)
        assertEquals("Third", item.variants[2].key)
    }
}

