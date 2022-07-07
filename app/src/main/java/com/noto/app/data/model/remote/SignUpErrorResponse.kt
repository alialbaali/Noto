package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpErrorResponse(
    @SerialName("code")
    val code: Int = 0, // 422
    @SerialName("msg")
    val msg: String = "", // Unable to validate email address: invalid format
)