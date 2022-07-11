package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUser(
    @SerialName("id")
    val id: String = "", // e3154659-7d55-401a-8ba0-a09e91f0bf4e
    @SerialName("name")
    val name: String = "", // Name Surname
)