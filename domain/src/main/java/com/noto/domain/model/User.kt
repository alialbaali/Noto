package com.noto.domain.model

data class User(
    val id: Long = 0L,
    val name: String,
    val username: String,
    val password: String
)