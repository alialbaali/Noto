package com.noto.domain.schema

data class ResponseSchema<T>(
    val success: Boolean,

    val error: String? = null,

    val data: T? = null
)