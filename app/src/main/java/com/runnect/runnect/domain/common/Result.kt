package com.runnect.runnect.domain.common

sealed interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Failure(
        val code: Int,
        val message: String?,
    ) : Result<Nothing>

    fun isSuccess(): Boolean = this is Success<T>
    fun isFailure(): Boolean = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    fun exceptionOrNull(): Throwable? = when (this) {
        is Success -> null
        is Failure -> RunnectException(code, message)
    }
}

fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

fun <T> Result<T>.onFailure(action: (RunnectException) -> Unit): Result<T> {
    if (this is Result.Failure) action(RunnectException(code, message))
    return this
}