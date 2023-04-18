package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseDetailDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.model.DetailToRunData
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {
    private var _courseDetailState = MutableLiveData<UiState>(UiState.Loading)
    val courseDetailState: LiveData<UiState>
        get() = _courseDetailState
    private var _courseDetail = CourseDetailDTO(
        R.drawable.user_profile_basic,
        "1",
        "1",
        1,
        "1",
        "1",
        "1",
        1,
        "1",
        false,
        "1",
        listOf(listOf(1.1))
    )
    val courseDetail: CourseDetailDTO
        get() = _courseDetail


    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                _courseDetailState.value = UiState.Loading
                Timber.d("runCatching : 상세코스 UiState ${_courseDetailState.value}")
                courseRepository.getCourseDetail(courseId)
            }.onSuccess {
                _courseDetail = it
                _courseDetailState.value = UiState.Success
//                Timber.d("Success : 상세코스 값??? $it")
                Timber.d("Success : 상세코스 _courseDetail값??? $_courseDetail")
                Timber.d("Success : 상세코스 UiState ${_courseDetailState.value}")
            }.onFailure {
                Timber.d(it.message)
                Timber.d(it.cause)
                Timber.d(it.localizedMessage)
                _courseDetailState.value = UiState.Failure
                Timber.d("Fail : 상세코스 UiState ${_courseDetailState.value}")
                Timber.d("Fail 메세지 왜??? ${it.message}")
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            runCatching {
                courseRepository.postCourseScrap(RequestCourseScrap(id, scrapTF.toString()))
            }.onSuccess {
                Timber.d("onSuccess 메세지 : $it")
            }.onFailure {
                Timber.d("onFailure 메세지 : $it")
            }
        }
    }
}