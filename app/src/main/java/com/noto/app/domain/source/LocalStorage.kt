package com.noto.app.domain.source

import kotlinx.coroutines.flow.Flow

interface LocalStorage {

    fun get(key: String): Result<Flow<String>>

    suspend fun put(key: String, value: String): Result<Unit>

    suspend fun remove(key: String): Result<Unit>

    suspend fun clear(): Result<Unit>

}