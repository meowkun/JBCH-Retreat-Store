package com.example.jbchretreatstore.bookstore.data.datasource

import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import kotlinx.coroutines.flow.Flow

interface BookStoreLocalDataSource {
    suspend fun saveDisplayItems(items: List<DisplayItemDto>)
    fun getDisplayItems(): Flow<List<DisplayItemDto>>
    suspend fun saveReceipts(receipts: List<ReceiptDataDto>)
    fun getReceipts(): Flow<List<ReceiptDataDto>>

    // Test data loading flag
    suspend fun isTestDataLoaded(): Boolean
    suspend fun setTestDataLoaded(loaded: Boolean)
}

