package com.noto.domain.model

data class User(
    var userDisplayName: String = String(),
    var userEmail: String = String(),
    var userPassword: String = String()
)