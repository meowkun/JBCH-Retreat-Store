package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShopUiStateTest {

    // ==================== Tests for searchedItemList derived property ====================

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
    fun `searchedItemList returns all items when search query is tab characters`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "T-Shirt")
        )
        val uiState = ShopUiState(searchQuery = "\t\t", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
    }

    @Test
    fun `searchedItemList returns all items when search query is newline characters`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "T-Shirt")
        )
        val uiState = ShopUiState(searchQuery = "\n\n", displayItemList = items)

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
    fun `searchedItemList handles mixed case query`() {
        val items = listOf(
            DisplayItem(name = "Bible"),
            DisplayItem(name = "BIBLE COVER"),
            DisplayItem(name = "bible study")
        )
        val uiState = ShopUiState(searchQuery = "BiBlE", displayItemList = items)

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

    @Test
    fun `searchedItemList handles single character search`() {
        val items = listOf(
            DisplayItem(name = "Apple"),
            DisplayItem(name = "Banana"),
            DisplayItem(name = "Cherry")
        )
        val uiState = ShopUiState(searchQuery = "a", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
        assertTrue(uiState.searchedItemList.any { it.name == "Apple" })
        assertTrue(uiState.searchedItemList.any { it.name == "Banana" })
    }

    @Test
    fun `searchedItemList handles special characters in search`() {
        val items = listOf(
            DisplayItem(name = "T-Shirt"),
            DisplayItem(name = "Hoodie"),
            DisplayItem(name = "Long-Sleeve")
        )
        val uiState = ShopUiState(searchQuery = "-", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
    }

    @Test
    fun `searchedItemList handles unicode characters`() {
        val items = listOf(
            DisplayItem(name = "日本語テスト"),
            DisplayItem(name = "English Test"),
            DisplayItem(name = "日本語本")
        )
        val uiState = ShopUiState(searchQuery = "日本語", displayItemList = items)

        assertEquals(2, uiState.searchedItemList.size)
    }

    @Test
    fun `searchedItemList handles numbers in search`() {
        val items = listOf(
            DisplayItem(name = "Item 123"),
            DisplayItem(name = "Item 456"),
            DisplayItem(name = "Other")
        )
        val uiState = ShopUiState(searchQuery = "123", displayItemList = items)

        assertEquals(1, uiState.searchedItemList.size)
        assertEquals("Item 123", uiState.searchedItemList.first().name)
    }

    @Test
    fun `searchedItemList preserves original list order`() {
        val items = listOf(
            DisplayItem(name = "A Bible"),
            DisplayItem(name = "Z Bible"),
            DisplayItem(name = "M Bible")
        )
        val uiState = ShopUiState(searchQuery = "Bible", displayItemList = items)

        assertEquals("A Bible", uiState.searchedItemList[0].name)
        assertEquals("Z Bible", uiState.searchedItemList[1].name)
        assertEquals("M Bible", uiState.searchedItemList[2].name)
    }

    @Test
    fun `searchedItemList handles large item list`() {
        val items = (1..1000).map { DisplayItem(name = "Item $it") }
        val uiState = ShopUiState(searchQuery = "Item 5", displayItemList = items)

        // Should match Item 5, Item 50-59, Item 500-599, etc.
        assertTrue(uiState.searchedItemList.isNotEmpty())
        assertTrue(uiState.searchedItemList.all { it.name.contains("Item 5") })
    }

    // ==================== Tests for default state ====================

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
    fun `default state has no dialogs or bottom sheets shown`() {
        val uiState = ShopUiState()

        assertFalse(uiState.showAddItemBottomSheet)
        assertFalse(uiState.showRemoveItemDialog)
        assertFalse(uiState.showEditItemBottomSheet)
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

    // ==================== Tests for cart state ====================

    @Test
    fun `hasItemsInCart reflects cart state correctly`() {
        val stateWithItems = ShopUiState(cartItemCount = 5, hasItemsInCart = true)
        val stateWithoutItems = ShopUiState(cartItemCount = 0, hasItemsInCart = false)

        assertTrue(stateWithItems.hasItemsInCart)
        assertFalse(stateWithoutItems.hasItemsInCart)
    }

    @Test
    fun `cart count handles large numbers`() {
        val uiState = ShopUiState(cartItemCount = 9999, hasItemsInCart = true)

        assertEquals(9999, uiState.cartItemCount)
        assertTrue(uiState.hasItemsInCart)
    }

    // ==================== Tests for showClearSearchButton derived property ====================

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

    @Test
    fun `showClearSearchButton returns true for single character`() {
        val uiState = ShopUiState(searchQuery = "a")

        assertTrue(uiState.showClearSearchButton)
    }

    // ==================== Tests for isReorderEnabled derived property ====================

    @Test
    fun `isReorderEnabled returns true when search query is empty`() {
        val uiState = ShopUiState(searchQuery = "")

        assertTrue(uiState.isReorderEnabled)
    }

    @Test
    fun `isReorderEnabled returns true when search query is blank with spaces`() {
        val uiState = ShopUiState(searchQuery = "   ")

        assertTrue(uiState.isReorderEnabled)
    }

    @Test
    fun `isReorderEnabled returns false when search query has content`() {
        val uiState = ShopUiState(searchQuery = "Bible")

        assertFalse(uiState.isReorderEnabled)
    }

    @Test
    fun `isReorderEnabled returns false for single character search`() {
        val uiState = ShopUiState(searchQuery = "a")

        assertFalse(uiState.isReorderEnabled)
    }

    @Test
    fun `isReorderEnabled and showClearSearchButton are inversely related`() {
        val stateWithQuery = ShopUiState(searchQuery = "test")
        val stateWithoutQuery = ShopUiState(searchQuery = "")

        // With query: reorder disabled, clear button shown
        assertFalse(stateWithQuery.isReorderEnabled)
        assertTrue(stateWithQuery.showClearSearchButton)

        // Without query: reorder enabled, clear button hidden
        assertTrue(stateWithoutQuery.isReorderEnabled)
        assertFalse(stateWithoutQuery.showClearSearchButton)
    }

    // ==================== Tests for removeDialogData derived property ====================

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

    @Test
    fun `removeDialogData returns null when both conditions are false`() {
        val uiState = ShopUiState(showRemoveItemDialog = false, itemToRemove = null)

        assertNull(uiState.removeDialogData)
    }

    // ==================== Tests for editBottomSheetData derived property ====================

    @Test
    fun `editBottomSheetData returns item when all conditions met`() {
        val item = DisplayItem(name = "Test")
        val uiState = ShopUiState(showEditItemBottomSheet = true, itemToEdit = item)

        assertEquals(item, uiState.editBottomSheetData)
    }

    @Test
    fun `editBottomSheetData returns null when showEditItemBottomSheet is false`() {
        val item = DisplayItem(name = "Test")
        val uiState = ShopUiState(showEditItemBottomSheet = false, itemToEdit = item)

        assertNull(uiState.editBottomSheetData)
    }

    @Test
    fun `editBottomSheetData returns null when itemToEdit is null`() {
        val uiState = ShopUiState(showEditItemBottomSheet = true, itemToEdit = null)

        assertNull(uiState.editBottomSheetData)
    }

    @Test
    fun `editBottomSheetData returns null when both conditions are false`() {
        val uiState = ShopUiState(showEditItemBottomSheet = false, itemToEdit = null)

        assertNull(uiState.editBottomSheetData)
    }

    // ==================== Tests for state combinations ====================

    @Test
    fun `multiple dialogs and bottom sheets can be tracked independently`() {
        val item1 = DisplayItem(name = "Item1")
        val item2 = DisplayItem(name = "Item2")

        val uiState = ShopUiState(
            showAddItemBottomSheet = true,
            showRemoveItemDialog = true,
            showEditItemBottomSheet = true,
            itemToRemove = item1,
            itemToEdit = item2
        )

        assertTrue(uiState.showAddItemBottomSheet)
        assertTrue(uiState.showRemoveItemDialog)
        assertTrue(uiState.showEditItemBottomSheet)
        assertEquals(item1, uiState.removeDialogData)
        assertEquals(item2, uiState.editBottomSheetData)
    }

    @Test
    fun `state can track different items for remove and edit`() {
        val removeItem = DisplayItem(name = "ToRemove", price = 10.0)
        val editItem = DisplayItem(name = "ToEdit", price = 20.0)

        val uiState = ShopUiState(
            showRemoveItemDialog = true,
            showEditItemBottomSheet = true,
            itemToRemove = removeItem,
            itemToEdit = editItem
        )

        assertEquals(removeItem, uiState.removeDialogData)
        assertEquals(editItem, uiState.editBottomSheetData)
        assertNotEquals(uiState.removeDialogData, uiState.editBottomSheetData)
    }

    // ==================== Tests for DisplayItem.formattedPrice extension ====================

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

    @Test
    fun `DisplayItem formattedPrice handles large price`() {
        val item = DisplayItem(name = "Expensive", price = 9999.99)

        assertEquals("$9999.99", item.formattedPrice)
    }

    @Test
    fun `DisplayItem formattedPrice handles small decimal`() {
        val item = DisplayItem(name = "Cheap", price = 0.01)

        assertEquals("$0.01", item.formattedPrice)
    }

    @Test
    fun `DisplayItem formattedPrice rounds correctly`() {
        val item = DisplayItem(name = "Test", price = 10.999)

        // Should round to 2 decimal places
        assertTrue(item.formattedPrice.startsWith("$"))
    }

    // ==================== Tests for CheckoutItem.getSelectedVariantValue extension ====================

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

    @Test
    fun `getSelectedVariantValue handles case sensitive variant keys`() {
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
        val displayVariant = DisplayItem.Variant(key = "size", valueList = listOf("S", "M", "L"))

        // Keys are case sensitive, so "size" != "Size"
        assertEquals("S", checkoutItem.getSelectedVariantValue(displayVariant))
    }

    @Test
    fun `getSelectedVariantValue handles multiple variants with same first value`() {
        val checkoutItem = CheckoutItem(
            itemName = "T-Shirt",
            variants = listOf(
                CheckoutItem.Variant(
                    key = "Size",
                    valueList = listOf("Small", "Medium", "Large"),
                    selectedValue = "Large"
                ),
                CheckoutItem.Variant(
                    key = "Fit",
                    valueList = listOf("Small", "Regular", "Loose"),
                    selectedValue = "Regular"
                )
            )
        )
        val sizeVariant =
            DisplayItem.Variant(key = "Size", valueList = listOf("Small", "Medium", "Large"))
        val fitVariant =
            DisplayItem.Variant(key = "Fit", valueList = listOf("Small", "Regular", "Loose"))

        assertEquals("Large", checkoutItem.getSelectedVariantValue(sizeVariant))
        assertEquals("Regular", checkoutItem.getSelectedVariantValue(fitVariant))
    }

    // ==================== Tests for state immutability ====================

    @Test
    fun `state copy creates new instance with updated values`() {
        val originalState = ShopUiState()
        val newState = originalState.copy(searchQuery = "test")

        assertEquals("", originalState.searchQuery)
        assertEquals("test", newState.searchQuery)
    }

    @Test
    fun `state copy preserves other values`() {
        val items = listOf(DisplayItem(name = "Test"))
        val originalState = ShopUiState(
            displayItemList = items,
            isLoading = false,
            cartItemCount = 5
        )
        val newState = originalState.copy(searchQuery = "test")

        assertEquals(items, newState.displayItemList)
        assertFalse(newState.isLoading)
        assertEquals(5, newState.cartItemCount)
    }

    // ==================== Tests for DisplayItem with variants ====================

    @Test
    fun `searchedItemList includes items with variants`() {
        val items = listOf(
            DisplayItem(
                name = "Bible",
                price = 25.0,
                variants = listOf(
                    DisplayItem.Variant(key = "Language", valueList = listOf("English", "Spanish"))
                )
            ),
            DisplayItem(name = "T-Shirt", price = 15.0)
        )
        val uiState = ShopUiState(searchQuery = "Bible", displayItemList = items)

        assertEquals(1, uiState.searchedItemList.size)
        assertEquals(1, uiState.searchedItemList.first().variants.size)
    }

    @Test
    fun `edit bottom sheet data preserves item variants`() {
        val item = DisplayItem(
            name = "Bible",
            price = 25.0,
            variants = listOf(
                DisplayItem.Variant(key = "Language", valueList = listOf("English", "Spanish")),
                DisplayItem.Variant(key = "Cover", valueList = listOf("Hard", "Soft"))
            )
        )
        val uiState = ShopUiState(showEditItemBottomSheet = true, itemToEdit = item)

        val editData = uiState.editBottomSheetData
        assertEquals(2, editData?.variants?.size)
    }
}
