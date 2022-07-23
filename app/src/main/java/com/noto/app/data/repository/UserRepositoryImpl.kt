package com.noto.app.data.repository

import com.noto.app.domain.model.User
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.domain.repository.UserRepository
import com.noto.app.domain.source.RemoteAuthDataSource
import com.noto.app.domain.source.RemoteUserDataSource
import com.noto.app.util.PasswordCryptoUtils
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
        val passwordData = PasswordCryptoUtils.hashPassword(password)
        val remoteAuthUser = remoteAuthDataSource.signUp(email, passwordData.encodedHashedPassword)
        settingsRepository.updateId(remoteAuthUser.id)
        settingsRepository.updateName(name)
        settingsRepository.updateEmail(email)
        settingsRepository.updatePasswordParameters(passwordData.encodedParameters)
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> = runCatching {
        val passwordData = remoteAuthDataSource.getPasswordParameters(email).run {
            PasswordCryptoUtils.hashPassword(
                password = password,
                encodedParameters = passwordParameters,
            )
        }
        val response = remoteAuthDataSource.login(email, passwordData.encodedHashedPassword)
        settingsRepository.updateId(response.user.id)
        settingsRepository.updateEmail(response.user.email)
        settingsRepository.updateAccessToken(response.accessToken)
        settingsRepository.updateRefreshToken(response.refreshToken)
        val user = remoteUserDataSource.getUser()
        settingsRepository.updateName(user.name)
    }

    override suspend fun completeUserRegistration(accessToken: String, refreshToken: String): Result<Unit> = runCatching {
        val id = settingsRepository.id.first()
        val name = settingsRepository.name.first()
        val passwordParameters = settingsRepository.passwordParameters.first()
        settingsRepository.updateAccessToken(accessToken)
        settingsRepository.updateRefreshToken(refreshToken)
        remoteUserDataSource.createUser(id, name, passwordParameters)
        settingsRepository.clearPasswordParameters()
    }

    override suspend fun updateName(name: String): Result<Unit> = runCatching {
        val id = settingsRepository.id.first()
        remoteUserDataSource.updateName(id, name)
        settingsRepository.updateName(name)
    }

    override suspend fun updateEmail(email: String): Result<Unit> = runCatching {
        remoteAuthDataSource.updateEmail(email)
    }

    override suspend fun completeUpdatingEmail(
        email: String,
        accessToken: String,
        refreshToken: String,
    ): Result<Unit> = runCatching { settingsRepository.updateEmail(email) }

    override suspend fun logOutUser(): Result<Unit> = runCatching {
        remoteAuthDataSource.logOut()
    }

    override suspend fun deleteUser(): Result<Unit> = runCatching {
        remoteAuthDataSource.delete()
    }

}