package com.example.jbchretreatstore.bookstore.fake

import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake implementation of BookStoreRepository for testing purposes.
 * Allows setting up initial data and verifying interactions.
 */
class FakeBookStoreRepository : BookStoreRepository {

    private val _displayItems = MutableStateFlow<List<DisplayItem>>(emptyList())
    private val _receipts = MutableStateFlow<List<ReceiptData>>(emptyList())

    // Track calls for verification
    var updateDisplayItemsCalled = false
        private set
    var updateReceiptListCalled = false
        private set
    var lastSavedDisplayItems: List<DisplayItem>? = null
        private set
    var lastSavedReceipts: List<ReceiptData>? = null
        private set

    // Option to simulate errors
    var shouldThrowOnSave = false
    var errorToThrow: Exception? = null

    override suspend fun updateDisplayItems(items: List<DisplayItem>) {
        updateDisplayItemsCalled = true
        lastSavedDisplayItems = items

        if (shouldThrowOnSave) {
            throw errorToThrow ?: Exception("Simulated error")
        }

        _displayItems.value = items
    }

    override fun fetchDisplayItems(): Flow<List<DisplayItem>> {
        return _displayItems.asStateFlow()
    }

    override suspend fun updateReceiptList(items: List<ReceiptData>) {
        updateReceiptListCalled = true
        lastSavedReceipts = items

        if (shouldThrowOnSave) {
            throw errorToThrow ?: Exception("Simulated error")
        }

        _receipts.value = items
    }

    override fun fetchReceiptList(): Flow<List<ReceiptData>> {
        return _receipts.asStateFlow()
    }

    // Helper methods for test setup

    fun setDisplayItems(items: List<DisplayItem>) {
        _displayItems.value = items
    }

    fun setReceipts(receipts: List<ReceiptData>) {
        _receipts.value = receipts
    }

    fun reset() {
        _displayItems.value = emptyList()
        _receipts.value = emptyList()
        updateDisplayItemsCalled = false
        updateReceiptListCalled = false
        lastSavedDisplayItems = null
        lastSavedReceipts = null
        shouldThrowOnSave = false
        errorToThrow = null
    }
}

