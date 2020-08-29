package com.noto.data.source.remote

import com.noto.domain.model.User
import com.noto.domain.schema.ResponseSchema
import com.noto.domain.schema.UserResponse
import java.util.*

class FakeUserClient(private val users: MutableList<User> = mutableListOf()) : UserRemoteDataSource {

    override suspend fun createUser(user: User): ResponseSchema<UserResponse> {
        users.add(user)
        val userToken = UUID.randomUUID().toString()
        val userResponse = UserResponse(user.userDisplayName, user.userEmail, userToken)
        return ResponseSchema(true, data = userResponse)
    }

    override suspend fun loginUser(user: User): ResponseSchema<UserResponse> {
        val result = users.find { it.userEmail == user.userEmail }
        return if (result == null) {
            ResponseSchema(false, "User doesn't exist")
        } else {
            val userToken = UUID.randomUUID().toString()
            val userResponse = UserResponse(user.userDisplayName, user.userEmail, userToken)
            ResponseSchema(true, data = userResponse)
        }
    }

}
