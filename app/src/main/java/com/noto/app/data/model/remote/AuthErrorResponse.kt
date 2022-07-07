package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthErrorResponse(
    @SerialName("error")
    val error: String = "", // invalid_grant
    @SerialName("error_description")
    val errorDescription: String = "", // Invalid login credentials
)