package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Use case for managing display items with business logic
 */
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
            return Result.failure(IllegalArgumentException("Item name cannot be empty"))
        }

        // Validate price is positive
        if (newItem.price <= 0) {
            return Result.failure(IllegalArgumentException("Item price must be greater than zero"))
        }

        // Validate no duplicate items (case-insensitive)
        val currentItems = repository.fetchDisplayItems().first()
        val isDuplicate = currentItems.any {
            it.name.equals(newItem.name, ignoreCase = true)
        }

        if (isDuplicate) {
            return Result.failure(IllegalArgumentException("Item with name '${newItem.name}' already exists"))
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
            return Result.failure(IllegalArgumentException("Item not found"))
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
            return Result.failure(IllegalArgumentException("Item not found"))
        }

        val updatedList = currentItems.toMutableList().apply {
            set(itemIndex, updatedItem)
        }

        repository.updateDisplayItems(updatedList)
        return Result.success(Unit)
    }

    /**
     * Search items by name
     */
    suspend fun searchItems(query: String): List<DisplayItem> {
        if (query.isBlank()) {
            return repository.fetchDisplayItems().first()
        }

        return repository.fetchDisplayItems().first().filter { item ->
            item.name.contains(query, ignoreCase = true)
        }
    }
}


