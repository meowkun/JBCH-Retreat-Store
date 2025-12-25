package com.example.jbchretreatstore.bookstore.presentation.ui.shop

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
}

