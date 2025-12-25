package com.example.jbchretreatstore.bookstore.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutItemDtoTest {

    // ============= DEFAULT CONSTRUCTOR TESTS =============

    @Test
    fun `default constructor creates dto with empty values`() {
        val dto = CheckoutItemDto()

        assertEquals("", dto.itemName)
        assertEquals(1, dto.quantity)
        assertTrue(dto.optionsMap.isEmpty())
        assertTrue(dto.variants.isEmpty())
        assertEquals(0.0, dto.totalPrice)
    }

    @Test
    fun `each dto has unique id by default`() {
        val dto1 = CheckoutItemDto()
        val dto2 = CheckoutItemDto()

        assertNotEquals(dto1.id, dto2.id)
    }

    @Test
    fun `dto can have custom id`() {
        val customId = Uuid.random()
        val dto = CheckoutItemDto(id = customId)

        assertEquals(customId, dto.id)
    }

    // ============= PARAMETER CONSTRUCTOR TESTS =============

    @Test
    fun `constructor with parameters creates dto correctly`() {
        val variants = listOf(
            CheckoutItemDto.VariantDto(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "M"
            )
        )
        val dto = CheckoutItemDto(
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 50.0
        )

        assertEquals("T-Shirt", dto.itemName)
        assertEquals(2, dto.quantity)
        assertEquals(1, dto.variants.size)
        assertEquals(50.0, dto.totalPrice)
    }

    @Test
    fun `constructor with optionsMap for backward compatibility`() {
        val optionsMap = mapOf("Size" to "M", "Color" to "Blue")
        val dto = CheckoutItemDto(
            itemName = "T-Shirt",
            quantity = 1,
            optionsMap = optionsMap,
            totalPrice = 25.0
        )

        assertEquals("M", dto.optionsMap["Size"])
        assertEquals("Blue", dto.optionsMap["Color"])
    }

    // ============= VARIANT DTO TESTS =============

    @Test
    fun `VariantDto default constructor creates empty values`() {
        val variant = CheckoutItemDto.VariantDto()

        assertEquals("", variant.key)
        assertTrue(variant.valueList.isEmpty())
        assertEquals("", variant.selectedValue)
    }

    @Test
    fun `VariantDto with parameters creates correctly`() {
        val variant = CheckoutItemDto.VariantDto(
            key = "Color",
            valueList = listOf("Red", "Blue", "Green"),
            selectedValue = "Blue"
        )

        assertEquals("Color", variant.key)
        assertEquals(3, variant.valueList.size)
        assertEquals("Blue", variant.selectedValue)
    }

    @Test
    fun `VariantDto selectedValue can be different from valueList`() {
        val variant = CheckoutItemDto.VariantDto(
            key = "Size",
            valueList = listOf("S", "M"),
            selectedValue = "Custom"
        )

        assertEquals("Custom", variant.selectedValue)
        assertTrue(variant.selectedValue !in variant.valueList)
    }

    @Test
    fun `VariantDto value list preserves order`() {
        val variant = CheckoutItemDto.VariantDto(
            key = "Size",
            valueList = listOf("XS", "S", "M", "L", "XL")
        )

        assertEquals("XS", variant.valueList[0])
        assertEquals("XL", variant.valueList[4])
    }

    // ============= COPY TESTS =============

    @Test
    fun `copy preserves id when not specified`() {
        val original = CheckoutItemDto(itemName = "Original")
        val copied = original.copy(itemName = "Copied")

        assertEquals(original.id, copied.id)
        assertEquals("Copied", copied.itemName)
    }

    @Test
    fun `copy can change all fields`() {
        val original = CheckoutItemDto(
            itemName = "Original",
            quantity = 1,
            totalPrice = 10.0
        )
        val copied = original.copy(
            itemName = "Copied",
            quantity = 5,
            totalPrice = 50.0
        )

        assertEquals("Copied", copied.itemName)
        assertEquals(5, copied.quantity)
        assertEquals(50.0, copied.totalPrice)
    }

    // ============= EDGE CASES =============

    @Test
    fun `dto with zero quantity`() {
        val dto = CheckoutItemDto(itemName = "Item", quantity = 0, totalPrice = 10.0)

        assertEquals(0, dto.quantity)
    }

    @Test
    fun `dto with negative quantity edge case`() {
        val dto = CheckoutItemDto(itemName = "Item", quantity = -1, totalPrice = 10.0)

        assertEquals(-1, dto.quantity)
    }

    @Test
    fun `dto with very large quantity`() {
        val dto = CheckoutItemDto(itemName = "Item", quantity = Int.MAX_VALUE, totalPrice = 10.0)

        assertEquals(Int.MAX_VALUE, dto.quantity)
    }

    @Test
    fun `dto with zero total price`() {
        val dto = CheckoutItemDto(itemName = "Free Item", quantity = 1, totalPrice = 0.0)

        assertEquals(0.0, dto.totalPrice)
    }

    @Test
    fun `dto with negative total price edge case`() {
        val dto = CheckoutItemDto(itemName = "Refund", quantity = 1, totalPrice = -10.0)

        assertEquals(-10.0, dto.totalPrice)
    }

    @Test
    fun `dto with very large total price`() {
        val dto = CheckoutItemDto(itemName = "Expensive", quantity = 1, totalPrice = 999999999.99)

        assertEquals(999999999.99, dto.totalPrice, 0.01)
    }

    @Test
    fun `dto with special characters in item name`() {
        val dto = CheckoutItemDto(
            itemName = "Book: 50% Off! @Special #1",
            quantity = 1,
            totalPrice = 10.0
        )

        assertEquals("Book: 50% Off! @Special #1", dto.itemName)
    }

    @Test
    fun `dto with unicode characters in item name`() {
        val dto = CheckoutItemDto(itemName = "经书 Scripture 📖", quantity = 1, totalPrice = 15.0)

        assertEquals("经书 Scripture 📖", dto.itemName)
    }

    @Test
    fun `dto with very long item name`() {
        val longName = "A".repeat(1000)
        val dto = CheckoutItemDto(itemName = longName, quantity = 1, totalPrice = 10.0)

        assertEquals(1000, dto.itemName.length)
    }

    @Test
    fun `dto with many variants`() {
        val variants = (1..50).map { i ->
            CheckoutItemDto.VariantDto(
                key = "Option$i",
                valueList = listOf("Value$i"),
                selectedValue = "Value$i"
            )
        }
        val dto = CheckoutItemDto(
            itemName = "Complex",
            quantity = 1,
            variants = variants,
            totalPrice = 100.0
        )

        assertEquals(50, dto.variants.size)
    }

    @Test
    fun `dto with large optionsMap`() {
        val optionsMap = (1..50).associate { "Option$it" to "Value$it" }
        val dto = CheckoutItemDto(
            itemName = "Complex",
            quantity = 1,
            optionsMap = optionsMap,
            totalPrice = 100.0
        )

        assertEquals(50, dto.optionsMap.size)
    }

    // ============= OPTIONS MAP AND VARIANTS COEXISTENCE =============

    @Test
    fun `dto can have both optionsMap and variants`() {
        val optionsMap = mapOf("Legacy" to "Value")
        val variants = listOf(
            CheckoutItemDto.VariantDto(key = "New", valueList = listOf("A"), selectedValue = "A")
        )
        val dto = CheckoutItemDto(
            itemName = "Item",
            quantity = 1,
            optionsMap = optionsMap,
            variants = variants,
            totalPrice = 10.0
        )

        assertEquals(1, dto.optionsMap.size)
        assertEquals(1, dto.variants.size)
    }

    // ============= EQUALITY TESTS =============

    @Test
    fun `dtos with same data are equal`() {
        val id = Uuid.random()
        val variants = listOf(
            CheckoutItemDto.VariantDto(key = "Size", valueList = listOf("M"), selectedValue = "M")
        )

        val dto1 = CheckoutItemDto(
            id = id,
            itemName = "Test",
            quantity = 1,
            variants = variants,
            totalPrice = 10.0
        )
        val dto2 = CheckoutItemDto(
            id = id,
            itemName = "Test",
            quantity = 1,
            variants = variants,
            totalPrice = 10.0
        )

        assertEquals(dto1, dto2)
    }

    @Test
    fun `dtos with different ids are not equal`() {
        val dto1 = CheckoutItemDto(itemName = "Test", quantity = 1, totalPrice = 10.0)
        val dto2 = CheckoutItemDto(itemName = "Test", quantity = 1, totalPrice = 10.0)

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `variant dtos with same data are equal`() {
        val variant1 = CheckoutItemDto.VariantDto(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItemDto.VariantDto(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "M"
        )

        assertEquals(variant1, variant2)
    }

    @Test
    fun `variant dtos with different selected values are not equal`() {
        val variant1 = CheckoutItemDto.VariantDto(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "M"
        )
        val variant2 = CheckoutItemDto.VariantDto(
            key = "Size",
            valueList = listOf("M", "L"),
            selectedValue = "L"
        )

        assertNotEquals(variant1, variant2)
    }

    // ============= HASH CODE TESTS =============

    @Test
    fun `equal dtos have same hash code`() {
        val id = Uuid.random()
        val dto1 = CheckoutItemDto(id = id, itemName = "Test", quantity = 1, totalPrice = 10.0)
        val dto2 = CheckoutItemDto(id = id, itemName = "Test", quantity = 1, totalPrice = 10.0)

        assertEquals(dto1.hashCode(), dto2.hashCode())
    }

    @Test
    fun `equal variants have same hash code`() {
        val variant1 =
            CheckoutItemDto.VariantDto(key = "Size", valueList = listOf("M"), selectedValue = "M")
        val variant2 =
            CheckoutItemDto.VariantDto(key = "Size", valueList = listOf("M"), selectedValue = "M")

        assertEquals(variant1.hashCode(), variant2.hashCode())
    }
}

