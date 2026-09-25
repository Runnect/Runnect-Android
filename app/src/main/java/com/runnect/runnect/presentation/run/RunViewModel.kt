package com.runnect.runnect.presentation.run

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 러닝 화면 UI 상태. 러닝 진행 상태의 원본은 러닝 서비스(TimerService)의 RunTracker이고,
 * 화면은 서비스에 바인딩된 동안 그 상태를 이 ViewModel로 옮겨 그린다. 서비스가 계속 살아 있으므로
 * 화면이 재생성(configuration change, 시스템 회수)돼도 다시 바인딩하면 같은 상태를 이어받는다.
 */
class RunViewModel : ViewModel() {
    var distanceSum = MutableLiveData(0.0)
    val departure = MutableLiveData<String>()
    val dataFrom = MutableLiveData<String>()
    var courseId = MutableLiveData<Int>()
    var publicCourseId = MutableLiveData<Int?>()

    private val _trackingState = MutableStateFlow(RunTrackingState())
    val trackingState: StateFlow<RunTrackingState> = _trackingState.asStateFlow()

    fun render(state: RunTrackingState) {
        _trackingState.value = state
    }
}
