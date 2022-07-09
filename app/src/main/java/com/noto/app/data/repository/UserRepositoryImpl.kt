package com.noto.app.data.repository

import com.noto.app.domain.model.User
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.domain.repository.UserRepository
import com.noto.app.domain.source.RemoteAuthDataSource
import com.noto.app.domain.source.RemoteUserDataSource
import kotlinx.coroutines.flow.*

class UserRepositoryImpl(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val remoteUserDataSource: RemoteUserDataSource,
    private val settingsRepository: SettingsRepository,
) : UserRepository {

    override val user: Flow<Result<User>> = combine(
        settingsRepository.name,
        settingsRepository.email,
    ) { name, email -> User(name, email) }
        .map { Result.success(it) }
        .catch { emit(Result.failure(it)) }

    override suspend fun registerUser(name: String, email: String, password: String): Result<Unit> = runCatching {
        val response = remoteAuthDataSource.signUp(email, password)
        settingsRepository.updateAccessToken(response.accessToken)
        settingsRepository.updateRefreshToken(response.refreshToken)
        remoteUserDataSource.createUser(response.user.id, name, email)
        settingsRepository.updateId(response.user.id)
        settingsRepository.updateName(name)
        settingsRepository.updateEmail(email)
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> = runCatching {
        val response = remoteAuthDataSource.login(email, password)
        settingsRepository.updateAccessToken(response.accessToken)
        settingsRepository.updateRefreshToken(response.refreshToken)
    }

    override suspend fun updateName(name: String): Result<Unit> = runCatching {
        val id = settingsRepository.id.first()
        remoteUserDataSource.updateName(id, name)
        settingsRepository.updateName(name)
    }

    override suspend fun logOutUser(): Result<Unit> = runCatching {
        remoteAuthDataSource.logOut()
    }

    override suspend fun deleteUser(): Result<Unit> = runCatching {
        remoteAuthDataSource.delete()
    }

}