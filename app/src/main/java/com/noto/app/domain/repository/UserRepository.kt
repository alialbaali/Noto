package com.noto.app.domain.repository

interface UserRepository {

    suspend fun registerUser(name: String, email: String, password: String): Result<Unit>

    suspend fun loginUser(email: String, password: String): Result<Unit>

}