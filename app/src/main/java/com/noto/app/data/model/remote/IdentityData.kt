package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentityData(
    @SerialName("sub")
    val sub: String = "" // 27b39f4d-fd1d-4f61-85e1-221a58595454
)