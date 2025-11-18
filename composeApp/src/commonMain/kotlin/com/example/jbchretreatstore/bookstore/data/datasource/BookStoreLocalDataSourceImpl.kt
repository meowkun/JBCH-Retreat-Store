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
        val jsonString = json.encodeToString(items)
        dataStore.edit { prefs ->
            prefs[displayItemsKey] = jsonString
        }
    }

    override fun getDisplayItems(): Flow<List<DisplayItemDto>> =
        dataStore.data.map { prefs ->
            prefs[displayItemsKey]?.let {
                json.decodeFromString<List<DisplayItemDto>>(it)
            } ?: emptyList()
        }

    override suspend fun saveReceipts(receipts: List<ReceiptDataDto>) {
        val jsonString = json.encodeToString(receipts)
        dataStore.edit { prefs ->
            prefs[receiptsKey] = jsonString
        }
    }

    override fun getReceipts(): Flow<List<ReceiptDataDto>> =
        dataStore.data.map { prefs ->
            prefs[receiptsKey]?.let {
                json.decodeFromString<List<ReceiptDataDto>>(it)
            } ?: emptyList()
        }
}

