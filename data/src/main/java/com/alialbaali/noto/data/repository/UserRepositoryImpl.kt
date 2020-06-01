package com.alialbaali.noto.data.repository

import com.noto.domain.model.User
import com.noto.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(user: User): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }
}