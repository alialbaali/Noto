package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppMetadata(
    @SerialName("provider")
    val provider: String = "", // email
    @SerialName("providers")
    val providers: List<String> = listOf()
)