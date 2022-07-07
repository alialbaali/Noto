package com.noto.app.domain.repository

import com.noto.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val user: Flow<Result<User>>

    suspend fun registerUser(name: String, email: String, password: String): Result<Unit>

    suspend fun loginUser(email: String, password: String): Result<Unit>

}