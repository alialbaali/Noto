package com.noto.app.data.source

import com.noto.app.data.model.remote.AuthErrorResponse
import com.noto.app.data.model.remote.AuthResponse
import com.noto.app.data.model.remote.ResponseException.Auth
import com.noto.app.data.model.remote.SignUpErrorResponse
import com.noto.app.domain.source.RemoteAuthDataSource
import com.noto.app.util.Constants
import com.noto.app.util.getOrElse
import com.noto.app.util.unhandledError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class RemoteAuthClient(private val client: HttpClient) : RemoteAuthDataSource {

    override suspend fun signUp(email: String, password: String): AuthResponse {
        return client.post("/auth/v1/signup") {
            setBody(
                mapOf(
                    Constants.Email to email,
                    Constants.Password to password,
                )
            )
        }.getOrElse { response ->
            val errorResponse = response.body<SignUpErrorResponse>()
            when (response.status) {
                HttpStatusCode.BadRequest -> Auth.UserAlreadyRegistered()
                HttpStatusCode.UnprocessableEntity -> when {
                    errorResponse.msg.contains("email", ignoreCase = true) -> Auth.InvalidEmail()
                    errorResponse.msg.contains("password", ignoreCase = true) -> Auth.InvalidPassword()
                    else -> unhandledError(errorResponse.msg)
                }
                else -> unhandledError(errorResponse.msg)
            }
        }
    }

    override suspend fun login(email: String, password: String): AuthResponse {
        return client.post("/auth/v1/token") {
            parameter(Constants.GrantType, Constants.Password)
            setBody(
                mapOf(
                    Constants.Email to email,
                    Constants.Password to password,
                )
            )
        }.getOrElse { response ->
            val errorResponse = response.body<AuthErrorResponse>()
            when (response.status) {
                HttpStatusCode.BadRequest -> Auth.InvalidLoginCredentials()
                else -> unhandledError(errorResponse.error)
            }
        }
    }

    override suspend fun refreshToken(refreshToken: String): AuthResponse {
        return client.post("auth/v1/token") {
            parameter(Constants.GrantType, Constants.RefreshToken)
            setBody(
                mapOf(
                    Constants.RefreshToken to refreshToken
                )
            )
        }.getOrElse { response ->
            val errorResponse = response.body<AuthErrorResponse>()
            when (response.status) {
                HttpStatusCode.BadRequest -> Auth.InvalidRefreshToken()
                else -> unhandledError(errorResponse.error)
            }
        }
    }
}