package com.noto.app.data.source

object FilterKeys {
    infix fun eq(value: String) = "eq.$value"
}