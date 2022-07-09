package com.noto.app.domain.source

import com.noto.app.data.model.remote.AuthResponse

interface RemoteAuthDataSource {

    suspend fun signUp(email: String, password: String): AuthResponse

    suspend fun login(email: String, password: String): AuthResponse

    suspend fun refreshToken(refreshToken: String): AuthResponse

    suspend fun logOut()

}