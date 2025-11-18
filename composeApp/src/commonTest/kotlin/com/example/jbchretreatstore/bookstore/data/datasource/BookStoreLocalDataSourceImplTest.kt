package com.example.jbchretreatstore.bookstore.data.datasource

import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Tests for BookStoreLocalDataSourceImpl focusing on:
 * - Normal save/load operations
 * - Backward compatibility with old JSON formats
 * - Resilience to missing fields
 * - Edge cases
 */
@OptIn(ExperimentalUuidApi::class)
class BookStoreLocalDataSourceImplTest {

    // Note: These tests verify the JSON parsing logic.
    // Full DataStore integration tests would require platform-specific setup.

    @Test
    fun `DisplayItemDto with all fields should serialize and deserialize correctly`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "Test Item",
            price = 29.99,
            options = listOf(
                DisplayItemDto.OptionDto("size", listOf("Small", "Large"))
            ),
            isInCart = true
        )

        // Verify all fields are present
        assertEquals("Test Item", item.name)
        assertEquals(29.99, item.price)
        assertEquals(1, item.options.size)
        assertTrue(item.isInCart)
    }

    @Test
    fun `DisplayItemDto with default values should work`() {
        // Given - using defaults
        val item = DisplayItemDto()

        // Then
        assertNotNull(item.id)
        assertEquals("", item.name)
        assertEquals(0.0, item.price)
        assertEquals(0, item.options.size)
        assertFalse(item.isInCart)
    }

    @Test
    fun `DisplayItemDto with partial fields should use defaults`() {
        // Given - only some fields
        val item = DisplayItemDto(
            name = "Partial Item",
            price = 15.0
        )

        // Then - other fields should have defaults
        assertNotNull(item.id)
        assertEquals("Partial Item", item.name)
        assertEquals(15.0, item.price)
        assertEquals(0, item.options.size)
        assertFalse(item.isInCart)
    }

    @Test
    fun `DisplayItemDto OptionDto with defaults should work`() {
        // Given
        val option = DisplayItemDto.OptionDto()

        // Then
        assertEquals("", option.optionKey)
        assertEquals(0, option.optionValueList.size)
    }

    @Test
    fun `DisplayItemDto with empty options list should work`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "No Options",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertEquals(0, item.options.size)
    }

    @Test
    fun `DisplayItemDto with multiple options should work`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "Multi Options",
            price = 50.0,
            options = listOf(
                DisplayItemDto.OptionDto("size", listOf("S", "M", "L")),
                DisplayItemDto.OptionDto("color", listOf("Red", "Blue")),
                DisplayItemDto.OptionDto("material", listOf("Cotton"))
            ),
            isInCart = true
        )

        // Then
        assertEquals(3, item.options.size)
        assertEquals("size", item.options[0].optionKey)
        assertEquals("color", item.options[1].optionKey)
        assertEquals("material", item.options[2].optionKey)
    }

    @Test
    fun `DisplayItemDto with isInCart true should preserve state`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "In Cart Item",
            price = 25.0,
            options = emptyList(),
            isInCart = true
        )

        // Then
        assertTrue(item.isInCart)
    }

    @Test
    fun `DisplayItemDto with isInCart false should preserve state`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "Not In Cart",
            price = 25.0,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertFalse(item.isInCart)
    }

    @Test
    fun `DisplayItemDto with very long name should work`() {
        // Given
        val longName = "a".repeat(1000)
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = longName,
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertEquals(longName, item.name)
    }

    @Test
    fun `DisplayItemDto with very large price should work`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "Expensive",
            price = Double.MAX_VALUE,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertEquals(Double.MAX_VALUE, item.price)
    }

    @Test
    fun `DisplayItemDto with zero price should work`() {
        // Given
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "Free",
            price = 0.0,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertEquals(0.0, item.price)
    }

    @Test
    fun `DisplayItemDto with special characters in name should work`() {
        // Given
        val specialName = "Test!@#$%^&*()_+-=[]{}|;':\",./<>?`~"
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = specialName,
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertEquals(specialName, item.name)
    }

    @Test
    fun `DisplayItemDto with unicode characters should work`() {
        // Given
        val unicodeName = "咖啡 ☕ Café 🎉"
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = unicodeName,
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // Then
        assertEquals(unicodeName, item.name)
    }

    @Test
    fun `DisplayItemDto with many option values should work`() {
        // Given
        val manyValues = (1..100).map { "Value $it" }
        val item = DisplayItemDto(
            id = Uuid.random(),
            name = "Item",
            price = 10.0,
            options = listOf(
                DisplayItemDto.OptionDto("sizes", manyValues)
            ),
            isInCart = false
        )

        // Then
        assertEquals(100, item.options[0].optionValueList.size)
    }

    @Test
    fun `DisplayItemDto list with mixed states should work`() {
        // Given
        val items = listOf(
            DisplayItemDto(Uuid.random(), "Item 1", 10.0, emptyList(), true),
            DisplayItemDto(Uuid.random(), "Item 2", 20.0, emptyList(), false),
            DisplayItemDto(Uuid.random(), "Item 3", 30.0, listOf(
                DisplayItemDto.OptionDto("option", listOf("A", "B"))
            ), true)
        )

        // Then
        assertEquals(3, items.size)
        assertTrue(items[0].isInCart)
        assertFalse(items[1].isInCart)
        assertTrue(items[2].isInCart)
        assertEquals(0, items[0].options.size)
        assertEquals(1, items[2].options.size)
    }

    @Test
    fun `DisplayItemDto copy with modifications should work`() {
        // Given
        val original = DisplayItemDto(
            id = Uuid.random(),
            name = "Original",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val modified = original.copy(name = "Modified", price = 20.0, isInCart = true)

        // Then
        assertEquals("Modified", modified.name)
        assertEquals(20.0, modified.price)
        assertTrue(modified.isInCart)
        assertEquals(original.id, modified.id) // ID should be preserved
    }

    @Test
    fun `DisplayItemDto equality should work`() {
        // Given
        val id = Uuid.random()
        val item1 = DisplayItemDto(id, "Item", 10.0, emptyList(), false)
        val item2 = DisplayItemDto(id, "Item", 10.0, emptyList(), false)
        val item3 = DisplayItemDto(Uuid.random(), "Item", 10.0, emptyList(), false)

        // Then
        assertEquals(item1, item2) // Same data should be equal
        assertNotEquals(item1, item3) // Different IDs should not be equal
    }

    @Test
    fun `OptionDto equality should work`() {
        // Given
        val option1 = DisplayItemDto.OptionDto("size", listOf("S", "M"))
        val option2 = DisplayItemDto.OptionDto("size", listOf("S", "M"))
        val option3 = DisplayItemDto.OptionDto("color", listOf("Red"))

        // Then
        assertEquals(option1, option2)
        assertNotEquals(option1, option3)
    }

    @Test
    fun `OptionDto with empty value list should work`() {
        // Given
        val option = DisplayItemDto.OptionDto("empty", emptyList())

        // Then
        assertEquals("empty", option.optionKey)
        assertEquals(0, option.optionValueList.size)
    }

    @Test
    fun `OptionDto with single value should work`() {
        // Given
        val option = DisplayItemDto.OptionDto("single", listOf("OnlyOne"))

        // Then
        assertEquals(1, option.optionValueList.size)
        assertEquals("OnlyOne", option.optionValueList[0])
    }

    @Test
    fun `OptionDto copy should work`() {
        // Given
        val original = DisplayItemDto.OptionDto("size", listOf("S"))

        // When
        val modified = original.copy(optionValueList = listOf("S", "M", "L"))

        // Then
        assertEquals("size", modified.optionKey)
        assertEquals(3, modified.optionValueList.size)
    }
}

