package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestErrorResponse(
    @SerialName("message")
    val message: String = "", // duplicate key value violates unique constraint "users_pkey"
    @SerialName("code")
    val code: String = "", // 23505
    @SerialName("details")
    val details: String? = null, // null
    @SerialName("hint")
    val hint: String? = null, // null
)