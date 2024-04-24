package com.runnect.runnect.util.extension

import kotlinx.coroutines.flow.Flow

suspend fun <T> Flow<Result<T>>.collectResult(
    onSuccess: (T) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    collect { result ->
        result.fold(onSuccess, onFailure)
    }
}