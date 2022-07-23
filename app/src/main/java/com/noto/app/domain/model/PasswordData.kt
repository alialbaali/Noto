package com.noto.app.domain.model

data class PasswordData(
    val encodedHashedPassword: String,
    val encodedParameters: String,
//    val shouldRehashAgain: Boolean,
)