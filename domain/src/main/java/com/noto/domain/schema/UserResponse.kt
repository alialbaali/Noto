package com.noto.domain.schema

data class UserResponse(
    val userDisplayName: String,
    val userEmail: String,
    val userToken: String
)