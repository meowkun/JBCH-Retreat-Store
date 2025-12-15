package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DisplayItemTest {

    @Test
    fun `default constructor creates item with empty values`() {
        val item = DisplayItem()

        assertEquals("", item.name)
        assertEquals(0.0, item.price)
        assertTrue(item.variants.isEmpty())
        assertFalse(item.isInCart)
    }

    @Test
    fun `constructor with parameters creates item correctly`() {
        val variants = listOf(
            DisplayItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L")
            )
        )
        val item = DisplayItem(
            name = "Book",
            price = 19.99,
            variants = variants,
            isInCart = true
        )

        assertEquals("Book", item.name)
        assertEquals(19.99, item.price)
        assertEquals(1, item.variants.size)
        assertTrue(item.isInCart)
    }

    @Test
    fun `each item has unique id by default`() {
        val item1 = DisplayItem()
        val item2 = DisplayItem()

        assertNotEquals(item1.id, item2.id)
    }

    @Test
    fun `item can have custom id`() {
        val customId = Uuid.random()
        val item = DisplayItem(id = customId)

        assertEquals(customId, item.id)
    }

    @Test
    fun `variant has unique id by default`() {
        val variant1 = DisplayItem.Variant()
        val variant2 = DisplayItem.Variant()

        assertNotEquals(variant1.id, variant2.id)
    }

    @Test
    fun `variant with empty values is valid`() {
        val variant = DisplayItem.Variant()

        assertEquals("", variant.key)
        assertTrue(variant.valueList.isEmpty())
    }

    @Test
    fun `variant with multiple values`() {
        val variant = DisplayItem.Variant(
            key = "Color",
            valueList = listOf("Red", "Green", "Blue", "Yellow")
        )

        assertEquals("Color", variant.key)
        assertEquals(4, variant.valueList.size)
        assertTrue(variant.valueList.contains("Red"))
        assertTrue(variant.valueList.contains("Yellow"))
    }

    @Test
    fun `item with multiple variants`() {
        val variants = listOf(
            DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L")),
            DisplayItem.Variant(key = "Color", valueList = listOf("Red", "Blue")),
            DisplayItem.Variant(key = "Material", valueList = listOf("Cotton", "Polyester"))
        )
        val item = DisplayItem(variants = variants)

        assertEquals(3, item.variants.size)
    }

    @Test
    fun `copy preserves id when not specified`() {
        val original = DisplayItem(name = "Original")
        val copied = original.copy(name = "Copied")

        assertEquals(original.id, copied.id)
        assertEquals("Copied", copied.name)
    }

    @Test
    fun `isInCart can be toggled`() {
        val item = DisplayItem(isInCart = false)
        val addedToCart = item.copy(isInCart = true)

        assertFalse(item.isInCart)
        assertTrue(addedToCart.isInCart)
    }

    @Test
    fun `zero price is allowed`() {
        val item = DisplayItem(price = 0.0)

        assertEquals(0.0, item.price)
    }

    @Test
    fun `negative price is allowed but represents edge case`() {
        val item = DisplayItem(price = -5.0)

        assertEquals(-5.0, item.price)
    }

    @Test
    fun `very large price is handled`() {
        val item = DisplayItem(price = 999999.99)

        assertEquals(999999.99, item.price)
    }

    @Test
    fun `item with special characters in name`() {
        val item = DisplayItem(name = "Test Item: 50% off! @Store #1")

        assertEquals("Test Item: 50% off! @Store #1", item.name)
    }

    @Test
    fun `item with unicode characters in name`() {
        val item = DisplayItem(name = "经书 - Scripture 📖")

        assertEquals("经书 - Scripture 📖", item.name)
    }

    @Test
    fun `variant with duplicate values in list`() {
        val variant = DisplayItem.Variant(
            key = "Size",
            valueList = listOf("M", "M", "L")
        )

        assertEquals(3, variant.valueList.size)
        assertEquals(2, variant.valueList.count { it == "M" })
    }

    @Test
    fun `items with same data are equal`() {
        val id = Uuid.random()
        val variantId = Uuid.random()
        val variants =
            listOf(DisplayItem.Variant(id = variantId, key = "Size", valueList = listOf("M")))

        val item1 =
            DisplayItem(id = id, name = "Test", price = 10.0, variants = variants, isInCart = false)
        val item2 =
            DisplayItem(id = id, name = "Test", price = 10.0, variants = variants, isInCart = false)

        assertEquals(item1, item2)
    }

    @Test
    fun `variants maintain order`() {
        val variants = listOf(
            DisplayItem.Variant(key = "First", valueList = emptyList()),
            DisplayItem.Variant(key = "Second", valueList = emptyList()),
            DisplayItem.Variant(key = "Third", valueList = emptyList())
        )
        val item = DisplayItem(variants = variants)

        assertEquals("First", item.variants[0].key)
        assertEquals("Second", item.variants[1].key)
        assertEquals("Third", item.variants[2].key)
    }

    @Test
    fun `precision handling for price with many decimals`() {
        val item = DisplayItem(price = 19.999999)

        // Double precision should maintain the value
        assertEquals(19.999999, item.price, 0.0000001)
    }

    @Test
    fun `variant valueList preserves order`() {
        val variant = DisplayItem.Variant(
            key = "Size",
            valueList = listOf("XS", "S", "M", "L", "XL", "XXL")
        )

        assertEquals("XS", variant.valueList[0])
        assertEquals("XXL", variant.valueList[5])
    }

    @Test
    fun `empty name is allowed`() {
        val item = DisplayItem(name = "")

        assertEquals("", item.name)
    }

    @Test
    fun `whitespace only name is allowed but represents edge case`() {
        val item = DisplayItem(name = "   ")

        assertEquals("   ", item.name)
    }
}

