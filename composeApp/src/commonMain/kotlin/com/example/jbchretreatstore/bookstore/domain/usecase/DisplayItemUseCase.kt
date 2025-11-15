package com.example.jbchretreatstore.bookstore.domain.usecase

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepositoryImpl
import kotlinx.coroutines.flow.Flow

class DisplayItemUseCase(
    private val repository: BookStoreRepositoryImpl
) {
    suspend fun updateDisplayItems(items: List<DisplayItem>) {
        repository.updateDisplayItems(items)
    }

    fun fetchDisplayItems(): Flow<List<DisplayItem>> {
        return repository.fetchDisplayItems()
    }
}