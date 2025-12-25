package com.example.jbchretreatstore.bookstore.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.constants.ErrorMessages
import com.example.jbchretreatstore.bookstore.domain.constants.LogMessages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class BookStoreLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : BookStoreLocalDataSource {

    private val displayItemsKey = stringPreferencesKey("display_items")
    private val receiptsKey = stringPreferencesKey("receipt_list")
    private val testDataLoadedKey = booleanPreferencesKey("test_data_loaded")

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true // Handle missing/null fields with default values
    }

    override suspend fun saveDisplayItems(items: List<DisplayItemDto>) {
        try {
            val jsonString = json.encodeToString(items)
            dataStore.edit { prefs ->
                prefs[displayItemsKey] = jsonString
            }
        } catch (e: Exception) {
            println(LogMessages.withError(LogMessages.SAVE_DISPLAY_ITEMS_ERROR_PREFIX, e.message))
            throw IllegalStateException(ErrorMessages.SAVE_DISPLAY_ITEMS_FAILED, e)
        }
    }

    override fun getDisplayItems(): Flow<List<DisplayItemDto>> =
        dataStore.data.map { prefs ->
            try {
                prefs[displayItemsKey]?.let {
                    json.decodeFromString<List<DisplayItemDto>>(it)
                } ?: emptyList()
            } catch (e: Exception) {
                println(
                    LogMessages.withError(
                        LogMessages.LOAD_DISPLAY_ITEMS_ERROR_PREFIX,
                        e.message
                    )
                )
                // Return empty list instead of crashing
                emptyList()
            }
        }

    override suspend fun saveReceipts(receipts: List<ReceiptDataDto>) {
        try {
            val jsonString = json.encodeToString(receipts)
            dataStore.edit { prefs ->
                prefs[receiptsKey] = jsonString
            }
        } catch (e: Exception) {
            println(LogMessages.withError(LogMessages.SAVE_RECEIPTS_ERROR_PREFIX, e.message))
            throw IllegalStateException(ErrorMessages.SAVE_RECEIPTS_FAILED, e)
        }
    }

    override fun getReceipts(): Flow<List<ReceiptDataDto>> =
        dataStore.data.map { prefs ->
            try {
                prefs[receiptsKey]?.let {
                    json.decodeFromString<List<ReceiptDataDto>>(it)
                } ?: emptyList()
            } catch (e: Exception) {
                println(LogMessages.withError(LogMessages.LOAD_RECEIPTS_ERROR_PREFIX, e.message))
                // Return empty list instead of crashing
                emptyList()
            }
        }

    override suspend fun isTestDataLoaded(): Boolean =
        dataStore.data.map { prefs ->
            prefs[testDataLoadedKey] ?: false
        }.first()

    override suspend fun setTestDataLoaded(loaded: Boolean) {
        dataStore.edit { prefs ->
            prefs[testDataLoadedKey] = loaded
        }
    }
}

