package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String = "", // 27b39f4d-fd1d-4f61-85e1-221a58595454
    @SerialName("aud")
    val aud: String = "", // authenticated
    @SerialName("role")
    val role: String = "", // authenticated
    @SerialName("email")
    val email: String = "", // ali1@albaali.com
    @SerialName("email_confirmed_at")
    val emailConfirmedAt: String = "", // 2022-06-29T01:03:05.594295Z
    @SerialName("phone")
    val phone: String = "",
    @SerialName("confirmed_at")
    val confirmedAt: String = "", // 2022-06-29T01:03:05.594295Z
    @SerialName("last_sign_in_at")
    val lastSignInAt: String = "", // 2022-06-29T01:22:52.697588396Z
    @SerialName("app_metadata")
    val appMetadata: AppMetadata = AppMetadata(),
    @SerialName("user_metadata")
    val userMetadata: UserMetadata = UserMetadata(),
    @SerialName("identities")
    val identities: List<Identity> = listOf(),
    @SerialName("created_at")
    val createdAt: String = "", // 2022-06-29T01:03:05.571065Z
    @SerialName("updated_at")
    val updatedAt: String = "" // 2022-06-29T01:22:52.698712Z
)