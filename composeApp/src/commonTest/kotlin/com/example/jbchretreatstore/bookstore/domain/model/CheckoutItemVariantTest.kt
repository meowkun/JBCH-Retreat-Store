package com.example.jbchretreatstore.bookstore.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

/**
 * Comprehensive tests for CheckoutItem.Variant class
 */
@OptIn(ExperimentalUuidApi::class)
class CheckoutItemVariantTest {

    // ============= CONSTRUCTOR TESTS =============

    @Test
    fun `default constructor creates empty variant`() {
        val variant = CheckoutItem.Variant()

        assertEquals("", variant.key)
        assertTrue(variant.valueList.isEmpty())
        assertEquals("", variant.selectedValue)
    }

    @Test
    fun `constructor with all parameters`() {
        val variant = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("S", "M", "L"),
            selectedValue = "M"
        )

        assertEquals("Size", variant.key)
        assertEquals(listOf("S", "M", "L"), variant.valueList)
        assertEquals("M", variant.selectedValue)
    }

    // ============= VALUE LIST TESTS =============

    @Test
    fun `valueList with single value`() {
        val variant = CheckoutItem.Variant(
            key = "Color",
            valueList = listOf("Red"),
            selectedValue = "Red"
        )

        assertEquals(1, variant.valueList.size)
        assertEquals("Red", variant.valueList[0])
    }

    @Test
    fun `valueList with many values`() {
        val values = (1..100).map { "Value$it" }
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = values,
            selectedValue = "Value1"
        )

        assertEquals(100, variant.valueList.size)
    }

    @Test
    fun `valueList preserves order`() {
        val variant = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL"),
            selectedValue = "M"
        )

        assertEquals("XXS", variant.valueList[0])
        assertEquals("XXXL", variant.valueList[7])
    }

    @Test
    fun `valueList with duplicate values`() {
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = listOf("A", "A", "B", "B", "C"),
            selectedValue = "A"
        )

        assertEquals(5, variant.valueList.size)
        assertEquals(2, variant.valueList.count { it == "A" })
        assertEquals(2, variant.valueList.count { it == "B" })
    }

    @Test
    fun `valueList with empty strings`() {
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = listOf("", "Valid", ""),
            selectedValue = ""
        )

        assertEquals(3, variant.valueList.size)
        assertEquals(2, variant.valueList.count { it.isEmpty() })
    }

    // ============= SELECTED VALUE TESTS =============

    @Test
    fun `selectedValue from valueList`() {
        val variant = CheckoutItem.Variant(
            key = "Color",
            valueList = listOf("Red", "Blue", "Green"),
            selectedValue = "Blue"
        )

        assertTrue(variant.selectedValue in variant.valueList)
    }

    @Test
    fun `selectedValue not in valueList is allowed`() {
        val variant = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("S", "M", "L"),
            selectedValue = "Custom Size"
        )

        assertEquals("Custom Size", variant.selectedValue)
        assertFalse(variant.selectedValue in variant.valueList)
    }

    @Test
    fun `empty selectedValue`() {
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = listOf("A", "B"),
            selectedValue = ""
        )

        assertEquals("", variant.selectedValue)
    }

    @Test
    fun `selectedValue with special characters`() {
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = listOf("Option: A", "Option: B"),
            selectedValue = "Option: A"
        )

        assertEquals("Option: A", variant.selectedValue)
    }

    @Test
    fun `selectedValue with unicode`() {
        val variant = CheckoutItem.Variant(
            key = "尺寸",
            valueList = listOf("小", "中", "大"),
            selectedValue = "中"
        )

        assertEquals("中", variant.selectedValue)
    }

    // ============= KEY TESTS =============

    @Test
    fun `key with special characters`() {
        val variant = CheckoutItem.Variant(
            key = "Size (US/UK)",
            valueList = listOf("S/M", "L/XL"),
            selectedValue = "S/M"
        )

        assertEquals("Size (US/UK)", variant.key)
    }

    @Test
    fun `key with unicode characters`() {
        val variant = CheckoutItem.Variant(
            key = "颜色 (Color)",
            valueList = listOf("红", "蓝"),
            selectedValue = "红"
        )

        assertEquals("颜色 (Color)", variant.key)
    }

    @Test
    fun `empty key`() {
        val variant = CheckoutItem.Variant(
            key = "",
            valueList = listOf("A", "B"),
            selectedValue = "A"
        )

        assertEquals("", variant.key)
    }

    @Test
    fun `key with only whitespace`() {
        val variant = CheckoutItem.Variant(
            key = "   ",
            valueList = listOf("A"),
            selectedValue = "A"
        )

        assertEquals("   ", variant.key)
    }

    @Test
    fun `very long key`() {
        val longKey = "K".repeat(1000)
        val variant = CheckoutItem.Variant(
            key = longKey,
            valueList = listOf("A"),
            selectedValue = "A"
        )

        assertEquals(1000, variant.key.length)
    }

    // ============= COPY TESTS =============

    @Test
    fun `copy changes selected value`() {
        val original = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("S", "M", "L"),
            selectedValue = "M"
        )
        val copied = original.copy(selectedValue = "L")

        assertEquals("M", original.selectedValue)
        assertEquals("L", copied.selectedValue)
    }

    @Test
    fun `copy changes key`() {
        val original = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M"),
            selectedValue = "M"
        )
        val copied = original.copy(key = "Dimension")

        assertEquals("Dimension", copied.key)
    }

    @Test
    fun `copy changes valueList`() {
        val original = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("S", "M"),
            selectedValue = "M"
        )
        val copied = original.copy(valueList = listOf("M", "L", "XL"))

        assertEquals(listOf("S", "M"), original.valueList)
        assertEquals(listOf("M", "L", "XL"), copied.valueList)
    }

    // ============= EQUALITY TESTS =============

    @Test
    fun `variants with same data are equal`() {
        val variant1 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M"),
            selectedValue = "M"
        )

        assertEquals(variant1, variant2)
    }

    @Test
    fun `variants with different selectedValue are not equal`() {
        val variant1 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "L"
        )

        assertNotEquals(variant1, variant2)
    }

    @Test
    fun `variants with different key are not equal`() {
        val variant1 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItem.Variant(
            key = "Color",
            valueList = listOf("M"),
            selectedValue = "M"
        )

        assertNotEquals(variant1, variant2)
    }

    @Test
    fun `variants with different valueList are not equal`() {
        val variant1 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("S", "M"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "M"
        )

        assertNotEquals(variant1, variant2)
    }

    // ============= HASH CODE TESTS =============

    @Test
    fun `equal variants have same hash code`() {
        val variant1 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItem.Variant(
            key = "Size",
            valueList = listOf("M"),
            selectedValue = "M"
        )

        assertEquals(variant1.hashCode(), variant2.hashCode())
    }

    // ============= INTEGRATION WITH CHECKOUT ITEM =============

    @Test
    fun `checkoutItem with multiple variants`() {
        val variants = listOf(
            CheckoutItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "M"
            ),
            CheckoutItem.Variant(
                key = "Color",
                valueList = listOf("Red", "Blue"),
                selectedValue = "Blue"
            ),
            CheckoutItem.Variant(
                key = "Material",
                valueList = listOf("Cotton", "Polyester"),
                selectedValue = "Cotton"
            )
        )
        val item = CheckoutItem(
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 50.0
        )

        assertEquals(3, item.variants.size)
        assertEquals("M", item.variantsMap["Size"])
        assertEquals("Blue", item.variantsMap["Color"])
        assertEquals("Cotton", item.variantsMap["Material"])
    }

    @Test
    fun `checkoutItem variantsMap reflects variant selections`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("S", "M"), selectedValue = "S"),
            CheckoutItem.Variant(key = "Color", valueList = listOf("Red"), selectedValue = "Red")
        )
        val item = CheckoutItem(variants = variants)

        val expectedMap = mapOf("Size" to "S", "Color" to "Red")
        assertEquals(expectedMap, item.variantsMap)
    }

    @Test
    fun `checkoutItem with empty variants list`() {
        val item = CheckoutItem(
            itemName = "Simple Item",
            quantity = 1,
            variants = emptyList(),
            totalPrice = 10.0
        )

        assertTrue(item.variants.isEmpty())
        assertTrue(item.variantsMap.isEmpty())
    }

    @Test
    fun `variants with same key different selected values`() {
        // Edge case: Two variants with the same key
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("S"), selectedValue = "S"),
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )
        val item = CheckoutItem(variants = variants)

        // The variantsMap uses key as map key, so second variant overwrites first
        assertEquals(2, item.variants.size)
        assertEquals("M", item.variantsMap["Size"]) // Last one wins in map
    }

    // ============= EDGE CASES =============

    @Test
    fun `variant with empty valueList`() {
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = emptyList(),
            selectedValue = "Selected"
        )

        assertTrue(variant.valueList.isEmpty())
        assertEquals("Selected", variant.selectedValue)
    }

    @Test
    fun `variant with very long valueList`() {
        val values = (1..1000).map { "Value$it" }
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = values,
            selectedValue = "Value500"
        )

        assertEquals(1000, variant.valueList.size)
        assertEquals("Value500", variant.selectedValue)
    }

    @Test
    fun `variant with very long selected value`() {
        val longValue = "V".repeat(1000)
        val variant = CheckoutItem.Variant(
            key = "Option",
            valueList = listOf(longValue),
            selectedValue = longValue
        )

        assertEquals(1000, variant.selectedValue.length)
    }

    @Test
    fun `variant with newlines in values`() {
        val variant = CheckoutItem.Variant(
            key = "Description",
            valueList = listOf("Line1\nLine2", "SingleLine"),
            selectedValue = "Line1\nLine2"
        )

        assertTrue(variant.selectedValue.contains("\n"))
    }

    @Test
    fun `variant with tabs in values`() {
        val variant = CheckoutItem.Variant(
            key = "Format",
            valueList = listOf("Tab\tSeparated", "Normal"),
            selectedValue = "Tab\tSeparated"
        )

        assertTrue(variant.selectedValue.contains("\t"))
    }
}
