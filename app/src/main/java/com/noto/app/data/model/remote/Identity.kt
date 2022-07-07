package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Identity(
    @SerialName("id")
    val id: String = "", // 27b39f4d-fd1d-4f61-85e1-221a58595454
    @SerialName("user_id")
    val userId: String = "", // 27b39f4d-fd1d-4f61-85e1-221a58595454
    @SerialName("identity_data")
    val identityData: IdentityData = IdentityData(),
    @SerialName("provider")
    val provider: String = "", // email
    @SerialName("last_sign_in_at")
    val lastSignInAt: String = "", // 2022-06-29T01:03:05.592399Z
    @SerialName("created_at")
    val createdAt: String = "", // 2022-06-29T01:03:05.592445Z
    @SerialName("updated_at")
    val updatedAt: String = "" // 2022-06-29T01:03:05.59245Z
)