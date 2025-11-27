package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutItemMapperTest {

    // ========== toDomain Tests ==========

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val id = Uuid.random()
        val dto = CheckoutItemDto(
            id = id,
            itemName = "Coffee",
            quantity = 3,
            optionsMap = mapOf("size" to "Large", "sugar" to "Yes"),
            totalPrice = 15.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(id, domain.id)
        assertEquals("Coffee", domain.itemName)
        assertEquals(3, domain.quantity)
        assertEquals(2, domain.variantsMap.size)
        assertEquals("Large", domain.variantsMap["size"])
        assertEquals("Yes", domain.variantsMap["sugar"])
        assertEquals(15.0, domain.totalPrice)
    }

    @Test
    fun `toDomain with empty options map should work`() {
        // Given
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "Simple Item",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(0, domain.variantsMap.size)
    }

    @Test
    fun `toDomain with many options should map all correctly`() {
        // Given
        val options = mapOf(
            "size" to "Large",
            "color" to "Blue",
            "material" to "Cotton",
            "style" to "Modern",
            "finish" to "Glossy"
        )
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "Complex Item",
            quantity = 2,
            optionsMap = options,
            totalPrice = 100.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(5, domain.variantsMap.size)
        assertEquals("Large", domain.variantsMap["size"])
        assertEquals("Blue", domain.variantsMap["color"])
        assertEquals("Cotton", domain.variantsMap["material"])
    }

    @Test
    fun `toDomain with default values should work`() {
        // Given
        val dto = CheckoutItemDto()

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertNotNull(domain.id)
        assertEquals("", domain.itemName)
        assertEquals(1, domain.quantity)
        assertEquals(0, domain.variantsMap.size)
        assertEquals(0.0, domain.totalPrice)
    }

    // ========== toDto Tests ==========

    @Test
    fun `toDto should map all fields correctly`() {
        // Given
        val id = Uuid.random()
        val domain = CheckoutItem(
            id = id,
            itemName = "Coffee",
            quantity = 3,
            variantsMap = mapOf("size" to "Large", "sugar" to "Yes"),
            totalPrice = 15.0
        )

        // When
        val dto = CheckoutItemMapper.toDto(domain)

        // Then
        assertEquals(id, dto.id)
        assertEquals("Coffee", dto.itemName)
        assertEquals(3, dto.quantity)
        assertEquals(2, dto.optionsMap.size)
        assertEquals("Large", dto.optionsMap["size"])
        assertEquals("Yes", dto.optionsMap["sugar"])
        assertEquals(15.0, dto.totalPrice)
    }

    @Test
    fun `toDto with empty options map should work`() {
        // Given
        val domain = CheckoutItem(
            id = Uuid.random(),
            itemName = "Simple Item",
            quantity = 1,
            variantsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val dto = CheckoutItemMapper.toDto(domain)

        // Then
        assertEquals(0, dto.optionsMap.size)
    }

    @Test
    fun `toDto with many options should map all correctly`() {
        // Given
        val options = mapOf(
            "size" to "Large",
            "color" to "Blue",
            "material" to "Cotton"
        )
        val domain = CheckoutItem(
            id = Uuid.random(),
            itemName = "Complex Item",
            quantity = 2,
            variantsMap = options,
            totalPrice = 100.0
        )

        // When
        val dto = CheckoutItemMapper.toDto(domain)

        // Then
        assertEquals(3, dto.optionsMap.size)
        assertEquals("Large", dto.optionsMap["size"])
        assertEquals("Blue", dto.optionsMap["color"])
        assertEquals("Cotton", dto.optionsMap["material"])
    }

    // ========== Round-trip Tests ==========

    @Test
    fun `round trip toDomain then toDto should preserve data`() {
        // Given
        val originalDto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "Test Item",
            quantity = 5,
            optionsMap = mapOf("option1" to "value1", "option2" to "value2"),
            totalPrice = 50.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(originalDto)
        val resultDto = CheckoutItemMapper.toDto(domain)

        // Then
        assertEquals(originalDto.id, resultDto.id)
        assertEquals(originalDto.itemName, resultDto.itemName)
        assertEquals(originalDto.quantity, resultDto.quantity)
        assertEquals(originalDto.optionsMap, resultDto.optionsMap)
        assertEquals(originalDto.totalPrice, resultDto.totalPrice)
    }

    @Test
    fun `round trip toDto then toDomain should preserve data`() {
        // Given
        val originalDomain = CheckoutItem(
            id = Uuid.random(),
            itemName = "Test Item",
            quantity = 5,
            variantsMap = mapOf("option1" to "value1", "option2" to "value2"),
            totalPrice = 50.0
        )

        // When
        val dto = CheckoutItemMapper.toDto(originalDomain)
        val resultDomain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(originalDomain.id, resultDomain.id)
        assertEquals(originalDomain.itemName, resultDomain.itemName)
        assertEquals(originalDomain.quantity, resultDomain.quantity)
        assertEquals(originalDomain.variantsMap, resultDomain.variantsMap)
        assertEquals(originalDomain.totalPrice, resultDomain.totalPrice)
    }

    // ========== toDomainList Tests ==========

    @Test
    fun `toDomainList should map all items`() {
        // Given
        val dtoList = listOf(
            CheckoutItemDto(Uuid.random(), "Item 1", 1, emptyMap(), 10.0),
            CheckoutItemDto(Uuid.random(), "Item 2", 2, emptyMap(), 20.0),
            CheckoutItemDto(Uuid.random(), "Item 3", 3, emptyMap(), 30.0)
        )

        // When
        val domainList = CheckoutItemMapper.toDomainList(dtoList)

        // Then
        assertEquals(3, domainList.size)
        assertEquals("Item 1", domainList[0].itemName)
        assertEquals("Item 2", domainList[1].itemName)
        assertEquals("Item 3", domainList[2].itemName)
        assertEquals(1, domainList[0].quantity)
        assertEquals(2, domainList[1].quantity)
        assertEquals(3, domainList[2].quantity)
    }

    @Test
    fun `toDomainList with empty list should return empty list`() {
        // When
        val domainList = CheckoutItemMapper.toDomainList(emptyList())

        // Then
        assertEquals(0, domainList.size)
    }

    @Test
    fun `toDomainList with single item should work`() {
        // Given
        val dtoList = listOf(
            CheckoutItemDto(Uuid.random(), "Single Item", 5, mapOf("key" to "value"), 50.0)
        )

        // When
        val domainList = CheckoutItemMapper.toDomainList(dtoList)

        // Then
        assertEquals(1, domainList.size)
        assertEquals("Single Item", domainList[0].itemName)
        assertEquals(5, domainList[0].quantity)
    }

    // ========== toDtoList Tests ==========

    @Test
    fun `toDtoList should map all items`() {
        // Given
        val domainList = listOf(
            CheckoutItem(Uuid.random(), "Item 1", 1, emptyMap(), 10.0),
            CheckoutItem(Uuid.random(), "Item 2", 2, emptyMap(), 20.0),
            CheckoutItem(Uuid.random(), "Item 3", 3, emptyMap(), 30.0)
        )

        // When
        val dtoList = CheckoutItemMapper.toDtoList(domainList)

        // Then
        assertEquals(3, dtoList.size)
        assertEquals("Item 1", dtoList[0].itemName)
        assertEquals("Item 2", dtoList[1].itemName)
        assertEquals("Item 3", dtoList[2].itemName)
    }

    @Test
    fun `toDtoList with empty list should return empty list`() {
        // When
        val dtoList = CheckoutItemMapper.toDtoList(emptyList())

        // Then
        assertEquals(0, dtoList.size)
    }

    // ========== Edge Cases ==========

    @Test
    fun `toDomain with very long item name should work`() {
        // Given
        val longName = "a".repeat(1000)
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = longName,
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(longName, domain.itemName)
    }

    @Test
    fun `toDomain with large quantity should work`() {
        // Given
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "Item",
            quantity = Int.MAX_VALUE,
            optionsMap = emptyMap(),
            totalPrice = 10.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(Int.MAX_VALUE, domain.quantity)
    }

    @Test
    fun `toDomain with large price should work`() {
        // Given
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "Item",
            quantity = 1,
            optionsMap = emptyMap(),
            totalPrice = Double.MAX_VALUE
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals(Double.MAX_VALUE, domain.totalPrice)
    }

    @Test
    fun `toDomain with special characters in options should work`() {
        // Given
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "Item",
            quantity = 1,
            optionsMap = mapOf(
                "special@key#1" to "value!@#$%",
                "unicode_😀" to "emoji_🎉"
            ),
            totalPrice = 10.0
        )

        // When
        val domain = CheckoutItemMapper.toDomain(dto)

        // Then
        assertEquals("value!@#$%", domain.variantsMap["special@key#1"])
        assertEquals("emoji_🎉", domain.variantsMap["unicode_😀"])
    }
}

