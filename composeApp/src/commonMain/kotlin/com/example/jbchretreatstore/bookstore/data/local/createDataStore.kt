package com.example.jbchretreatstore.bookstore.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal const val dataStoreFileName = "items.preferences_pb"

expect fun createDataStore(): DataStore<Preferences>
