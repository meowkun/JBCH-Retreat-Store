package com.example.jbchretreatstore.bookstore.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jbchretreatstore.bookstore.createDataStore
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class BookStoreRepositoryImpl() : BookStoreRepository {
    private var dataStoreInstance: DataStore<Preferences>? = null
    private val displayItemKey = stringPreferencesKey("display_items")
    private val receiptList = stringPreferencesKey("receipt_list")

    fun getDataStore(): DataStore<Preferences> {
        return dataStoreInstance ?: createDataStore().also { dataStoreInstance = it }
    }

    override suspend fun updateDisplayItems(items: List<DisplayItem>) {
        val json = Json.encodeToString(items)
        getDataStore().edit { prefs ->
            prefs[displayItemKey] = json
        }
    }

    override fun fetchDisplayItems(): Flow<List<DisplayItem>> =
        getDataStore().data.map { prefs ->
            prefs[displayItemKey]?.let { Json.Default.decodeFromString(it) } ?: emptyList()
        }

    override suspend fun updateReceiptList(items: List<ReceiptData>) {
        val json = Json.encodeToString(items)
        getDataStore().edit { prefs ->
            prefs[receiptList] = json
        }
    }

    override fun fetchReceiptList(): Flow<List<ReceiptData>> = getDataStore().data.map { prefs ->
        prefs[receiptList]?.let { Json.Default.decodeFromString(it) } ?: emptyList()
    }
}