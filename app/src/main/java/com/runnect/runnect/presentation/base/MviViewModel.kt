package com.runnect.runnect.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class MviViewModel<State, Intent, Effect>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    val currentState: State get() = _state.value

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag(throwable::class.java.simpleName).e(throwable)
        handleException(throwable)
    }

    fun intent(intent: Intent) {
        viewModelScope.launch(exceptionHandler) {
            handleIntent(intent)
        }
    }

    protected abstract suspend fun handleIntent(intent: Intent)

    protected fun reduce(reducer: State.() -> State) {
        _state.value = currentState.reducer()
    }

    protected fun postEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    protected fun <T> collectFlow(
        flow: suspend () -> Flow<Result<T>>,
        onLoading: () -> Unit = {},
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch(exceptionHandler) {
            flow()
                .onStart { onLoading() }
                .catch { onFailure(it) }
                .collect { result ->
                    result.fold(onSuccess, onFailure)
                }
        }
    }

    protected open fun handleException(throwable: Throwable) {
        Timber.e(throwable)
    }
}
