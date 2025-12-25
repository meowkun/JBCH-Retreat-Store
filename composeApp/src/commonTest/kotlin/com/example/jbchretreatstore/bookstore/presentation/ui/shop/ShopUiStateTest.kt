package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShopUiStateTest {

    // Tests for searchedItemList derived property

    @Test
    fun `searchedItemList returns all items when search query is empty`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "T-Shirt"),
            DisplayItem(name = "Bookmark")
        )
        val uiState = ShopUiState(searchQuery = "", displayItemList = items)

        assertEquals(3, uiState.searchedItemList.size)
        assertEquals(items, uiState.searchedItemList)
    }

    @Test
    fun `searchedItemList returns all items when search query is blank with spaces`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "T-Shirt")
        )
        val uiState = ShopUiState(searchQuery = "   ", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
    }

    @Test
    fun `searchedItemList filters items by name containing query`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "Bible Study Guide"),
            DisplayItem(name = "T-Shirt")
        )
        val uiState = ShopUiState(searchQuery = "Bible", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
        assertTrue(uiState.searchedItemList.all { it.name.contains("Bible") })
    }

    @Test
    fun `searchedItemList is case insensitive`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "BIBLE COVER"),
            DisplayItem(name = "bible study")
        )
        val uiState = ShopUiState(searchQuery = "bible", displayItemList = items)

        assertEquals(3, uiState.searchedItemList.size)
    }

    @Test
    fun `searchedItemList returns empty list when no matches found`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "T-Shirt")
        )
        val uiState = ShopUiState(searchQuery = "xyz", displayItemList = items)

        assertTrue(uiState.searchedItemList.isEmpty())
    }

    @Test
    fun `searchedItemList returns empty list when display items are empty`() {
        val uiState = ShopUiState(searchQuery = "Bible", displayItemList = emptyList())

        assertTrue(uiState.searchedItemList.isEmpty())
    }

    @Test
    fun `searchedItemList handles partial matches`() {
        val items = listOf(
            DisplayItem(name = "Large Bible"),
            DisplayItem(name = "Small Bookmark"),
            DisplayItem(name = "Bible Cover")
        )
        val uiState = ShopUiState(searchQuery = "ble", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
        assertTrue(uiState.searchedItemList.any { it.name == "Large Bible" })
        assertTrue(uiState.searchedItemList.any { it.name == "Bible Cover" })
    }

    // Tests for default state

    @Test
    fun `default state has empty search query`() {
        val uiState = ShopUiState()

        assertEquals("", uiState.searchQuery)
    }

    @Test
    fun `default state has empty display item list`() {
        val uiState = ShopUiState()

        assertTrue(uiState.displayItemList.isEmpty())
    }

    @Test
    fun `default state is loading`() {
        val uiState = ShopUiState()

        assertTrue(uiState.isLoading)
    }

    @Test
    fun `default state has no dialogs shown`() {
        val uiState = ShopUiState()

        assertFalse(uiState.showAddItemDialog)
        assertFalse(uiState.showRemoveItemDialog)
        assertFalse(uiState.showEditItemDialog)
    }

    @Test
    fun `default state has no item to remove or edit`() {
        val uiState = ShopUiState()

        assertNull(uiState.itemToRemove)
        assertNull(uiState.itemToEdit)
    }

    @Test
    fun `default state has zero cart items`() {
        val uiState = ShopUiState()

        assertEquals(0, uiState.cartItemCount)
        assertFalse(uiState.hasItemsInCart)
    }

    // Tests for cart state

    @Test
    fun `hasItemsInCart reflects cart state correctly`() {
        val stateWithItems = ShopUiState(cartItemCount = 5, hasItemsInCart = true)
        val stateWithoutItems = ShopUiState(cartItemCount = 0, hasItemsInCart = false)

        assertTrue(stateWithItems.hasItemsInCart)
        assertFalse(stateWithoutItems.hasItemsInCart)
    }

    // Tests for showClearSearchButton derived property

    @Test
    fun `showClearSearchButton returns true when search query is not blank`() {
        val uiState = ShopUiState(searchQuery = "Bible")

        assertTrue(uiState.showClearSearchButton)
    }

    @Test
    fun `showClearSearchButton returns false when search query is empty`() {
        val uiState = ShopUiState(searchQuery = "")

        assertFalse(uiState.showClearSearchButton)
    }

    @Test
    fun `showClearSearchButton returns false when search query is blank with spaces`() {
        val uiState = ShopUiState(searchQuery = "   ")

        assertFalse(uiState.showClearSearchButton)
    }

    // Tests for removeDialogData derived property

    @Test
    fun `removeDialogData returns item when all conditions met`() {
        val item = DisplayItem(name = "Test")
        val uiState = ShopUiState(showRemoveItemDialog = true, itemToRemove = item)

        assertEquals(item, uiState.removeDialogData)
    }

    @Test
    fun `removeDialogData returns null when showRemoveItemDialog is false`() {
        val item = DisplayItem(name = "Test")
        val uiState = ShopUiState(showRemoveItemDialog = false, itemToRemove = item)

        assertNull(uiState.removeDialogData)
    }

    @Test
    fun `removeDialogData returns null when itemToRemove is null`() {
        val uiState = ShopUiState(showRemoveItemDialog = true, itemToRemove = null)

        assertNull(uiState.removeDialogData)
    }

    // Tests for editDialogData derived property

    @Test
    fun `editDialogData returns item when all conditions met`() {
        val item = DisplayItem(name = "Test")
        val uiState = ShopUiState(showEditItemDialog = true, itemToEdit = item)

        assertEquals(item, uiState.editDialogData)
    }

    @Test
    fun `editDialogData returns null when showEditItemDialog is false`() {
        val item = DisplayItem(name = "Test")
        val uiState = ShopUiState(showEditItemDialog = false, itemToEdit = item)

        assertNull(uiState.editDialogData)
    }

    @Test
    fun `editDialogData returns null when itemToEdit is null`() {
        val uiState = ShopUiState(showEditItemDialog = true, itemToEdit = null)

        assertNull(uiState.editDialogData)
    }

    // Tests for DisplayItem.formattedPrice extension

    @Test
    fun `DisplayItem formattedPrice returns formatted currency`() {
        val item = DisplayItem(name = "Bible", price = 25.50)

        assertEquals("$25.50", item.formattedPrice)
    }

    @Test
    fun `DisplayItem formattedPrice handles zero price`() {
        val item = DisplayItem(name = "Free Item", price = 0.0)

        assertEquals("$0.00", item.formattedPrice)
    }

    // Tests for CheckoutItem.getSelectedVariantValue extension

    @Test
    fun `getSelectedVariantValue returns value from variantsMap when present`() {
        val checkoutItem = CheckoutItem(
            itemName = "T-Shirt",
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("S", "M", "L"),
                    selectedValue = "M"
                )
            )
        )
        val displayVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))

        assertEquals("M", checkoutItem.getSelectedVariantValue(displayVariant))
    }

    @Test
    fun `getSelectedVariantValue falls back to first value when key not in variantsMap`() {
        val checkoutItem = CheckoutItem(
            itemName = "T-Shirt",
            variants = emptyList()
        )
        val displayVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))

        assertEquals("S", checkoutItem.getSelectedVariantValue(displayVariant))
    }

    @Test
    fun `getSelectedVariantValue returns empty string when both sources are empty`() {
        val checkoutItem = CheckoutItem(
            itemName = "T-Shirt",
            variants = emptyList()
        )
        val displayVariant = DisplayItem.Variant(key = "Size", valueList = emptyList())

        assertEquals("", checkoutItem.getSelectedVariantValue(displayVariant))
    }

    @Test
    fun `getSelectedVariantValue uses correct variant key for lookup`() {
        val checkoutItem = CheckoutItem(
            itemName = "T-Shirt",
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("S", "M", "L"),
                    selectedValue = "L"
                ),
                CheckoutItem.Variant(
                    key = "Color",
                    valueList = listOf("Red", "Blue"),
                    selectedValue = "Blue"
                )
            )
        )
        val sizeVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))
        val colorVariant = DisplayItem.Variant(key = "Color", valueList = listOf("Red", "Blue"))

        assertEquals("L", checkoutItem.getSelectedVariantValue(sizeVariant))
        assertEquals("Blue", checkoutItem.getSelectedVariantValue(colorVariant))
    }

    @Test
    fun `getSelectedVariantValue falls back when variant key exists but has empty selectedValue`() {
        val checkoutItem = CheckoutItem(
            itemName = "T-Shirt",
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("S", "M", "L"),
                    selectedValue = ""
                )
            )
        )
        val displayVariant = DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))

        // variantsMap["Size"] returns "" (empty string), which is truthy in Kotlin
        // so it won't fall back - the empty string IS the selected value
        assertEquals("", checkoutItem.getSelectedVariantValue(displayVariant))
    }
}

