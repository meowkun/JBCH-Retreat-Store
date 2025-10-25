package com.example.jbchretreatstore.bookstore.domain.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jbchretreatstore.bookstore.createDataStore
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class BookStoreRepository() {
    private var dataStoreInstance: DataStore<Preferences>? = null
    private val displayItemKey = stringPreferencesKey("display_items")

    fun getDataStore(): DataStore<Preferences> {
        return dataStoreInstance ?: createDataStore().also { dataStoreInstance = it }
    }

    suspend fun updateDisplayItems(items: List<DisplayItem>) {
        val json = Json.Default.encodeToString(items)
        getDataStore().edit { prefs ->
            prefs[displayItemKey] = json
        }
    }

    fun fetchDisplayItems(): Flow<List<DisplayItem>> =
        getDataStore().data.map { prefs ->
            prefs[displayItemKey]?.let { Json.Default.decodeFromString(it) } ?: emptyList()
        }
}