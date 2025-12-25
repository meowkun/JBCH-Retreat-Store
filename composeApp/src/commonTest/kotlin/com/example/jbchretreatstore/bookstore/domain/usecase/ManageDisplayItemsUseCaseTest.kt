package com.example.jbchretreatstore.bookstore.domain.usecase

import app.cash.turbine.test
import com.example.jbchretreatstore.bookstore.domain.constants.ErrorMessages
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.fake.FakeBookStoreRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ManageDisplayItemsUseCaseTest {

    private lateinit var repository: FakeBookStoreRepository
    private lateinit var useCase: ManageDisplayItemsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeBookStoreRepository()
        useCase = ManageDisplayItemsUseCase(repository)
    }

    // ============= GET DISPLAY ITEMS TESTS =============

    @Test
    fun `getDisplayItems returns flow of items from repository`() = runTest {
        val items = listOf(
            DisplayItem(name = "Book 1", price = 10.0),
            DisplayItem(name = "Book 2", price = 20.0)
        )
        repository.setDisplayItems(items)

        useCase.getDisplayItems().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Book 1", result[0].name)
            assertEquals("Book 2", result[1].name)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getDisplayItems returns empty list when no items`() = runTest {
        useCase.getDisplayItems().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= ADD DISPLAY ITEM TESTS =============

    @Test
    fun `addDisplayItem succeeds with valid item`() = runTest {
        val newItem = DisplayItem(name = "New Book", price = 15.99)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertTrue(repository.updateDisplayItemsCalled)
        assertEquals(1, repository.lastSavedDisplayItems?.size)
    }

    @Test
    fun `addDisplayItem fails with empty name`() = runTest {
        val newItem = DisplayItem(name = "", price = 10.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(ErrorMessages.ITEM_NAME_EMPTY, result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem fails with blank name`() = runTest {
        val newItem = DisplayItem(name = "   ", price = 10.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isFailure)
        assertEquals(ErrorMessages.ITEM_NAME_EMPTY, result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem fails with zero price`() = runTest {
        val newItem = DisplayItem(name = "Test", price = 0.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isFailure)
        assertEquals(ErrorMessages.ITEM_PRICE_INVALID, result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem fails with negative price`() = runTest {
        val newItem = DisplayItem(name = "Test", price = -5.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isFailure)
        assertEquals(ErrorMessages.ITEM_PRICE_INVALID, result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem fails with duplicate name case insensitive`() = runTest {
        val existingItem = DisplayItem(name = "Bible", price = 20.0)
        repository.setDisplayItems(listOf(existingItem))

        val duplicateItem = DisplayItem(name = "BIBLE", price = 25.0)

        val result = useCase.addDisplayItem(duplicateItem)

        assertTrue(result.isFailure)
        assertEquals("Item with name 'BIBLE' already exists", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem fails with duplicate name exact match`() = runTest {
        val existingItem = DisplayItem(name = "Bible", price = 20.0)
        repository.setDisplayItems(listOf(existingItem))

        val duplicateItem = DisplayItem(name = "Bible", price = 25.0)

        val result = useCase.addDisplayItem(duplicateItem)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun `addDisplayItem appends to existing items`() = runTest {
        val existingItem = DisplayItem(name = "Existing", price = 10.0)
        repository.setDisplayItems(listOf(existingItem))

        val newItem = DisplayItem(name = "New", price = 20.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals(2, repository.lastSavedDisplayItems?.size)
    }

    @Test
    fun `addDisplayItem with variants`() = runTest {
        val variants = listOf(
            DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L")),
            DisplayItem.Variant(key = "Color", valueList = listOf("Red", "Blue"))
        )
        val newItem = DisplayItem(name = "T-Shirt", price = 25.0, variants = variants)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals(2, repository.lastSavedDisplayItems?.first()?.variants?.size)
    }

    @Test
    fun `addDisplayItem with special characters in name`() = runTest {
        val newItem = DisplayItem(name = "Book: 50% Off! @Special", price = 10.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals("Book: 50% Off! @Special", repository.lastSavedDisplayItems?.first()?.name)
    }

    @Test
    fun `addDisplayItem with unicode name`() = runTest {
        val newItem = DisplayItem(name = "经书 📚", price = 15.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals("经书 📚", repository.lastSavedDisplayItems?.first()?.name)
    }

    // ============= REMOVE DISPLAY ITEM TESTS =============

    @Test
    fun `removeDisplayItem succeeds with existing item`() = runTest {
        val itemId = Uuid.random()
        val item = DisplayItem(id = itemId, name = "Test", price = 10.0)
        repository.setDisplayItems(listOf(item))

        val result = useCase.removeDisplayItem(item)

        assertTrue(result.isSuccess)
        assertTrue(repository.lastSavedDisplayItems?.isEmpty() == true)
    }

    @Test
    fun `removeDisplayItem fails when item not found`() = runTest {
        val existingItem = DisplayItem(name = "Existing", price = 10.0)
        repository.setDisplayItems(listOf(existingItem))

        val nonExistentItem = DisplayItem(id = Uuid.random(), name = "Non-existent", price = 10.0)

        val result = useCase.removeDisplayItem(nonExistentItem)

        assertTrue(result.isFailure)
        assertEquals(ErrorMessages.ITEM_NOT_FOUND, result.exceptionOrNull()?.message)
    }

    @Test
    fun `removeDisplayItem removes only specified item`() = runTest {
        val itemToRemove = DisplayItem(name = "Remove", price = 10.0)
        val itemToKeep = DisplayItem(name = "Keep", price = 20.0)
        repository.setDisplayItems(listOf(itemToRemove, itemToKeep))

        val result = useCase.removeDisplayItem(itemToRemove)

        assertTrue(result.isSuccess)
        assertEquals(1, repository.lastSavedDisplayItems?.size)
        assertEquals("Keep", repository.lastSavedDisplayItems?.first()?.name)
    }

    @Test
    fun `removeDisplayItem on empty repository fails`() = runTest {
        val item = DisplayItem(name = "Test", price = 10.0)

        val result = useCase.removeDisplayItem(item)

        assertTrue(result.isFailure)
    }

    // ============= UPDATE DISPLAY ITEM TESTS =============

    @Test
    fun `updateDisplayItem succeeds with existing item`() = runTest {
        val itemId = Uuid.random()
        val originalItem = DisplayItem(id = itemId, name = "Original", price = 10.0)
        repository.setDisplayItems(listOf(originalItem))

        val updatedItem = DisplayItem(id = itemId, name = "Updated", price = 15.0)

        val result = useCase.updateDisplayItem(updatedItem)

        assertTrue(result.isSuccess)
        assertEquals("Updated", repository.lastSavedDisplayItems?.first()?.name)
        assertEquals(15.0, repository.lastSavedDisplayItems?.first()?.price)
    }

    @Test
    fun `updateDisplayItem fails when item not found`() = runTest {
        val existingItem = DisplayItem(name = "Existing", price = 10.0)
        repository.setDisplayItems(listOf(existingItem))

        val nonExistentItem = DisplayItem(id = Uuid.random(), name = "Non-existent", price = 10.0)

        val result = useCase.updateDisplayItem(nonExistentItem)

        assertTrue(result.isFailure)
        assertEquals(ErrorMessages.ITEM_NOT_FOUND, result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateDisplayItem preserves other items`() = runTest {
        val item1 = DisplayItem(name = "Item 1", price = 10.0)
        val item2 = DisplayItem(name = "Item 2", price = 20.0)
        repository.setDisplayItems(listOf(item1, item2))

        val updatedItem1 = item1.copy(name = "Updated Item 1")

        val result = useCase.updateDisplayItem(updatedItem1)

        assertTrue(result.isSuccess)
        assertEquals(2, repository.lastSavedDisplayItems?.size)
        assertEquals("Updated Item 1", repository.lastSavedDisplayItems?.first()?.name)
        assertEquals("Item 2", repository.lastSavedDisplayItems?.get(1)?.name)
    }

    @Test
    fun `updateDisplayItem can update variants`() = runTest {
        val itemId = Uuid.random()
        val originalItem =
            DisplayItem(id = itemId, name = "T-Shirt", price = 25.0, variants = emptyList())
        repository.setDisplayItems(listOf(originalItem))

        val newVariants = listOf(
            DisplayItem.Variant(key = "Size", valueList = listOf("S", "M", "L"))
        )
        val updatedItem = originalItem.copy(variants = newVariants)

        val result = useCase.updateDisplayItem(updatedItem)

        assertTrue(result.isSuccess)
        assertEquals(1, repository.lastSavedDisplayItems?.first()?.variants?.size)
    }

    @Test
    fun `updateDisplayItem can toggle isInCart`() = runTest {
        val itemId = Uuid.random()
        val item = DisplayItem(id = itemId, name = "Test", price = 10.0, isInCart = false)
        repository.setDisplayItems(listOf(item))

        val updatedItem = item.copy(isInCart = true)

        val result = useCase.updateDisplayItem(updatedItem)

        assertTrue(result.isSuccess)
        assertTrue(repository.lastSavedDisplayItems?.first()?.isInCart == true)
    }

    // ============= SEARCH ITEMS TESTS =============

    @Test
    fun `searchItems returns all items when query is empty`() = runTest {
        val items = listOf(
            DisplayItem(name = "Bible", price = 20.0),
            DisplayItem(name = "Hymnal", price = 15.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("")

        assertEquals(2, result.size)
    }

    @Test
    fun `searchItems returns all items when query is blank`() = runTest {
        val items = listOf(
            DisplayItem(name = "Bible", price = 20.0),
            DisplayItem(name = "Hymnal", price = 15.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("   ")

        assertEquals(2, result.size)
    }

    @Test
    fun `searchItems finds items by partial name match`() = runTest {
        val items = listOf(
            DisplayItem(name = "Holy Bible", price = 20.0),
            DisplayItem(name = "Bible Study Guide", price = 15.0),
            DisplayItem(name = "Hymnal", price = 10.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("Bible")

        assertEquals(2, result.size)
    }

    @Test
    fun `searchItems is case insensitive`() = runTest {
        val items = listOf(
            DisplayItem(name = "Holy Bible", price = 20.0),
            DisplayItem(name = "Hymnal", price = 10.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("BIBLE")

        assertEquals(1, result.size)
        assertEquals("Holy Bible", result.first().name)
    }

    @Test
    fun `searchItems returns empty list when no matches`() = runTest {
        val items = listOf(
            DisplayItem(name = "Bible", price = 20.0),
            DisplayItem(name = "Hymnal", price = 15.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("Nonexistent")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchItems with special characters`() = runTest {
        val items = listOf(
            DisplayItem(name = "Book: 50% Off!", price = 10.0),
            DisplayItem(name = "Regular Book", price = 20.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("50%")

        assertEquals(1, result.size)
        assertEquals("Book: 50% Off!", result.first().name)
    }

    @Test
    fun `searchItems finds items with unicode characters`() = runTest {
        val items = listOf(
            DisplayItem(name = "经书 Scripture", price = 15.0),
            DisplayItem(name = "Book", price = 20.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("经书")

        assertEquals(1, result.size)
    }

    @Test
    fun `searchItems with single character query`() = runTest {
        val items = listOf(
            DisplayItem(name = "Bible", price = 20.0),
            DisplayItem(name = "Book", price = 15.0),
            DisplayItem(name = "Card", price = 5.0)
        )
        repository.setDisplayItems(items)

        val result = useCase.searchItems("B")

        assertEquals(2, result.size) // Bible, Book
    }

    // ============= EDGE CASES =============

    @Test
    fun `operations on large item list`() = runTest {
        val items = (1..1000).map { i ->
            DisplayItem(name = "Item $i", price = i.toDouble())
        }
        repository.setDisplayItems(items)

        val searchResult = useCase.searchItems("Item 500")

        assertEquals(1, searchResult.size)
    }

    @Test
    fun `addDisplayItem with very long name`() = runTest {
        val longName = "A".repeat(1000)
        val newItem = DisplayItem(name = longName, price = 10.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals(1000, repository.lastSavedDisplayItems?.first()?.name?.length)
    }

    @Test
    fun `addDisplayItem with very high price`() = runTest {
        val newItem = DisplayItem(name = "Expensive Item", price = 999999999.99)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals(
            999999999.99,
            repository.lastSavedDisplayItems?.first()?.price ?: 0.00, 0.01
        )
    }

    @Test
    fun `addDisplayItem with many variants`() = runTest {
        val variants = (1..50).map { i ->
            DisplayItem.Variant(key = "Variant$i", valueList = (1..10).map { "Value$it" })
        }
        val newItem = DisplayItem(name = "Complex Item", price = 50.0, variants = variants)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals(50, repository.lastSavedDisplayItems?.first()?.variants?.size)
    }

    // ============= CLEAR ALL ITEMS TESTS =============

    @Test
    fun `clearAllItems removes all items`() = runTest {
        val items = listOf(
            DisplayItem(name = "Item 1", price = 10.0),
            DisplayItem(name = "Item 2", price = 20.0),
            DisplayItem(name = "Item 3", price = 30.0)
        )
        repository.setDisplayItems(items)

        useCase.clearAllItems()

        assertTrue(repository.lastSavedDisplayItems?.isEmpty() == true)
    }

    @Test
    fun `clearAllItems on empty repository`() = runTest {
        useCase.clearAllItems()

        assertTrue(repository.lastSavedDisplayItems?.isEmpty() == true)
    }

    // ============= LOAD TEST DATA IF NEEDED TESTS =============

    @Test
    fun `loadTestDataIfNeeded loads data when not previously loaded`() = runTest {
        val loaded = useCase.loadTestDataIfNeeded()

        assertTrue(loaded)
        assertTrue(repository.updateDisplayItemsCalled)
    }

    @Test
    fun `loadTestDataIfNeeded does not load when already loaded`() = runTest {
        // First load
        val firstResult = useCase.loadTestDataIfNeeded()
        assertTrue(firstResult)

        // Second load - should not reload since flag is set
        val loaded = useCase.loadTestDataIfNeeded()

        assertFalse(loaded)
    }

    @Test
    fun `loadTestDataIfNeeded sets testDataLoaded flag`() = runTest {
        useCase.loadTestDataIfNeeded()

        assertTrue(repository.isTestDataLoaded())
    }

    // ============= FLOW REACTIVITY TESTS =============

    @Test
    fun `getDisplayItems updates when repository changes`() = runTest {
        useCase.getDisplayItems().test {
            // Initial empty state
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            // Add items
            val newItems = listOf(
                DisplayItem(name = "New Item", price = 15.0)
            )
            repository.setDisplayItems(newItems)

            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals("New Item", updated.first().name)

            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= CONCURRENT OPERATIONS TESTS =============

    @Test
    fun `multiple addDisplayItem operations preserve order`() = runTest {
        val items = listOf("Alpha", "Beta", "Gamma", "Delta")

        items.forEach { name ->
            useCase.addDisplayItem(DisplayItem(name = name, price = 10.0))
        }

        val savedItems = repository.lastSavedDisplayItems
        assertEquals(4, savedItems?.size)
        assertEquals("Alpha", savedItems?.get(0)?.name)
        assertEquals("Beta", savedItems?.get(1)?.name)
        assertEquals("Gamma", savedItems?.get(2)?.name)
        assertEquals("Delta", savedItems?.get(3)?.name)
    }

    @Test
    fun `addDisplayItem then removeDisplayItem works correctly`() = runTest {
        val item = DisplayItem(name = "Temporary Item", price = 15.0)
        useCase.addDisplayItem(item)

        val addedItem = repository.lastSavedDisplayItems?.first()!!
        useCase.removeDisplayItem(addedItem)

        assertTrue(repository.lastSavedDisplayItems?.isEmpty() == true)
    }

    @Test
    fun `updateDisplayItem changes are reflected in getDisplayItems`() = runTest {
        val item = DisplayItem(name = "Original", price = 10.0)
        repository.setDisplayItems(listOf(item))

        useCase.getDisplayItems().test {
            val initial = awaitItem()
            assertEquals("Original", initial.first().name)

            val updated = item.copy(name = "Updated")
            useCase.updateDisplayItem(updated)

            val afterUpdate = awaitItem()
            assertEquals("Updated", afterUpdate.first().name)

            cancelAndConsumeRemainingEvents()
        }
    }

    // ============= VALIDATION EDGE CASES =============

    @Test
    fun `addDisplayItem with price at precision limit`() = runTest {
        val newItem = DisplayItem(name = "Precise Price", price = 0.01)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
        assertEquals(0.01, repository.lastSavedDisplayItems?.first()?.price ?: 0.0, 0.001)
    }

    @Test
    fun `addDisplayItem with name containing only whitespace and letters`() = runTest {
        val newItem = DisplayItem(name = "  Valid Name  ", price = 10.0)

        val result = useCase.addDisplayItem(newItem)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateDisplayItem preserves ID even when all other fields change`() = runTest {
        val originalId = Uuid.random()
        val original = DisplayItem(
            id = originalId,
            name = "Original",
            price = 10.0,
            variants = emptyList(),
            isInCart = false
        )
        repository.setDisplayItems(listOf(original))

        val updated = DisplayItem(
            id = originalId,
            name = "Completely Different",
            price = 999.99,
            variants = listOf(DisplayItem.Variant(key = "Size", valueList = listOf("XL"))),
            isInCart = true
        )

        val result = useCase.updateDisplayItem(updated)

        assertTrue(result.isSuccess)
        val saved = repository.lastSavedDisplayItems?.first()
        assertEquals(originalId, saved?.id)
        assertEquals("Completely Different", saved?.name)
        assertEquals(999.99, saved?.price ?: 0.0, 0.01)
        assertEquals(1, saved?.variants?.size)
        assertTrue(saved?.isInCart == true)
    }
}

