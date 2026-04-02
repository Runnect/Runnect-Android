package com.runnect.runnect.presentation.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ScreenRefreshEvent {
    data object RefreshDiscoverCourses : ScreenRefreshEvent
    data object RefreshStorageScrap : ScreenRefreshEvent
}

@Singleton
class ScreenRefreshEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<ScreenRefreshEvent>()
    val events: SharedFlow<ScreenRefreshEvent> = _events.asSharedFlow()

    suspend fun emit(event: ScreenRefreshEvent) {
        _events.emit(event)
    }
}
