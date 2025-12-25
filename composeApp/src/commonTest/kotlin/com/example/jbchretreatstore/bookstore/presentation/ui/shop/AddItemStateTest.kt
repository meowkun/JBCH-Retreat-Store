package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddItemStateTest {

    // ==================== Companion Object Tests ====================

    @Test
    fun `fromItem with null creates default state in add mode`() {
        val state = AddItemState.fromItem(null)

        assertEquals("", state.newItem.name)
        assertEquals(0.0, state.newItem.price)
        assertEquals("", state.displayPrice)
        assertFalse(state.isEditMode)
    }

    @Test
    fun `fromItem with item creates state in edit mode`() {
        val item = DisplayItem(name = "Test Item", price = 29.99)
        val state = AddItemState.fromItem(item)

        assertEquals("Test Item", state.newItem.name)
        assertEquals(29.99, state.newItem.price)
        assertEquals("29.99", state.displayPrice)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `fromItem with zero price item has empty displayPrice`() {
        val item = DisplayItem(name = "Free Item", price = 0.0)
        val state = AddItemState.fromItem(item)

        assertEquals("", state.displayPrice)
    }

    // ==================== Derived Properties Tests ====================

    @Test
    fun `isOptionKeyEnabled returns true when no values added`() {
        val state = AddItemState()
        assertTrue(state.isOptionKeyEnabled)
    }

    @Test
    fun `isOptionKeyEnabled returns false when values exist`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(valueList = listOf("Value1"))
        )
        assertFalse(state.isOptionKeyEnabled)
    }

    @Test
    fun `hasVariants returns false for empty variants`() {
        val state = AddItemState()
        assertFalse(state.hasVariants)
    }

    @Test
    fun `hasVariants returns true when variants exist`() {
        val state = AddItemState(
            newItem = DisplayItem(
                variants = listOf(DisplayItem.Variant(key = "Size", valueList = listOf("S", "M")))
            )
        )
        assertTrue(state.hasVariants)
    }

    @Test
    fun `showNameFieldError returns true only when error flag set and name blank`() {
        val stateWithError =
            AddItemState(showItemNameError = true, newItem = DisplayItem(name = ""))
        val stateWithName =
            AddItemState(showItemNameError = true, newItem = DisplayItem(name = "Test"))
        val stateNoError = AddItemState(showItemNameError = false, newItem = DisplayItem(name = ""))

        assertTrue(stateWithError.showNameFieldError)
        assertFalse(stateWithName.showNameFieldError)
        assertFalse(stateNoError.showNameFieldError)
    }

    @Test
    fun `showPriceFieldError returns true only when error flag set and price invalid`() {
        val stateWithError =
            AddItemState(showItemPriceError = true, newItem = DisplayItem(price = 0.0))
        val stateWithPrice =
            AddItemState(showItemPriceError = true, newItem = DisplayItem(price = 10.0))
        val stateNoError =
            AddItemState(showItemPriceError = false, newItem = DisplayItem(price = 0.0))

        assertTrue(stateWithError.showPriceFieldError)
        assertFalse(stateWithPrice.showPriceFieldError)
        assertFalse(stateNoError.showPriceFieldError)
    }

    @Test
    fun `showVariantKeyFieldError returns true when either error flag set`() {
        val stateWithAddError = AddItemState(showAddOptionError = true)
        val stateWithDuplicateError = AddItemState(showDuplicateKeyError = true)
        val stateNoError = AddItemState()

        assertTrue(stateWithAddError.showVariantKeyFieldError)
        assertTrue(stateWithDuplicateError.showVariantKeyFieldError)
        assertFalse(stateNoError.showVariantKeyFieldError)
    }

    // ==================== Field Update Functions Tests ====================

    @Test
    fun `updateName updates item name and clears error`() {
        val state = AddItemState(showItemNameError = true)
        val newState = state.updateName("New Name")

        assertEquals("New Name", newState.newItem.name)
        assertFalse(newState.showItemNameError)
    }

    @Test
    fun `updatePrice with valid input updates price and displayPrice`() {
        val state = AddItemState()
        val newState = state.updatePrice("25.99")

        assertNotNull(newState)
        assertEquals("25.99", newState.displayPrice)
        assertEquals(25.99, newState.newItem.price)
        assertFalse(newState.showItemPriceError)
    }

    @Test
    fun `updatePrice with non-numeric input filters to empty string`() {
        val state = AddItemState()
        val newState = state.updatePrice("abc")

        // filterNumericInputWithMaxDecimals filters out non-digits, resulting in empty string
        assertNotNull(newState)
        assertEquals("", newState.displayPrice)
        assertEquals(0.0, newState.newItem.price)
    }

    @Test
    fun `updatePrice with multiple decimals returns null`() {
        val state = AddItemState()
        val newState = state.updatePrice("10.50.25")

        assertNull(newState)
    }

    @Test
    fun `updatePrice with too many decimal places returns null`() {
        val state = AddItemState()
        val newState = state.updatePrice("10.999")

        assertNull(newState)
    }

    @Test
    fun `updatePrice with empty string returns valid state`() {
        val state = AddItemState(displayPrice = "10.00")
        val newState = state.updatePrice("")

        assertNotNull(newState)
        assertEquals("", newState.displayPrice)
        assertEquals(0.0, newState.newItem.price)
    }

    @Test
    fun `updateVariantKey updates key and clears errors`() {
        val state = AddItemState(showAddOptionError = true, showDuplicateKeyError = true)
        val newState = state.updateVariantKey("Size")

        assertEquals("Size", newState.newItemVariant.key)
        assertFalse(newState.showAddOptionError)
        assertFalse(newState.showDuplicateKeyError)
    }

    @Test
    fun `updateOptionValue updates value and clears error`() {
        val state = AddItemState(showValueError = true)
        val newState = state.updateOptionValue("Large")

        assertEquals("Large", newState.optionValue)
        assertFalse(newState.showValueError)
    }

    // ==================== List Management Functions Tests ====================

    @Test
    fun `reorderVariants updates variants order`() {
        val variant1 = DisplayItem.Variant(key = "Size")
        val variant2 = DisplayItem.Variant(key = "Color")
        val state = AddItemState(
            newItem = DisplayItem(variants = listOf(variant1, variant2))
        )

        val newState = state.reorderVariants(listOf(variant2, variant1))

        assertEquals("Color", newState.newItem.variants[0].key)
        assertEquals("Size", newState.newItem.variants[1].key)
    }

    @Test
    fun `removeVariant removes specified variant`() {
        val variant1 = DisplayItem.Variant(key = "Size")
        val variant2 = DisplayItem.Variant(key = "Color")
        val state = AddItemState(
            newItem = DisplayItem(variants = listOf(variant1, variant2))
        )

        val newState = state.removeVariant(variant1)

        assertEquals(1, newState.newItem.variants.size)
        assertEquals("Color", newState.newItem.variants[0].key)
    }

    @Test
    fun `reorderVariantValues updates values order`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))
        )

        val newState = state.reorderVariantValues(listOf("L", "M", "S"))

        assertEquals(listOf("L", "M", "S"), newState.newItemVariant.valueList)
    }

    @Test
    fun `removeVariantValue removes specified value`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))
        )

        val newState = state.removeVariantValue("M")

        assertEquals(listOf("S", "L"), newState.newItemVariant.valueList)
    }

    // ==================== Navigation Functions Tests ====================

    @Test
    fun `showAddOptionView sets displayAddOptionView to true`() {
        val state = AddItemState()
        val newState = state.showAddOptionView()

        assertTrue(newState.displayAddOptionView)
    }

    @Test
    fun `navigateBack clears add option view and resets variant state`() {
        val state = AddItemState(
            displayAddOptionView = true,
            showAddOptionError = true,
            showValueError = true,
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S")),
            optionValue = "M"
        )

        val newState = state.navigateBack()

        assertFalse(newState.displayAddOptionView)
        assertFalse(newState.showAddOptionError)
        assertFalse(newState.showValueError)
        assertEquals("", newState.newItemVariant.key)
        assertTrue(newState.newItemVariant.valueList.isEmpty())
        assertEquals("", newState.optionValue)
    }

    // ==================== Validation Functions Tests ====================

    @Test
    fun `validateItem returns item when name and price valid`() {
        val state = AddItemState(
            newItem = DisplayItem(name = "Test", price = 10.0)
        )

        val (item, _) = state.validateItem()

        assertNotNull(item)
        assertEquals("Test", item.name)
        assertEquals(10.0, item.price)
    }

    @Test
    fun `validateItem returns null and sets name error when name blank`() {
        val state = AddItemState(
            newItem = DisplayItem(name = "", price = 10.0)
        )

        val (item, newState) = state.validateItem()

        assertNull(item)
        assertTrue(newState.showItemNameError)
        assertFalse(newState.showItemPriceError)
    }

    @Test
    fun `validateItem returns null and sets price error when price zero`() {
        val state = AddItemState(
            newItem = DisplayItem(name = "Test", price = 0.0)
        )

        val (item, newState) = state.validateItem()

        assertNull(item)
        assertFalse(newState.showItemNameError)
        assertTrue(newState.showItemPriceError)
    }

    @Test
    fun `validateItem returns null and sets both errors when both invalid`() {
        val state = AddItemState(
            newItem = DisplayItem(name = "", price = 0.0)
        )

        val (item, newState) = state.validateItem()

        assertNull(item)
        assertTrue(newState.showItemNameError)
        assertTrue(newState.showItemPriceError)
    }

    @Test
    fun `saveVariantAndNavigateBack saves valid variant`() {
        val state = AddItemState(
            displayAddOptionView = true,
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M")),
            optionValue = "L"
        )

        val newState = state.saveVariantAndNavigateBack()

        assertFalse(newState.displayAddOptionView)
        assertEquals(1, newState.newItem.variants.size)
        assertEquals("Size", newState.newItem.variants[0].key)
        assertEquals(listOf("S", "M"), newState.newItem.variants[0].valueList)
        assertEquals("", newState.newItemVariant.key)
        assertEquals("", newState.optionValue)
    }

    @Test
    fun `saveVariantAndNavigateBack does not save invalid variant`() {
        val state = AddItemState(
            displayAddOptionView = true,
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = emptyList())
        )

        val newState = state.saveVariantAndNavigateBack()

        assertFalse(newState.displayAddOptionView)
        assertTrue(newState.newItem.variants.isEmpty())
    }

    @Test
    fun `saveVariantAndNavigateBack clears all error flags`() {
        val state = AddItemState(
            displayAddOptionView = true,
            showAddOptionError = true,
            showValueError = true,
            showDuplicateKeyError = true,
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S"))
        )

        val newState = state.saveVariantAndNavigateBack()

        assertFalse(newState.showAddOptionError)
        assertFalse(newState.showValueError)
        assertFalse(newState.showDuplicateKeyError)
    }

    // ==================== addVariantValue Tests ====================

    @Test
    fun `addVariantValue sets error when key blank`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = ""),
            optionValue = "Large"
        )

        val newState = state.addVariantValue()

        assertTrue(newState.showAddOptionError)
    }

    @Test
    fun `addVariantValue sets duplicate error when key exists`() {
        val state = AddItemState(
            newItem = DisplayItem(
                variants = listOf(DisplayItem.Variant(key = "Size", valueList = listOf("S")))
            ),
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = emptyList()),
            optionValue = "M"
        )

        val newState = state.addVariantValue()

        assertTrue(newState.showDuplicateKeyError)
    }

    @Test
    fun `addVariantValue sets error when value blank`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = "Size"),
            optionValue = ""
        )

        val newState = state.addVariantValue()

        assertTrue(newState.showValueError)
    }

    @Test
    fun `addVariantValue sets error when value duplicate`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S")),
            optionValue = "S"
        )

        val newState = state.addVariantValue()

        assertTrue(newState.showValueError)
    }

    @Test
    fun `addVariantValue adds value successfully`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S")),
            optionValue = "M"
        )

        val newState = state.addVariantValue()

        assertEquals(listOf("S", "M"), newState.newItemVariant.valueList)
        assertEquals("", newState.optionValue)
        assertFalse(newState.showValueError)
        assertFalse(newState.showDuplicateKeyError)
    }

    @Test
    fun `addVariantValue trims whitespace from value`() {
        val state = AddItemState(
            newItemVariant = DisplayItem.Variant(key = "Size"),
            optionValue = "  Large  "
        )

        val newState = state.addVariantValue()

        assertEquals(listOf("Large"), newState.newItemVariant.valueList)
    }
}

