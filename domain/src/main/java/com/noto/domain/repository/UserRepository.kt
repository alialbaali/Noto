package com.noto.domain.repository

import com.noto.domain.model.User

const val AUTH_SCHEME = "Bearer "

interface UserRepository {

    suspend fun createUser(user: User): Result<User>

    suspend fun loginUser(user: User): Result<User>

    suspend fun getUserToken(): Boolean

}