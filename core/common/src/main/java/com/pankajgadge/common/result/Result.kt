package com.pankajgadge.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Extension function to check if Result is Success
 */
fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success

/**
 * Extension function to check if Result is Error
 */
fun <T> Result<T>.isError(): Boolean = this is Result.Error

/**
 * Extension function to check if Result is Loading
 */
fun <T> Result<T>.isLoading(): Boolean = this is Result.Loading

/**
 * Extension function to get data or null
 */
fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

/**
 * Extension function to get data or default value
 */
fun <T> Result<T>.getOrDefault(default: T): T {
    return when (this) {
        is Result.Success -> data
        else -> default
    }
}

/**
 * Extension function to get exception or null
 */
fun <T> Result<T>.exceptionOrNull(): Throwable? {
    return when (this) {
        is Result.Error -> exception
        else -> null
    }
}

/**
 * Extension function to map Success data
 *
 * Example:
 * ```
 * val result: Result<List<Quiz>> = repository.getQuizzes()
 * val mapped: Result<Int> = result.map { it.size }
 * ```
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception)
        is Result.Loading -> Result.Loading
    }
}

/**
 * Extension function to handle Result states
 *
 * Example:
 * ```
 * result.onSuccess { data ->
 *     println("Got ${data.size} quizzes")
 * }.onError { error ->
 *     println("Error: ${error.message}")
 * }.onLoading {
 *     println("Loading...")
 * }
 * ```
 */
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

/**
 * Extension function to convert Result to nullable value
 * Returns data if Success, null otherwise
 */
fun <T> Result<T>.toNullable(): T? = getOrNull()

/**
 * Utility function to create Success Result
 */
fun <T> successResult(data: T): Result<T> = Result.Success(data)

/**
 * Utility function to create Error Result
 */
fun <T> errorResult(exception: Throwable): Result<T> = Result.Error(exception)

/**
 * Utility function to create Error Result from message
 */
fun <T> errorResult(message: String): Result<T> = Result.Error(Exception(message))

/**
 * Utility function to create Loading Result
 */
fun <T> loadingResult(): Result<T> = Result.Loading

// ============================================
// ADVANCED: Flow Extensions (Optional)
// ============================================

/**
 * Extension to wrap Flow emissions in Result
 *
 * Usage:
 * ```
 * fun getQuizzes(): Flow<Result<List<Quiz>>> {
 *     return flow {
 *         val data = api.getQuizzes()
 *         emit(data)
 *     }.asResult()
 * }
 * ```
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }
}

/**
 * Extension to handle Flow<Result<T>> in UI
 *
 * Usage:
 * ```
 * repository.getQuizzes()
 *     .handleResult(
 *         onLoading = { showLoading() },
 *         onSuccess = { data -> showQuizzes(data) },
 *         onError = { error -> showError(error) }
 *     )
 * ```
 */
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
