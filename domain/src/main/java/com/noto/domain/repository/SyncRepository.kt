package com.noto.domain.repository

interface SyncRepository {

    val userToken: String

    suspend fun fetchData()

    suspend fun syncData()

}