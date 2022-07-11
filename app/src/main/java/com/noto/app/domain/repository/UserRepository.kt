package com.noto.app.domain.repository

import com.noto.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val user: Flow<Result<User>>

    suspend fun registerUser(name: String, email: String, password: String): Result<Unit>

    suspend fun loginUser(email: String, password: String): Result<Unit>

    suspend fun completeUserRegistration(accessToken: String, refreshToken: String): Result<Unit>

    suspend fun updateName(name: String): Result<Unit>

    suspend fun updateEmail(email: String): Result<Unit>

    suspend fun completeUpdatingEmail(email: String, accessToken: String, refreshToken: String): Result<Unit>

    suspend fun logOutUser(): Result<Unit>

    suspend fun deleteUser(): Result<Unit>

}