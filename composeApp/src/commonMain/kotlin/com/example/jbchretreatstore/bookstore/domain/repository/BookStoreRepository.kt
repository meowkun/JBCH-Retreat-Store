package com.example.jbchretreatstore.bookstore.domain.repository

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.coroutines.flow.Flow

interface BookStoreRepository {
    suspend fun updateDisplayItems(items: List<DisplayItem>)
    fun fetchDisplayItems(): Flow<List<DisplayItem>>
    suspend fun updateReceiptList(items: List<ReceiptData>)
    fun fetchReceiptList(): Flow<List<ReceiptData>>

    // Test data loading flag
    suspend fun isTestDataLoaded(): Boolean
    suspend fun setTestDataLoaded(loaded: Boolean)
}