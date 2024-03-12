package com.runnect.runnect.data

import java.io.IOException

sealed class Result<out T : Any> {
    data class Success<T : Any>(val body: T?) : Result<T>()
    data class Failure(val code: Int, val error: String?) : Result<Nothing>()
    data class NetworkError(val exception: IOException) : Result<Nothing>()
    data class Unexpected(val t: Throwable?) : Result<Nothing>()
}