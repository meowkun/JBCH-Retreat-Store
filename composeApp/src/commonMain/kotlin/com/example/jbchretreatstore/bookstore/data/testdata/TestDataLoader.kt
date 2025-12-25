package com.example.jbchretreatstore.bookstore.data.testdata

import com.example.jbchretreatstore.bookstore.data.testdata.TestDataLoader.Companion.ENABLE_TEST_DATA
import com.example.jbchretreatstore.bookstore.domain.constants.LogMessages
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository

/**
 * Centralized test data loader for development and testing purposes.
 *
 * Usage:
 * - Call [loadAllTestData] to load all sample data (display items + purchase history)
 * - Call [loadAllTestDataIfNeeded] to load test data only once (persisted flag)
 * - Set [ENABLE_TEST_DATA] to false to disable test data loading
 */
class TestDataLoader(
    private val repository: BookStoreRepository
) {
    companion object {
        /**
         * Master switch to enable/disable test data loading.
         * Set to false for production builds.
         */
        const val ENABLE_TEST_DATA = true
    }

    /**
     * Load all test data (display items + purchase history).
     * This will replace existing data with sample data.
     */
    suspend fun loadAllTestData() {
        if (!ENABLE_TEST_DATA) {
            println(LogMessages.TEST_DATA_DISABLED)
            return
        }

        println(LogMessages.TEST_DATA_LOADING_ALL)
        repository.updateDisplayItems(SampleTestData.sampleDisplayItems)
        repository.updateReceiptList(SampleTestData.samplePurchaseHistory)
        println(LogMessages.TEST_DATA_LOADED_SUCCESS)
    }

    /**
     * Load all test data only if it hasn't been loaded before (one-time initialization).
     * Uses a persisted flag to track if test data has already been loaded.
     *
     * @return true if test data was loaded, false if it was already loaded previously or disabled
     */
    suspend fun loadAllTestDataIfNeeded(): Boolean {
        if (!ENABLE_TEST_DATA) {
            println(LogMessages.TEST_DATA_DISABLED)
            return false
        }

        if (repository.isTestDataLoaded()) {
            println(LogMessages.TEST_DATA_ALREADY_LOADED)
            return false
        }

        println(LogMessages.TEST_DATA_LOADING_FIRST_TIME)
        repository.updateDisplayItems(SampleTestData.sampleDisplayItems)
        repository.updateReceiptList(SampleTestData.samplePurchaseHistory)
        repository.setTestDataLoaded(true)
        println(LogMessages.TEST_DATA_LOADED_SUCCESS)
        return true
    }

    /**
     * Load only display items test data.
     */
    suspend fun loadDisplayItemsTestData() {
        if (!ENABLE_TEST_DATA) return
        repository.updateDisplayItems(SampleTestData.sampleDisplayItems)
    }

    /**
     * Load only purchase history test data.
     */
    suspend fun loadPurchaseHistoryTestData() {
        if (!ENABLE_TEST_DATA) return
        repository.updateReceiptList(SampleTestData.samplePurchaseHistory)
    }

    /**
     * Clear all data (display items + purchase history).
     */
    suspend fun clearAllData() {
        repository.updateDisplayItems(emptyList())
        repository.updateReceiptList(emptyList())
        repository.setTestDataLoaded(false)
        println(LogMessages.TEST_DATA_CLEARED)
    }

    /**
     * Reset test data loaded flag (allows loading test data again).
     */
    suspend fun resetTestDataFlag() {
        repository.setTestDataLoaded(false)
        println(LogMessages.TEST_DATA_FLAG_RESET)
    }
}

