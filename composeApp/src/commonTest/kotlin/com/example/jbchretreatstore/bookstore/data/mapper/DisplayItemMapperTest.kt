package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlin.test.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DisplayItemMapperTest {

    // ========== toDomain Tests ==========

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val id = Uuid.random()
        val dto = DisplayItemDto(
            id = id,
            name = "Test Item",
            price = 29.99,
            options = listOf(
                DisplayItemDto.OptionDto(
                    optionKey = "size",
                    optionValueList = listOf("Small", "Medium", "Large")
                )
            ),
            isInCart = true
        )

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(id, domain.id)
        assertEquals("Test Item", domain.name)
        assertEquals(29.99, domain.price)
        assertEquals(1, domain.options.size)
        assertEquals("size", domain.options[0].optionKey)
        assertEquals(3, domain.options[0].optionValueList.size)
        assertTrue(domain.isInCart)
    }

    @Test
    fun `toDomain with empty options should map correctly`() {
        // Given
        val dto = DisplayItemDto(
            id = Uuid.random(),
            name = "Simple Item",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(0, domain.options.size)
        assertFalse(domain.isInCart)
    }

    @Test
    fun `toDomain with multiple options should map all correctly`() {
        // Given
        val dto = DisplayItemDto(
            id = Uuid.random(),
            name = "Complex Item",
            price = 50.0,
            options = listOf(
                DisplayItemDto.OptionDto("size", listOf("S", "M", "L")),
                DisplayItemDto.OptionDto("color", listOf("Red", "Blue")),
                DisplayItemDto.OptionDto("material", listOf("Cotton", "Polyester"))
            ),
            isInCart = false
        )

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(3, domain.options.size)
        assertEquals("size", domain.options[0].optionKey)
        assertEquals("color", domain.options[1].optionKey)
        assertEquals("material", domain.options[2].optionKey)
    }

    @Test
    fun `toDomain with default values should work`() {
        // Given
        val dto = DisplayItemDto()

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertNotNull(domain.id)
        assertEquals("", domain.name)
        assertEquals(0.0, domain.price)
        assertEquals(0, domain.options.size)
        assertFalse(domain.isInCart)
    }

    // ========== toDto Tests ==========

    @Test
    fun `toDto should map all fields correctly`() {
        // Given
        val id = Uuid.random()
        val domain = DisplayItem(
            id = id,
            name = "Test Item",
            price = 29.99,
            options = listOf(
                DisplayItem.Option(
                    optionKey = "size",
                    optionValueList = listOf("Small", "Medium", "Large")
                )
            ),
            isInCart = true
        )

        // When
        val dto = DisplayItemMapper.toDto(domain)

        // Then
        assertEquals(id, dto.id)
        assertEquals("Test Item", dto.name)
        assertEquals(29.99, dto.price)
        assertEquals(1, dto.options.size)
        assertEquals("size", dto.options[0].optionKey)
        assertEquals(3, dto.options[0].optionValueList.size)
        assertTrue(dto.isInCart)
    }

    @Test
    fun `toDto with empty options should map correctly`() {
        // Given
        val domain = DisplayItem(
            id = Uuid.random(),
            name = "Simple Item",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val dto = DisplayItemMapper.toDto(domain)

        // Then
        assertEquals(0, dto.options.size)
        assertFalse(dto.isInCart)
    }

    @Test
    fun `toDto with multiple options should map all correctly`() {
        // Given
        val domain = DisplayItem(
            id = Uuid.random(),
            name = "Complex Item",
            price = 50.0,
            options = listOf(
                DisplayItem.Option("size", listOf("S", "M", "L")),
                DisplayItem.Option("color", listOf("Red", "Blue")),
                DisplayItem.Option("material", listOf("Cotton", "Polyester"))
            ),
            isInCart = true
        )

        // When
        val dto = DisplayItemMapper.toDto(domain)

        // Then
        assertEquals(3, dto.options.size)
        assertEquals("size", dto.options[0].optionKey)
        assertEquals("color", dto.options[1].optionKey)
        assertEquals("material", dto.options[2].optionKey)
    }

    // ========== Round-trip Tests ==========

    @Test
    fun `round trip toDomain then toDto should preserve data`() {
        // Given
        val originalDto = DisplayItemDto(
            id = Uuid.random(),
            name = "Original Item",
            price = 99.99,
            options = listOf(
                DisplayItemDto.OptionDto("color", listOf("Red", "Blue"))
            ),
            isInCart = true
        )

        // When
        val domain = DisplayItemMapper.toDomain(originalDto)
        val resultDto = DisplayItemMapper.toDto(domain)

        // Then
        assertEquals(originalDto.id, resultDto.id)
        assertEquals(originalDto.name, resultDto.name)
        assertEquals(originalDto.price, resultDto.price)
        assertEquals(originalDto.options.size, resultDto.options.size)
        assertEquals(originalDto.isInCart, resultDto.isInCart)
    }

    @Test
    fun `round trip toDto then toDomain should preserve data`() {
        // Given
        val originalDomain = DisplayItem(
            id = Uuid.random(),
            name = "Original Item",
            price = 99.99,
            options = listOf(
                DisplayItem.Option("color", listOf("Red", "Blue"))
            ),
            isInCart = true
        )

        // When
        val dto = DisplayItemMapper.toDto(originalDomain)
        val resultDomain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(originalDomain.id, resultDomain.id)
        assertEquals(originalDomain.name, resultDomain.name)
        assertEquals(originalDomain.price, resultDomain.price)
        assertEquals(originalDomain.options.size, resultDomain.options.size)
        assertEquals(originalDomain.isInCart, resultDomain.isInCart)
    }

    // ========== toDomainList Tests ==========

    @Test
    fun `toDomainList should map all items`() {
        // Given
        val dtoList = listOf(
            DisplayItemDto(Uuid.random(), "Item 1", 10.0, emptyList(), false),
            DisplayItemDto(Uuid.random(), "Item 2", 20.0, emptyList(), true),
            DisplayItemDto(Uuid.random(), "Item 3", 30.0, emptyList(), false)
        )

        // When
        val domainList = DisplayItemMapper.toDomainList(dtoList)

        // Then
        assertEquals(3, domainList.size)
        assertEquals("Item 1", domainList[0].name)
        assertEquals("Item 2", domainList[1].name)
        assertEquals("Item 3", domainList[2].name)
    }

    @Test
    fun `toDomainList with empty list should return empty list`() {
        // When
        val domainList = DisplayItemMapper.toDomainList(emptyList())

        // Then
        assertEquals(0, domainList.size)
    }

    // ========== toDtoList Tests ==========

    @Test
    fun `toDtoList should map all items`() {
        // Given
        val domainList = listOf(
            DisplayItem(Uuid.random(), "Item 1", 10.0, emptyList(), false),
            DisplayItem(Uuid.random(), "Item 2", 20.0, emptyList(), true),
            DisplayItem(Uuid.random(), "Item 3", 30.0, emptyList(), false)
        )

        // When
        val dtoList = DisplayItemMapper.toDtoList(domainList)

        // Then
        assertEquals(3, dtoList.size)
        assertEquals("Item 1", dtoList[0].name)
        assertEquals("Item 2", dtoList[1].name)
        assertEquals("Item 3", dtoList[2].name)
    }

    @Test
    fun `toDtoList with empty list should return empty list`() {
        // When
        val dtoList = DisplayItemMapper.toDtoList(emptyList())

        // Then
        assertEquals(0, dtoList.size)
    }

    // ========== Edge Cases ==========

    @Test
    fun `toDomain with very long name should work`() {
        // Given
        val longName = "a".repeat(1000)
        val dto = DisplayItemDto(
            id = Uuid.random(),
            name = longName,
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(longName, domain.name)
    }

    @Test
    fun `toDomain with very large price should work`() {
        // Given
        val dto = DisplayItemDto(
            id = Uuid.random(),
            name = "Expensive",
            price = Double.MAX_VALUE,
            options = emptyList(),
            isInCart = false
        )

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(Double.MAX_VALUE, domain.price)
    }

    @Test
    fun `toDomain with many option values should work`() {
        // Given
        val manyValues = (1..100).map { "Value $it" }
        val dto = DisplayItemDto(
            id = Uuid.random(),
            name = "Item",
            price = 10.0,
            options = listOf(
                DisplayItemDto.OptionDto("sizes", manyValues)
            ),
            isInCart = false
        )

        // When
        val domain = DisplayItemMapper.toDomain(dto)

        // Then
        assertEquals(100, domain.options[0].optionValueList.size)
    }
}

