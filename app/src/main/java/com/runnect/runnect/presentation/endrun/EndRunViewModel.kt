package com.runnect.runnect.presentation.endrun

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.model.RequestPostRecordDto
import com.runnect.runnect.data.model.ResponsePostRecordDto
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EndRunViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {

    val distanceSum = MutableLiveData<Double>()
    val captureUri = MutableLiveData<Uri>()
    val departure = MutableLiveData<String>()
    val timerHourMinSec = MutableLiveData<String>()
    val paceTotal = MutableLiveData<String>()
    val courseId = MutableLiveData<Int>()
    val publicCourseId = MutableLiveData<Int?>()
    val editTextValue = MutableLiveData<String>()
    val uploadResult = MutableLiveData<ResponsePostRecordDto>()
    val errorMessage = MutableLiveData<String>()
    val currentTime = MutableLiveData<String>() //현재 시간
    val dataFrom = MutableLiveData<String>()

    private val _endRunState = MutableLiveData<UiState>(UiState.Empty)
    val endRunState: LiveData<UiState>
        get() = _endRunState


    fun postRecord(request: RequestPostRecordDto) {
        viewModelScope.launch {
            runCatching {
                _endRunState.value = UiState.Loading
                courseRepository.postRecord(
                    RequestPostRecordDto(
                        request.courseId,
                        request.publicCourseId,
                        request.title,
                        request.time,
                        request.pace
                    )
                )
            }.onSuccess {
                uploadResult.value = it.body()
                _endRunState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _endRunState.value = UiState.Failure
            }
        }
    }

}