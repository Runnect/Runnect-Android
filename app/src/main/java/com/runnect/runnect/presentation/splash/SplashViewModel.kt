package com.runnect.runnect.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _navigateEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateEvent: SharedFlow<Unit> = _navigateEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY)
            _isReady.value = true
            _navigateEvent.emit(Unit)
        }
    }

    companion object {
        const val SPLASH_DELAY = 1000L
    }
}
