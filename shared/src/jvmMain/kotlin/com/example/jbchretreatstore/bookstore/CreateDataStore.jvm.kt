package com.example.jbchretreatstore.bookstore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = {
            dataStoreFileName.toPath()
        }
    )
}