package com.noto.domain.local

import kotlinx.coroutines.flow.Flow

interface LocalStorage {

    suspend fun get(key: String): Result<Flow<String>>

    suspend fun put(key: String, value: String): Result<Unit>

    suspend fun remove(key: String): Result<Unit>

    suspend fun clear(): Result<Unit>

}