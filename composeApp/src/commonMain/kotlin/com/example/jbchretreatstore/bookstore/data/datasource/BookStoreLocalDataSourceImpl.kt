package com.example.jbchretreatstore.bookstore.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class BookStoreLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : BookStoreLocalDataSource {

    private val displayItemsKey = stringPreferencesKey("display_items")
    private val receiptsKey = stringPreferencesKey("receipt_list")

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
            println("Error saving display items: ${e.message}")
            throw IllegalStateException("Failed to save display items", e)
        }
    }

    override fun getDisplayItems(): Flow<List<DisplayItemDto>> =
        dataStore.data.map { prefs ->
            try {
                prefs[displayItemsKey]?.let {
                    json.decodeFromString<List<DisplayItemDto>>(it)
                } ?: emptyList()
            } catch (e: Exception) {
                println("Error loading display items: ${e.message}")
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
            println("Error saving receipts: ${e.message}")
            throw IllegalStateException("Failed to save receipts", e)
        }
    }

    override fun getReceipts(): Flow<List<ReceiptDataDto>> =
        dataStore.data.map { prefs ->
            try {
                prefs[receiptsKey]?.let {
                    json.decodeFromString<List<ReceiptDataDto>>(it)
                } ?: emptyList()
            } catch (e: Exception) {
                println("Error loading receipts: ${e.message}")
                // Return empty list instead of crashing
                emptyList()
            }
        }
}

