package com.pankajgadge.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}

// Extension functions
fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success
fun <T> Result<T>.isError(): Boolean = this is Result.Error
fun <T> Result<T>.isLoading(): Boolean = this is Result.Loading

fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

fun <T> Result<T>.getOrDefault(default: T): T {
    return when (this) {
        is Result.Success -> data
        else -> default
    }
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception)
        is Result.Loading -> Result.Loading
    }
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

fun <T> Result<T>.exceptionOrNull(): Throwable? {
    return when (this) {
        is Result.Error -> exception
        else -> null
    }
}

fun <T> Result<T>.toNullable(): T? = getOrNull()


fun <T> successResult(data: T): Result<T> = Result.Success(data)

fun <T> errorResult(exception: Throwable): Result<T> = Result.Error(exception)

fun <T> errorResult(message: String): Result<T> = Result.Error(Exception(message))

fun <T> loadingResult(): Result<T> = Result.Loading

// ============================================
// ADVANCED: Flow Extensions (Optional)
// ============================================

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }
}

inline fun <T> Flow<Result<T>>.handleResult(
    crossinline onLoading: () -> Unit = {},
    crossinline onSuccess: (T) -> Unit = {},
    crossinline onError: (Throwable) -> Unit = {}
): Flow<Result<T>> {
    return this.map { result ->
        when (result) {
            is Result.Loading -> onLoading()
            is Result.Success -> onSuccess(result.data)
            is Result.Error -> onError(result.exception)
        }
        result
    }
}
