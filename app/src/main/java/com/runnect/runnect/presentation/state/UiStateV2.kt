package com.runnect.runnect.presentation.state

sealed interface UiStateV2<out T> {
    object Empty : UiStateV2<Nothing>

    object Loading : UiStateV2<Nothing>

    data class Success<T>(
        val data: T
    ) : UiStateV2<T>

    data class Failure(
        val msg: String
    ) : UiStateV2<Nothing>
}
