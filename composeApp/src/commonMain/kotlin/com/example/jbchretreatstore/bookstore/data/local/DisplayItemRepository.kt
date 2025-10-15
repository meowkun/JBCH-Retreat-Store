package com.example.jbchretreatstore.bookstore.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DisplayItemRepository() {
    private var dataStoreInstance: DataStore<Preferences>? = null

    fun getDataStore(): DataStore<Preferences> {
        return dataStoreInstance ?: createDataStore().also { dataStoreInstance = it }
    }

    private val key = stringPreferencesKey("display_items")

    suspend fun saveItems(items: List<DisplayItem>) {
        val json = Json.encodeToString(items)
        getDataStore().edit { prefs ->
            prefs[key] = json
        }
    }

    fun getItems(): Flow<List<DisplayItem>> =
        getDataStore().data.map { prefs ->
            prefs[key]?.let { Json.decodeFromString(it) } ?: emptyList()
        }
}