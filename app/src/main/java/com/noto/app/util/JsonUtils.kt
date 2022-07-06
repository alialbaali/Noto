package com.noto.app.util

import kotlinx.serialization.json.Json

val NotoDefaultJson = Json {
    isLenient = true
    allowStructuredMapKeys = true
    coerceInputValues = true
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
    prettyPrint = true
}