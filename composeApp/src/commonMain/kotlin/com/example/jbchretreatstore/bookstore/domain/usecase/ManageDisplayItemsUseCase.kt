package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.data.testdata.SampleTestData
import com.example.jbchretreatstore.bookstore.domain.constants.ErrorMessages
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi

/**
 * Use case for managing display items with business logic
 */
@OptIn(ExperimentalUuidApi::class)
class ManageDisplayItemsUseCase(
    private val repository: BookStoreRepository
) {

    /**
     * Fetch all display items
     */
    fun getDisplayItems(): Flow<List<DisplayItem>> {
        return repository.fetchDisplayItems()
    }

    /**
     * Add a new display item with validation
     * @return Result indicating success or failure with error message
     */
    suspend fun addDisplayItem(newItem: DisplayItem): Result<Unit> {
        // Validate item name is not empty
        if (newItem.name.isBlank()) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_NAME_EMPTY))
        }

        // Validate price is positive
        if (newItem.price <= 0) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_PRICE_INVALID))
        }

        // Validate no duplicate items (case-insensitive)
        val currentItems = repository.fetchDisplayItems().first()
        val isDuplicate = currentItems.any {
            it.name.equals(newItem.name, ignoreCase = true)
        }

        if (isDuplicate) {
            return Result.failure(IllegalArgumentException(ErrorMessages.itemAlreadyExists(newItem.name)))
        }

        // Add item
        val updatedList = currentItems + newItem
        repository.updateDisplayItems(updatedList)

        return Result.success(Unit)
    }

    /**
     * Remove a display item
     */
    suspend fun removeDisplayItem(item: DisplayItem): Result<Unit> {
        val currentItems = repository.fetchDisplayItems().first()
        val updatedList = currentItems.filter { it.id != item.id }

        if (updatedList.size == currentItems.size) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_NOT_FOUND))
        }

        repository.updateDisplayItems(updatedList)
        return Result.success(Unit)
    }

    /**
     * Update an existing display item
     */
    suspend fun updateDisplayItem(updatedItem: DisplayItem): Result<Unit> {
        val currentItems = repository.fetchDisplayItems().first()
        val itemIndex = currentItems.indexOfFirst { it.id == updatedItem.id }

        if (itemIndex == -1) {
            return Result.failure(IllegalArgumentException(ErrorMessages.ITEM_NOT_FOUND))
        }

        val updatedList = currentItems.toMutableList().apply {
            set(itemIndex, updatedItem)
        }

        repository.updateDisplayItems(updatedList)
        return Result.success(Unit)
    }

    /**
     * Search items by name.
     * @testOnly Currently only used in unit tests.
     */
    suspend fun searchItems(query: String): List<DisplayItem> {
        if (query.isBlank()) {
            return repository.fetchDisplayItems().first()
        }

        return repository.fetchDisplayItems().first().filter { item ->
            item.name.contains(query, ignoreCase = true)
        }
    }

    /**
     * Load sample test data (for development/testing purposes)
     * This replaces existing items with sample data
     */
    suspend fun loadTestData() {
        repository.updateDisplayItems(SampleTestData.sampleDisplayItems)
    }

    /**
     * Load sample test data only if it hasn't been loaded before (one-time initialization)
     * @return true if test data was loaded, false if it was already loaded previously
     */
    suspend fun loadTestDataIfNeeded(): Boolean {
        if (!repository.isTestDataLoaded()) {
            repository.updateDisplayItems(SampleTestData.sampleDisplayItems)
            repository.setTestDataLoaded(true)
            return true
        }
        return false
    }

    /**
     * Clear all display items
     */
    suspend fun clearAllItems() {
        repository.updateDisplayItems(emptyList())
    }

    /**
     * Reorder display items
     * @param reorderedItems The new ordered list of display items
     * @return Result indicating success or failure
     */
    suspend fun reorderDisplayItems(reorderedItems: List<DisplayItem>): Result<Unit> {
        repository.updateDisplayItems(reorderedItems)
        return Result.success(Unit)
    }
}


