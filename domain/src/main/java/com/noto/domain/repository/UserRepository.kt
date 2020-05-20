package com.noto.domain.repository

import com.noto.domain.model.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun deleteUser(user: User)

    suspend fun getUser(user: User) : Result<User>

    suspend fun updateUser(user: User)
}