package com.example.jbchretreatstore.bookstore.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DisplayItemDtoTest {

    // ============= DEFAULT CONSTRUCTOR TESTS =============

    @Test
    fun `default constructor creates dto with empty values`() {
        val dto = DisplayItemDto()

        assertEquals("", dto.name)
        assertEquals(0.0, dto.price)
        assertTrue(dto.options.isEmpty())
        assertFalse(dto.isInCart)
    }

    @Test
    fun `each dto has unique id by default`() {
        val dto1 = DisplayItemDto()
        val dto2 = DisplayItemDto()

        assertNotEquals(dto1.id, dto2.id)
    }

    @Test
    fun `dto can have custom id`() {
        val customId = Uuid.random()
        val dto = DisplayItemDto(id = customId)

        assertEquals(customId, dto.id)
    }

    // ============= PARAMETER CONSTRUCTOR TESTS =============

    @Test
    fun `constructor with parameters creates dto correctly`() {
        val options = listOf(
            DisplayItemDto.OptionDto(
                optionKey = "Size",
                optionValueList = listOf("S", "M", "L")
            )
        )
        val dto = DisplayItemDto(
            name = "T-Shirt",
            price = 25.99,
            options = options,
            isInCart = true
        )

        assertEquals("T-Shirt", dto.name)
        assertEquals(25.99, dto.price)
        assertEquals(1, dto.options.size)
        assertTrue(dto.isInCart)
    }

    // ============= OPTION DTO TESTS =============

    @Test
    fun `OptionDto default constructor creates empty values`() {
        val option = DisplayItemDto.OptionDto()

        assertEquals("", option.optionKey)
        assertTrue(option.optionValueList.isEmpty())
    }

    @Test
    fun `OptionDto with parameters creates correctly`() {
        val option = DisplayItemDto.OptionDto(
            optionKey = "Color",
            optionValueList = listOf("Red", "Blue", "Green")
        )

        assertEquals("Color", option.optionKey)
        assertEquals(3, option.optionValueList.size)
        assertEquals(listOf("Red", "Blue", "Green"), option.optionValueList)
    }

    @Test
    fun `OptionDto with empty value list`() {
        val option = DisplayItemDto.OptionDto(
            optionKey = "EmptyOption",
            optionValueList = emptyList()
        )

        assertEquals("EmptyOption", option.optionKey)
        assertTrue(option.optionValueList.isEmpty())
    }

    @Test
    fun `OptionDto value list preserves order`() {
        val option = DisplayItemDto.OptionDto(
            optionKey = "Size",
            optionValueList = listOf("XS", "S", "M", "L", "XL", "XXL")
        )

        assertEquals("XS", option.optionValueList[0])
        assertEquals("XXL", option.optionValueList[5])
    }

    // ============= COPY TESTS =============

    @Test
    fun `copy preserves id when not specified`() {
        val original = DisplayItemDto(name = "Original")
        val copied = original.copy(name = "Copied")

        assertEquals(original.id, copied.id)
        assertEquals("Copied", copied.name)
    }

    @Test
    fun `copy can change id`() {
        val original = DisplayItemDto(name = "Original")
        val newId = Uuid.random()
        val copied = original.copy(id = newId)

        assertNotEquals(original.id, copied.id)
        assertEquals(newId, copied.id)
    }

    @Test
    fun `copy preserves other fields`() {
        val options = listOf(
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M"))
        )
        val original = DisplayItemDto(
            name = "Original",
            price = 50.0,
            options = options,
            isInCart = true
        )
        val copied = original.copy(name = "Copied")

        assertEquals(50.0, copied.price)
        assertEquals(1, copied.options.size)
        assertTrue(copied.isInCart)
    }

    // ============= EDGE CASES =============

    @Test
    fun `dto with zero price`() {
        val dto = DisplayItemDto(name = "Free Item", price = 0.0)

        assertEquals(0.0, dto.price)
    }

    @Test
    fun `dto with negative price (edge case)`() {
        val dto = DisplayItemDto(name = "Negative", price = -10.0)

        assertEquals(-10.0, dto.price)
    }

    @Test
    fun `dto with very large price`() {
        val dto = DisplayItemDto(name = "Expensive", price = 999999999.99)

        assertEquals(999999999.99, dto.price, 0.01)
    }

    @Test
    fun `dto with special characters in name`() {
        val dto = DisplayItemDto(name = "Book: 50% Off! @Special #1")

        assertEquals("Book: 50% Off! @Special #1", dto.name)
    }

    @Test
    fun `dto with unicode characters in name`() {
        val dto = DisplayItemDto(name = "经书 Scripture 📖")

        assertEquals("经书 Scripture 📖", dto.name)
    }

    @Test
    fun `dto with very long name`() {
        val longName = "A".repeat(1000)
        val dto = DisplayItemDto(name = longName)

        assertEquals(1000, dto.name.length)
    }

    @Test
    fun `dto with many options`() {
        val options = (1..100).map { i ->
            DisplayItemDto.OptionDto(
                optionKey = "Option$i",
                optionValueList = listOf("Value$i")
            )
        }
        val dto = DisplayItemDto(name = "Complex", options = options)

        assertEquals(100, dto.options.size)
    }

    @Test
    fun `option with duplicate values in list`() {
        val option = DisplayItemDto.OptionDto(
            optionKey = "Size",
            optionValueList = listOf("M", "M", "L")
        )

        assertEquals(3, option.optionValueList.size)
        assertEquals(2, option.optionValueList.count { it == "M" })
    }

    @Test
    fun `option with special characters in key`() {
        val option = DisplayItemDto.OptionDto(
            optionKey = "Size (US/UK)",
            optionValueList = listOf("S/M")
        )

        assertEquals("Size (US/UK)", option.optionKey)
    }

    @Test
    fun `option with unicode in key`() {
        val option = DisplayItemDto.OptionDto(
            optionKey = "尺寸 (Size)",
            optionValueList = listOf("小", "中", "大")
        )

        assertEquals("尺寸 (Size)", option.optionKey)
    }

    // ============= EQUALITY TESTS =============

    @Test
    fun `dtos with same data are equal`() {
        val id = Uuid.random()
        val options = listOf(
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M"))
        )

        val dto1 = DisplayItemDto(
            id = id,
            name = "Test",
            price = 10.0,
            options = options,
            isInCart = false
        )
        val dto2 = DisplayItemDto(
            id = id,
            name = "Test",
            price = 10.0,
            options = options,
            isInCart = false
        )

        assertEquals(dto1, dto2)
    }

    @Test
    fun `dtos with different ids are not equal`() {
        val dto1 = DisplayItemDto(name = "Test", price = 10.0)
        val dto2 = DisplayItemDto(name = "Test", price = 10.0)

        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `option dtos with same data are equal`() {
        val option1 =
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M", "L"))
        val option2 =
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M", "L"))

        assertEquals(option1, option2)
    }

    @Test
    fun `option dtos with different order are not equal`() {
        val option1 =
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M", "L"))
        val option2 =
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("L", "M"))

        assertNotEquals(option1, option2)
    }

    // ============= HASH CODE TESTS =============

    @Test
    fun `equal dtos have same hash code`() {
        val id = Uuid.random()
        val dto1 = DisplayItemDto(id = id, name = "Test", price = 10.0)
        val dto2 = DisplayItemDto(id = id, name = "Test", price = 10.0)

        assertEquals(dto1.hashCode(), dto2.hashCode())
    }

    @Test
    fun `equal options have same hash code`() {
        val option1 = DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M"))
        val option2 = DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("M"))

        assertEquals(option1.hashCode(), option2.hashCode())
    }
}

