package com.noto.app.data.source

import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.noto.app.domain.source.LocalStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class LocalStorageImpl(private val storage: DataStore<Preferences>) : LocalStorage {

    override fun get(key: String): Flow<String> = storage.data
        .mapNotNull { preferences -> preferences[preferencesKey(key)] }

    override suspend fun put(key: String, value: String) {
        storage.edit { preferences -> preferences[preferencesKey(key)] = value }
    }

    override suspend fun remove(key: String) {
        storage.edit { preferences -> preferences.remove(preferencesKey(key)) }
    }

    override suspend fun clear() {
        storage.edit { preferences -> preferences.clear() }
    }
}