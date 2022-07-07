package com.noto.app.util

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend inline fun <reified T> HttpResponse.getOrElse(callback: (HttpResponse) -> T): T {
    return if (status.isSuccess()) {
        body<T>()
    } else {
        callback(this)
    }
}

fun unhandledError(message: String): Nothing = error("Unhandled error: $message")