package com.noto.remote

import com.noto.data.source.remote.UserRemoteDataSource
import com.noto.domain.model.User
import com.noto.domain.schema.ResponseSchema
import com.noto.domain.schema.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post

class UserClient(private val client: HttpClient) : UserRemoteDataSource {

    override suspend fun createUser(user: User): ResponseSchema<UserResponse> {

        return client.post("$baseUrl/create") {
            body = user
        }

    }

    override suspend fun loginUser(user: User): ResponseSchema<UserResponse> {

        return client.post("$baseUrl/login") {
            body = user
        }

    }

}