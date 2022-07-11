package com.noto.app.domain.source

import com.noto.app.data.model.remote.AuthResponse
import com.noto.app.data.model.remote.RemoteAuthUser

interface RemoteAuthDataSource {

    suspend fun signUp(email: String, password: String): RemoteAuthUser

    suspend fun login(email: String, password: String): AuthResponse

    suspend fun refreshToken(refreshToken: String): AuthResponse

    suspend fun updateEmail(email: String): RemoteAuthUser

    suspend fun get(): RemoteAuthUser

    suspend fun logOut()

    suspend fun delete()

}