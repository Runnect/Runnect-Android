package com.runnect.runnect.util.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

suspend fun <T> Flow<Result<T>>.collectResult(
    onSuccess: ((T) -> Unit)? = null,
    onFailure: ((Throwable) -> Unit)? = null
) {
    collect { result ->
        result.fold(onSuccess ?: {}, onFailure ?: {})
    }
}

fun <T> Flow<Result<T>>.onEachResult(
    onSuccess: ((T) -> Unit)? = null,
    onFailure: ((Throwable) -> Unit)? = null
): Flow<Result<T>> {
    return onEach { result ->
        result.fold(onSuccess ?: {}, onFailure ?: {})
    }
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

fun LifecycleOwner.repeatOnStarted(vararg blocks: suspend CoroutineScope.() -> Unit) {
    blocks.forEach {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, it)
        }
    }
}