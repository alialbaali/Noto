package com.noto.app.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FakeLocalStorage : LocalStorage {
    private val storage = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())

    override fun get(key: String): Flow<String> = storage.mapNotNull { it[key] }

    override fun getOrNull(key: String): Flow<String?> = storage.map { it[key] }

    override suspend fun put(key: String, value: String) {
        storage.value[key] = value
    }

    override suspend fun remove(key: String) {
        storage.value.remove(key)
    }

    override suspend fun clear() {
        storage.value.clear()
    }
}