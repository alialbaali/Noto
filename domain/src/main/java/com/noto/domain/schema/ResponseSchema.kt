package com.noto.domain.schema

data class ResponseSchema(
    val success: Boolean,
    val error: String?,
    val data: Any?
)