package com.noto.app.data.model.remote


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("access_token")
    val accessToken: String = "", // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNjU2NDY5MzcyLCJzdWIiOiIyN2IzOWY0ZC1mZDFkLTRmNjEtODVlMS0yMjFhNTg1OTU0NTQiLCJlbWFpbCI6ImFsaTFAYWxiYWFsaS5jb20iLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6ImVtYWlsIiwicHJvdmlkZXJzIjpbImVtYWlsIl19LCJ1c2VyX21ldGFkYXRhIjp7fSwicm9sZSI6ImF1dGhlbnRpY2F0ZWQifQ.ru2fRK6wJHmmeHVz0I-81erizonj23MHqT-T3isntW8
    @SerialName("token_type")
    val tokenType: String = "", // bearer
    @SerialName("expires_in")
    val expiresIn: Int = 0, // 3600
    @SerialName("refresh_token")
    val refreshToken: String = "", // 6LLVAZOGqauglkoP2z6j_w
    @SerialName("user")
    val user: RemoteAuthUser = RemoteAuthUser(),
)