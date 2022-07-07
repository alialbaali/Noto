package com.noto.app.data.repository

import com.noto.app.domain.model.User
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.domain.repository.UserRepository
import com.noto.app.domain.source.RemoteAuthDataSource
import com.noto.app.domain.source.RemoteUserDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

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
        settingsRepository.updateName(name)
        settingsRepository.updateEmail(email)
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> = runCatching {
        val response = remoteAuthDataSource.login(email, password)
        settingsRepository.updateAccessToken(response.accessToken)
        settingsRepository.updateRefreshToken(response.refreshToken)
    }

}