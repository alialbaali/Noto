package com.alialbaali.noto.remote

import com.alialbaali.noto.data.source.remote.UserRemoteDataSource
import com.noto.domain.model.User
import com.noto.domain.schema.ResponseSchema
import com.noto.domain.schema.UserResponse
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.POST

interface UserClient : UserRemoteDataSource {

    @POST("/user/create")
    override suspend fun createUser(@Body user: User): ResponseSchema<UserResponse>

    @POST("/user/login")
    override suspend fun loginUser(@Body user: User): ResponseSchema<UserResponse>

}