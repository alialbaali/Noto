package com.noto.domain.repository

interface SyncRepository {

    val userToken: String

    suspend fun fetchData(): Result<Unit>

    suspend fun syncData(): Result<Unit>

}