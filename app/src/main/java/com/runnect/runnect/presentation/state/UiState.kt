package com.runnect.runnect.presentation.state

sealed class UiState {
    object Success : UiState()
    object Empty : UiState()
    object Failure : UiState()
    object Loading : UiState()
}
