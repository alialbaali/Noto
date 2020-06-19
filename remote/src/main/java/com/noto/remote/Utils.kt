package com.noto.remote

import io.ktor.client.request.HttpRequestBuilder

private const val AUTH_HEADER = "Authorization"

fun HttpRequestBuilder.authHeader(value: Any?): Unit =
    value?.let { headers.append(AUTH_HEADER, it.toString()) } ?: Unit