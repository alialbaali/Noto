package com.noto.app.domain.source

import kotlinx.coroutines.flow.Flow

interface LocalStorage {

    fun getAll(): Flow<Map<String, String>>

    fun get(key: String): Flow<String>

    fun getOrNull(key: String): Flow<String?>

    suspend fun put(key: String, value: String)

    suspend fun remove(key: String)

    suspend fun clear()
}