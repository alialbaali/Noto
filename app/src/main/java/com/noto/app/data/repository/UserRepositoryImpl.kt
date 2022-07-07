package com.noto.app.data.repository

import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.domain.repository.UserRepository
import com.noto.app.domain.source.RemoteAuthDataSource
import com.noto.app.domain.source.RemoteUserDataSource

class UserRepositoryImpl(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val remoteUserDataSource: RemoteUserDataSource,
    private val settingsRepository: SettingsRepository,
) : UserRepository {

    override suspend fun registerUser(name: String, email: String, password: String): Result<Unit> = runCatching {
        val response = remoteAuthDataSource.signUp(email, password)
        settingsRepository.updateAccessToken(response.accessToken)
        settingsRepository.updateRefreshToken(response.refreshToken)
        remoteUserDataSource.createUser(response.user.id, name, email)
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> = runCatching {
        val response = remoteAuthDataSource.login(email, password)
        settingsRepository.updateAccessToken(response.accessToken)
        settingsRepository.updateRefreshToken(response.refreshToken)
    }

}