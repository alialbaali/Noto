package com.alialbaali.noto.data.repository

import com.alialbaali.noto.data.repository.util.tryCatching
import com.alialbaali.noto.data.source.local.UserLocalDataSource
import com.alialbaali.noto.data.source.remote.UserRemoteDataSource
import com.noto.domain.model.User
import com.noto.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val localSource: UserLocalDataSource, private val remoteSource: UserRemoteDataSource) : UserRepository {

    override suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {

        tryCatching {
            remoteSource.createUser(user)
        }.mapCatching { response ->
            val data = response.data!!
            val responseUser = User(data.userDisplayName, data.userEmail)
            localSource.createUser(responseUser, data.userToken)
            Result.success(responseUser)
        }.getOrElse {
            Result.failure(it)
        }
    }

    override suspend fun loginUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        tryCatching {
            remoteSource.loginUser(user)
        }.mapCatching { response ->
            val data = response.data!!
            val responseUser = User(data.userDisplayName, data.userEmail)
            localSource.createUser(responseUser, data.userToken)
            Result.success(responseUser)
        }.getOrElse {
            Result.failure(it)
        }
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