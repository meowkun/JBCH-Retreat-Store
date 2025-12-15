package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckoutItemMapperTest {

    // ============= TO DOMAIN TESTS =============

    @Test
    fun `toDomain maps dto with new variants structure correctly`() {
        val variants = listOf(
            CheckoutItemDto.VariantDto(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "M"
            ),
            CheckoutItemDto.VariantDto(
                key = "Color",
                valueList = listOf("Red", "Blue"),
                selectedValue = "Blue"
            )
        )
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "T-Shirt",
            quantity = 2,
            variants = variants,
            totalPrice = 40.0
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals(dto.id, domain.id)
        assertEquals("T-Shirt", domain.itemName)
        assertEquals(2, domain.quantity)
        assertEquals(40.0, domain.totalPrice)
        assertEquals(2, domain.variants.size)
        assertEquals("Size", domain.variants[0].key)
        assertEquals(listOf("S", "M", "L"), domain.variants[0].valueList)
        assertEquals("M", domain.variants[0].selectedValue)
    }

    @Test
    fun `toDomain falls back to legacy optionsMap when variants empty`() {
        val dto = CheckoutItemDto(
            id = Uuid.random(),
            itemName = "T-Shirt",
            quantity = 2,
            optionsMap = mapOf("Size" to "M", "Color" to "Blue"),
            variants = emptyList(), // Empty variants
            totalPrice = 40.0
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals(2, domain.variants.size)
        // Legacy conversion should have selectedValue from optionsMap
        assertEquals("M", domain.variants.find { it.key == "Size" }?.selectedValue)
        assertEquals("Blue", domain.variants.find { it.key == "Color" }?.selectedValue)
        // valueList should contain only the selected value (legacy limitation)
        assertEquals(listOf("M"), domain.variants.find { it.key == "Size" }?.valueList)
    }

    @Test
    fun `toDomain handles empty optionsMap`() {
        val dto = CheckoutItemDto(
            itemName = "Simple Item",
            quantity = 1,
            optionsMap = emptyMap(),
            variants = emptyList(),
            totalPrice = 10.0
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertTrue(domain.variants.isEmpty())
    }

    @Test
    fun `toDomain preserves all fields`() {
        val id = Uuid.random()
        val dto = CheckoutItemDto(
            id = id,
            itemName = "Test",
            quantity = 5,
            variants = listOf(
                CheckoutItemDto.VariantDto(
                    key = "Option",
                    valueList = listOf("A"),
                    selectedValue = "A"
                )
            ),
            totalPrice = 50.0
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals(id, domain.id)
        assertEquals("Test", domain.itemName)
        assertEquals(5, domain.quantity)
        assertEquals(50.0, domain.totalPrice)
    }

    // ============= TO DTO TESTS =============

    @Test
    fun `toDto maps domain to dto correctly`() {
        val variants = listOf(
            CheckoutItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L"),
                selectedValue = "L"
            )
        )
        val domain = CheckoutItem(
            id = Uuid.random(),
            itemName = "T-Shirt",
            quantity = 3,
            variants = variants,
            totalPrice = 60.0
        )

        val dto = CheckoutItemMapper.toDto(domain)

        assertEquals(domain.id, dto.id)
        assertEquals("T-Shirt", dto.itemName)
        assertEquals(3, dto.quantity)
        assertEquals(60.0, dto.totalPrice)
        // optionsMap should be populated for backward compatibility
        assertEquals(mapOf("Size" to "L"), dto.optionsMap)
        // variants should be populated
        assertEquals(1, dto.variants.size)
        assertEquals("Size", dto.variants[0].key)
        assertEquals(listOf("S", "M", "L"), dto.variants[0].valueList)
        assertEquals("L", dto.variants[0].selectedValue)
    }

    @Test
    fun `toDto generates optionsMap from variants`() {
        val variants = listOf(
            CheckoutItem.Variant(key = "Size", valueList = listOf("M"), selectedValue = "M"),
            CheckoutItem.Variant(key = "Color", valueList = listOf("Red"), selectedValue = "Red")
        )
        val domain = CheckoutItem(variants = variants)

        val dto = CheckoutItemMapper.toDto(domain)

        assertEquals(mapOf("Size" to "M", "Color" to "Red"), dto.optionsMap)
    }

    @Test
    fun `toDto handles empty variants`() {
        val domain = CheckoutItem(
            itemName = "Simple Item",
            quantity = 1,
            variants = emptyList(),
            totalPrice = 10.0
        )

        val dto = CheckoutItemMapper.toDto(domain)

        assertTrue(dto.optionsMap.isEmpty())
        assertTrue(dto.variants.isEmpty())
    }

    // ============= LIST MAPPING TESTS =============

    @Test
    fun `toDomainList maps list correctly`() {
        val dtoList = listOf(
            CheckoutItemDto(itemName = "Item 1", quantity = 1, totalPrice = 10.0),
            CheckoutItemDto(itemName = "Item 2", quantity = 2, totalPrice = 20.0),
            CheckoutItemDto(itemName = "Item 3", quantity = 3, totalPrice = 30.0)
        )

        val domainList = CheckoutItemMapper.toDomainList(dtoList)

        assertEquals(3, domainList.size)
        assertEquals("Item 1", domainList[0].itemName)
        assertEquals("Item 2", domainList[1].itemName)
        assertEquals("Item 3", domainList[2].itemName)
    }

    @Test
    fun `toDomainList handles empty list`() {
        val domainList = CheckoutItemMapper.toDomainList(emptyList())

        assertTrue(domainList.isEmpty())
    }

    @Test
    fun `toDtoList maps list correctly`() {
        val domainList = listOf(
            CheckoutItem(itemName = "Item 1", quantity = 1, totalPrice = 10.0),
            CheckoutItem(itemName = "Item 2", quantity = 2, totalPrice = 20.0)
        )

        val dtoList = CheckoutItemMapper.toDtoList(domainList)

        assertEquals(2, dtoList.size)
        assertEquals("Item 1", dtoList[0].itemName)
        assertEquals("Item 2", dtoList[1].itemName)
    }

    @Test
    fun `toDtoList handles empty list`() {
        val dtoList = CheckoutItemMapper.toDtoList(emptyList())

        assertTrue(dtoList.isEmpty())
    }

    // ============= ROUND TRIP TESTS =============

    @Test
    fun `round trip preserves data with new variants`() {
        val original = CheckoutItem(
            itemName = "Round Trip Test",
            quantity = 5,
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("S", "M", "L"),
                    selectedValue = "M"
                ),
                CheckoutItem.Variant(
                    key = "Color",
                    valueList = listOf("Red", "Blue"),
                    selectedValue = "Red"
                )
            ),
            totalPrice = 100.0
        )

        val dto = CheckoutItemMapper.toDto(original)
        val restored = CheckoutItemMapper.toDomain(dto)

        assertEquals(original.itemName, restored.itemName)
        assertEquals(original.quantity, restored.quantity)
        assertEquals(original.totalPrice, restored.totalPrice)
        assertEquals(original.variants.size, restored.variants.size)
        assertEquals(original.variants[0].key, restored.variants[0].key)
        assertEquals(original.variants[0].valueList, restored.variants[0].valueList)
        assertEquals(original.variants[0].selectedValue, restored.variants[0].selectedValue)
    }

    @Test
    fun `round trip preserves id`() {
        val original = CheckoutItem(
            id = Uuid.random(),
            itemName = "Test",
            quantity = 1,
            totalPrice = 10.0
        )

        val dto = CheckoutItemMapper.toDto(original)
        val restored = CheckoutItemMapper.toDomain(dto)

        assertEquals(original.id, restored.id)
    }

    // ============= EDGE CASES =============

    @Test
    fun `mapping handles special characters in item name`() {
        val dto = CheckoutItemDto(
            itemName = "Book: 50% Off! @Special #1",
            quantity = 1,
            totalPrice = 10.0
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals("Book: 50% Off! @Special #1", domain.itemName)
    }

    @Test
    fun `mapping handles unicode characters`() {
        val dto = CheckoutItemDto(
            itemName = "经书 📚",
            quantity = 1,
            totalPrice = 15.0
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals("经书 📚", domain.itemName)
    }

    @Test
    fun `mapping handles variant with special characters in key`() {
        val variants = listOf(
            CheckoutItemDto.VariantDto(
                key = "Size (US)",
                valueList = listOf("S/M", "L/XL"),
                selectedValue = "S/M"
            )
        )
        val dto = CheckoutItemDto(variants = variants)

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals("Size (US)", domain.variants[0].key)
        assertEquals("S/M", domain.variants[0].selectedValue)
    }

    @Test
    fun `mapping handles many variants`() {
        val variants = (1..20).map { i ->
            CheckoutItemDto.VariantDto(
                key = "Option$i",
                valueList = listOf("Value$i"),
                selectedValue = "Value$i"
            )
        }
        val dto = CheckoutItemDto(variants = variants)

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals(20, domain.variants.size)
    }

    @Test
    fun `mapping handles very large values`() {
        val dto = CheckoutItemDto(
            itemName = "Expensive Item",
            quantity = Int.MAX_VALUE,
            totalPrice = Double.MAX_VALUE / 2 // Avoid overflow
        )

        val domain = CheckoutItemMapper.toDomain(dto)

        assertEquals(Int.MAX_VALUE, domain.quantity)
    }
}

