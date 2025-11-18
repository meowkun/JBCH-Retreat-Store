package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ManageDisplayItemsUseCaseTest {

    private lateinit var repository: DisplayItemsFakeRepository
    private lateinit var useCase: ManageDisplayItemsUseCase

    @BeforeTest
    fun setup() {
        repository = DisplayItemsFakeRepository()
        useCase = ManageDisplayItemsUseCase(repository)
    }

    @Test
    fun `getDisplayItems should return flow of items`() = runTest {
        // Given
        val items = listOf(
            DisplayItem(id = Uuid.random(), name = "Item 1", price = 10.0, options = emptyList(), isInCart = false),
            DisplayItem(id = Uuid.random(), name = "Item 2", price = 20.0, options = emptyList(), isInCart = false)
        )
        repository.seedDisplayItems(items)

        // When
        val result = useCase.getDisplayItems().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Item 1", result[0].name)
        assertEquals("Item 2", result[1].name)
    }

    @Test
    fun `addDisplayItem with valid item should succeed`() = runTest {
        // Given
        val newItem = DisplayItem(
            id = Uuid.random(),
            name = "New Item",
            price = 15.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.addDisplayItem(newItem)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, repository.displayItems.size)
        assertEquals("New Item", repository.displayItems[0].name)
    }

    @Test
    fun `addDisplayItem with empty name should fail`() = runTest {
        // Given
        val invalidItem = DisplayItem(
            id = Uuid.random(),
            name = "",
            price = 15.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.addDisplayItem(invalidItem)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with blank name should fail`() = runTest {
        // Given
        val invalidItem = DisplayItem(
            id = Uuid.random(),
            name = "   ",
            price = 15.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.addDisplayItem(invalidItem)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with zero price should fail`() = runTest {
        // Given
        val invalidItem = DisplayItem(
            id = Uuid.random(),
            name = "Item",
            price = 0.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.addDisplayItem(invalidItem)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with negative price should fail`() = runTest {
        // Given
        val invalidItem = DisplayItem(
            id = Uuid.random(),
            name = "Item",
            price = -10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.addDisplayItem(invalidItem)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item price must be greater than zero", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addDisplayItem with duplicate name should fail case insensitive`() = runTest {
        // Given
        val existingItem = DisplayItem(
            id = Uuid.random(),
            name = "Existing Item",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )
        repository.seedDisplayItems(listOf(existingItem))

        val duplicateItem = DisplayItem(
            id = Uuid.random(),
            name = "existing item",
            price = 15.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.addDisplayItem(duplicateItem)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("already exists") == true)
    }

    @Test
    fun `removeDisplayItem with existing item should succeed`() = runTest {
        // Given
        val item = DisplayItem(
            id = Uuid.random(),
            name = "Item to Remove",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )
        repository.seedDisplayItems(listOf(item))

        // When
        val result = useCase.removeDisplayItem(item)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, repository.displayItems.size)
    }

    @Test
    fun `removeDisplayItem with non-existing item should fail`() = runTest {
        // Given
        val item = DisplayItem(
            id = Uuid.random(),
            name = "Non-existing",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.removeDisplayItem(item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateDisplayItem with existing item should succeed`() = runTest {
        // Given
        val itemId = Uuid.random()
        val originalItem = DisplayItem(
            id = itemId,
            name = "Original",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )
        repository.seedDisplayItems(listOf(originalItem))

        val updatedItem = originalItem.copy(name = "Updated", price = 20.0)

        // When
        val result = useCase.updateDisplayItem(updatedItem)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, repository.displayItems.size)
        assertEquals("Updated", repository.displayItems[0].name)
        assertEquals(20.0, repository.displayItems[0].price)
    }

    @Test
    fun `updateDisplayItem with non-existing item should fail`() = runTest {
        // Given
        val item = DisplayItem(
            id = Uuid.random(),
            name = "Non-existing",
            price = 10.0,
            options = emptyList(),
            isInCart = false
        )

        // When
        val result = useCase.updateDisplayItem(item)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Item not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `searchItems with empty query should return all items`() = runTest {
        // Given
        val items = listOf(
            DisplayItem(id = Uuid.random(), name = "Apple", price = 10.0, options = emptyList(), isInCart = false),
            DisplayItem(id = Uuid.random(), name = "Banana", price = 20.0, options = emptyList(), isInCart = false)
        )
        repository.seedDisplayItems(items)

        // When
        val result = useCase.searchItems("")

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `searchItems with query should return matching items case insensitive`() = runTest {
        // Given
        val items = listOf(
            DisplayItem(id = Uuid.random(), name = "Apple Juice", price = 10.0, options = emptyList(), isInCart = false),
            DisplayItem(id = Uuid.random(), name = "Banana Smoothie", price = 20.0, options = emptyList(), isInCart = false),
            DisplayItem(id = Uuid.random(), name = "Orange Juice", price = 15.0, options = emptyList(), isInCart = false)
        )
        repository.seedDisplayItems(items)

        // When
        val result = useCase.searchItems("juice")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Apple Juice" })
        assertTrue(result.any { it.name == "Orange Juice" })
    }

    @Test
    fun `searchItems with no matching query should return empty list`() = runTest {
        // Given
        val items = listOf(
            DisplayItem(id = Uuid.random(), name = "Apple", price = 10.0, options = emptyList(), isInCart = false),
            DisplayItem(id = Uuid.random(), name = "Banana", price = 20.0, options = emptyList(), isInCart = false)
        )
        repository.seedDisplayItems(items)

        // When
        val result = useCase.searchItems("Mango")

        // Then
        assertEquals(0, result.size)
    }
}

// Fake repository for testing
@OptIn(ExperimentalUuidApi::class)
private class DisplayItemsFakeRepository : BookStoreRepository {
    var displayItems = mutableListOf<DisplayItem>()
    private var receiptList = mutableListOf<com.example.jbchretreatstore.bookstore.domain.model.ReceiptData>()

    fun seedDisplayItems(items: List<DisplayItem>) {
        displayItems = items.toMutableList()
    }

    override suspend fun updateDisplayItems(items: List<DisplayItem>) {
        displayItems = items.toMutableList()
    }

    override fun fetchDisplayItems(): Flow<List<DisplayItem>> {
        return flowOf(displayItems)
    }

    override suspend fun updateReceiptList(items: List<com.example.jbchretreatstore.bookstore.domain.model.ReceiptData>) {
        receiptList = items.toMutableList()
    }

    override fun fetchReceiptList(): Flow<List<com.example.jbchretreatstore.bookstore.domain.model.ReceiptData>> {
        return flowOf(receiptList)
    }
}
