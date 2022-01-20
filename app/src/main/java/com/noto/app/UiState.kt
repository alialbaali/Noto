package com.noto.app

import com.noto.app.UiState.Loading
import com.noto.app.UiState.Success

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val value: T) : UiState<T>
}

inline fun <T, R> UiState<T>.map(transform: (value: T) -> R): UiState<R> = when (this) {
    is Loading -> Loading
    is Success -> Success(transform(value))
}

fun <T> UiState<T>.getOrDefault(defaultValue: T) = when (this) {
    is Loading -> defaultValue
    is Success -> value
}