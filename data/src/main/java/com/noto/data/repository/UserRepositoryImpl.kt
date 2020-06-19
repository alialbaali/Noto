package com.noto.data.repository

import com.noto.data.repository.util.tryCatching
import com.noto.data.source.local.UserLocalDataSource
import com.noto.data.source.remote.UserRemoteDataSource
import com.noto.domain.model.User
import com.noto.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val localSource: UserLocalDataSource, private val remoteSource: UserRemoteDataSource) : UserRepository {

    override suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {

        tryCatching {

            remoteSource.createUser(user)

        }.mapCatching { response ->

            if (response.success) {

                val data = response.data!!

                val responseUser = User(data.userDisplayName, data.userEmail)

                localSource.createUser(responseUser, data.userToken)

                Result.success(responseUser)

            } else {

                Result.failure(Throwable(response.error))

            }

        }.getOrElse {

            Result.failure(it)

        }

    }

    override suspend fun loginUser(user: User): Result<User> = withContext(Dispatchers.IO) {

        tryCatching {

            remoteSource.loginUser(user)

        }.mapCatching { response ->

            if (response.success) {

                val data = response.data!!

                val responseUser = User(data.userDisplayName, data.userEmail)

                localSource.createUser(responseUser, data.userToken)

                Result.success(responseUser)

            } else {

                Result.failure(Throwable(response.error))

            }

        }.getOrElse {

            Result.failure(it)

        }

    }

    override suspend fun getUserToken(): Boolean {
        return localSource.getUserToken().isNotBlank()
    }

}