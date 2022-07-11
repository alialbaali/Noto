package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteAuthUser(
    @SerialName("id")
    val id: String = "", // 80abe018-fe09-4e3f-94d3-12740f999317
    @SerialName("aud")
    val aud: String = "", // authenticated
    @SerialName("role")
    val role: String = "", // authenticated
    @SerialName("email")
    val email: String = "", // email@gmail.com
    @SerialName("email_confirmed_at")
    val emailConfirmedAt: String = "", // 2022-07-11T15:16:29.118449Z
    @SerialName("phone")
    val phone: String = "",
    @SerialName("confirmation_sent_at")
    val confirmationSentAt: String = "", // 2022-07-11T15:06:44.205087Z
    @SerialName("confirmed_at")
    val confirmedAt: String = "", // 2022-07-11T15:16:29.118449Z
    @SerialName("last_sign_in_at")
    val lastSignInAt: String = "", // 2022-07-11T15:16:35.63345582Z
    @SerialName("app_metadata")
    val appMetadata: AppMetadata = AppMetadata(),
    @SerialName("user_metadata")
    val userMetadata: UserMetadata = UserMetadata(),
    @SerialName("identities")
    val identities: List<Identity> = listOf(),
    @SerialName("created_at")
    val createdAt: String = "", // 2022-07-11T14:56:23.008194Z
    @SerialName("updated_at")
    val updatedAt: String = "", // 2022-07-11T15:16:35.634787Z
)