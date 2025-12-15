package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DisplayItemMapperTest {

    // ============= TO DOMAIN TESTS =============

    @Test
    fun `toDomain maps dto correctly`() {
        val options = listOf(
            DisplayItemDto.OptionDto(optionKey = "Size", optionValueList = listOf("S", "M", "L")),
            DisplayItemDto.OptionDto(optionKey = "Color", optionValueList = listOf("Red", "Blue"))
        )
        val dto = DisplayItemDto(
            id = Uuid.random(),
            name = "T-Shirt",
            price = 25.0,
            options = options,
            isInCart = true
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(dto.id, domain.id)
        assertEquals("T-Shirt", domain.name)
        assertEquals(25.0, domain.price)
        assertTrue(domain.isInCart)
        assertEquals(2, domain.variants.size)
        assertEquals("Size", domain.variants[0].key)
        assertEquals(listOf("S", "M", "L"), domain.variants[0].valueList)
    }

    @Test
    fun `toDomain handles empty options`() {
        val dto = DisplayItemDto(
            name = "Simple Item",
            price = 10.0,
            options = emptyList()
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertTrue(domain.variants.isEmpty())
    }

    @Test
    fun `toDomain preserves all fields`() {
        val id = Uuid.random()
        val dto = DisplayItemDto(
            id = id,
            name = "Test",
            price = 99.99,
            options = listOf(
                DisplayItemDto.OptionDto(
                    optionKey = "Option",
                    optionValueList = listOf("A", "B")
                )
            ),
            isInCart = false
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(id, domain.id)
        assertEquals("Test", domain.name)
        assertEquals(99.99, domain.price)
        assertFalse(domain.isInCart)
    }

    @Test
    fun `toDomain handles default values`() {
        val dto = DisplayItemDto()

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals("", domain.name)
        assertEquals(0.0, domain.price)
        assertFalse(domain.isInCart)
        assertTrue(domain.variants.isEmpty())
    }

    // ============= TO DTO TESTS =============

    @Test
    fun `toDto maps domain correctly`() {
        val variants = listOf(
            DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L")),
            DisplayItem.Variant(key = "Color", valueList = listOf("Red", "Blue"))
        )
        val domain = DisplayItem(
            id = Uuid.random(),
            name = "T-Shirt",
            price = 25.0,
            variants = variants,
            isInCart = true
        )

        val dto = DisplayItemMapper.toDto(domain)

        assertEquals(domain.id, dto.id)
        assertEquals("T-Shirt", dto.name)
        assertEquals(25.0, dto.price)
        assertTrue(dto.isInCart)
        assertEquals(2, dto.options.size)
        assertEquals("Size", dto.options[0].optionKey)
        assertEquals(listOf("S", "M", "L"), dto.options[0].optionValueList)
    }

    @Test
    fun `toDto handles empty variants`() {
        val domain = DisplayItem(
            name = "Simple Item",
            price = 10.0,
            variants = emptyList()
        )

        val dto = DisplayItemMapper.toDto(domain)

        assertTrue(dto.options.isEmpty())
    }

    // ============= LIST MAPPING TESTS =============

    @Test
    fun `toDomainList maps list correctly`() {
        val dtoList = listOf(
            DisplayItemDto(name = "Item 1", price = 10.0),
            DisplayItemDto(name = "Item 2", price = 20.0),
            DisplayItemDto(name = "Item 3", price = 30.0)
        )

        val domainList = DisplayItemMapper.toDomainList(dtoList)

        assertEquals(3, domainList.size)
        assertEquals("Item 1", domainList[0].name)
        assertEquals("Item 2", domainList[1].name)
        assertEquals("Item 3", domainList[2].name)
    }

    @Test
    fun `toDomainList handles empty list`() {
        val domainList = DisplayItemMapper.toDomainList(emptyList())

        assertTrue(domainList.isEmpty())
    }

    @Test
    fun `toDtoList maps list correctly`() {
        val domainList = listOf(
            DisplayItem(name = "Item 1", price = 10.0),
            DisplayItem(name = "Item 2", price = 20.0)
        )

        val dtoList = DisplayItemMapper.toDtoList(domainList)

        assertEquals(2, dtoList.size)
        assertEquals("Item 1", dtoList[0].name)
        assertEquals("Item 2", dtoList[1].name)
    }

    @Test
    fun `toDtoList handles empty list`() {
        val dtoList = DisplayItemMapper.toDtoList(emptyList())

        assertTrue(dtoList.isEmpty())
    }

    // ============= ROUND TRIP TESTS =============

    @Test
    fun `round trip preserves data`() {
        val original = DisplayItem(
            name = "Round Trip Test",
            price = 50.0,
            variants = listOf(
                DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L")),
                DisplayItem.Variant(key = "Color", valueList = listOf("Red", "Blue", "Green"))
            ),
            isInCart = true
        )

        val dto = DisplayItemMapper.toDto(original)
        val restored = DisplayItemMapper.toDomain(dto)

        assertEquals(original.name, restored.name)
        assertEquals(original.price, restored.price)
        assertEquals(original.isInCart, restored.isInCart)
        assertEquals(original.variants.size, restored.variants.size)
        assertEquals(original.variants[0].key, restored.variants[0].key)
        assertEquals(original.variants[0].valueList, restored.variants[0].valueList)
    }

    @Test
    fun `round trip preserves id`() {
        val original = DisplayItem(
            id = Uuid.random(),
            name = "Test",
            price = 10.0
        )

        val dto = DisplayItemMapper.toDto(original)
        val restored = DisplayItemMapper.toDomain(dto)

        assertEquals(original.id, restored.id)
    }

    // ============= EDGE CASES =============

    @Test
    fun `mapping handles special characters in name`() {
        val dto = DisplayItemDto(
            name = "Book: 50% Off! @Special #1",
            price = 10.0
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals("Book: 50% Off! @Special #1", domain.name)
    }

    @Test
    fun `mapping handles unicode characters`() {
        val dto = DisplayItemDto(
            name = "经书 Scripture 📖",
            price = 15.0
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals("经书 Scripture 📖", domain.name)
    }

    @Test
    fun `mapping handles option with special characters in key`() {
        val options = listOf(
            DisplayItemDto.OptionDto(
                optionKey = "Size (US)",
                optionValueList = listOf("Small/Medium", "Large/XL")
            )
        )
        val dto = DisplayItemDto(name = "Item", price = 10.0, options = options)

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals("Size (US)", domain.variants[0].key)
    }

    @Test
    fun `mapping handles many options`() {
        val options = (1..50).map { i ->
            DisplayItemDto.OptionDto(
                optionKey = "Option$i",
                optionValueList = (1..10).map { "Value$it" }
            )
        }
        val dto = DisplayItemDto(name = "Complex Item", price = 100.0, options = options)

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(50, domain.variants.size)
        assertEquals(10, domain.variants[0].valueList.size)
    }

    @Test
    fun `mapping handles very large price`() {
        val dto = DisplayItemDto(
            name = "Expensive Item",
            price = 999999999.99
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(999999999.99, domain.price, 0.01)
    }

    @Test
    fun `mapping handles zero price`() {
        val dto = DisplayItemDto(
            name = "Free Item",
            price = 0.0
        )

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(0.0, domain.price)
    }

    @Test
    fun `mapping handles option with empty value list`() {
        val options = listOf(
            DisplayItemDto.OptionDto(
                optionKey = "EmptyOption",
                optionValueList = emptyList()
            )
        )
        val dto = DisplayItemDto(name = "Item", price = 10.0, options = options)

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(1, domain.variants.size)
        assertTrue(domain.variants[0].valueList.isEmpty())
    }

    @Test
    fun `mapping preserves order of options`() {
        val options = listOf(
            DisplayItemDto.OptionDto(optionKey = "First", optionValueList = listOf("1")),
            DisplayItemDto.OptionDto(optionKey = "Second", optionValueList = listOf("2")),
            DisplayItemDto.OptionDto(optionKey = "Third", optionValueList = listOf("3"))
        )
        val dto = DisplayItemDto(name = "Item", price = 10.0, options = options)

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals("First", domain.variants[0].key)
        assertEquals("Second", domain.variants[1].key)
        assertEquals("Third", domain.variants[2].key)
    }

    @Test
    fun `mapping preserves order of option values`() {
        val options = listOf(
            DisplayItemDto.OptionDto(
                optionKey = "Size",
                optionValueList = listOf("XS", "S", "M", "L", "XL", "XXL")
            )
        )
        val dto = DisplayItemDto(name = "Item", price = 10.0, options = options)

        val domain = DisplayItemMapper.toDomain(dto)

        assertEquals(listOf("XS", "S", "M", "L", "XL", "XXL"), domain.variants[0].valueList)
    }
}

