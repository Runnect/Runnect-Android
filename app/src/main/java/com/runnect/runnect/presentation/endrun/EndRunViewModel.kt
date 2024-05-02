package com.runnect.runnect.presentation.endrun

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.request.RequestPostRunningHistory
import com.runnect.runnect.data.dto.response.ResponsePostMyHistory
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class EndRunViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    BaseViewModel() {

    val distanceSum = MutableLiveData<Double>()
    val captureUri = MutableLiveData<Uri>()
    val departure = MutableLiveData<String>()
    val timerHourMinSec = MutableLiveData<String>()
    val paceTotal = MutableLiveData<String>()
    val courseId = MutableLiveData<Int>()
    val publicCourseId = MutableLiveData<Int?>()
    val editTextValue = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val currentTime = MutableLiveData<String>()
    val dataFrom = MutableLiveData<String>()

    private val _endRunState = MutableLiveData<UiState>(UiState.Empty)
    val endRunState: LiveData<UiState>
        get() = _endRunState

    fun postRecord(request: RequestPostRunningHistory) {
        launchWithHandler {
            courseRepository.postRecord(
                RequestPostRunningHistory(
                    courseId = request.courseId,
                    publicCourseId = request.publicCourseId,
                    title = request.title,
                    time = request.time,
                    pace = request.pace

                )
            ).onStart {
                _endRunState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    _endRunState.value = UiState.Success
                },
                onFailure = {
                    errorMessage.value = it.message
                    _endRunState.value = UiState.Failure
                }
            )
        }
    }
}