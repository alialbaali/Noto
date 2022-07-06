package com.noto.app

import com.noto.app.UiState.*

sealed interface UiState<out T> {
    object Empty : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val value: T) : UiState<T>
    data class Failure(val exception: Throwable) : UiState<Nothing>
}

inline fun <T, R> UiState<T>.map(transform: (value: T) -> R): UiState<R> = when (this) {
    is Empty -> Empty
    is Loading -> Loading
    is Success -> Success(transform(value))
    is Failure -> Failure(exception)
}

fun <T> UiState<T>.getOrDefault(defaultValue: T) = when (this) {
    is Empty -> defaultValue
    is Loading -> defaultValue
    is Success -> value
    is Failure -> defaultValue
}

inline fun <T> UiState<T>.fold(
    onEmpty: () -> Unit = {},
    onLoading: () -> Unit = {},
    onSuccess: (T) -> Unit = {},
    onFailure: (Throwable) -> Unit = {},
): UiState<T> {
    when (this) {
        is Empty -> onEmpty()
        is Loading -> onLoading()
        is Success -> onSuccess(value)
        is Failure -> onFailure(exception)
    }
    return this
}

fun <T> Result<T>.toUiState() = fold(
    onSuccess = { Success(it) },
    onFailure = { Failure(it) },
)