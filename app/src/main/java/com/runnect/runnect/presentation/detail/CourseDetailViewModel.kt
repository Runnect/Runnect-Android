package com.runnect.runnect.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseDetailDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.mypage.upload.MyUploadViewModel
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(private val courseRepository: CourseRepository, private val userRepository: UserRepository) :
    ViewModel() {
    private var _courseDetailState = MutableLiveData<UiState>(UiState.Loading)
    val courseDetailState: LiveData<UiState>
        get() = _courseDetailState

    private var _myUploadDeleteState = MutableLiveData<UiState>()
    val myUploadDeleteState: LiveData<UiState>
        get() = _myUploadDeleteState

    private var _itemsToDelete: MutableList<Int> = mutableListOf()
    val itemsToDelete: List<Int>
        get() = _itemsToDelete

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

    val errorMessage = MutableLiveData<String>()

    fun getCourseDetail(courseId: Int) {
        viewModelScope.launch {
            runCatching {
                _courseDetailState.value = UiState.Loading
                courseRepository.getCourseDetail(courseId)
            }.onSuccess {
                _courseDetail = it
                _courseDetailState.value = UiState.Success
            }.onFailure {
                _courseDetailState.value = UiState.Failure
                errorMessage.value = it.message
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            runCatching {
                courseRepository.postCourseScrap(RequestCourseScrap(id, scrapTF.toString()))
            }.onSuccess {
            }.onFailure {
                errorMessage.value = it.message
            }
        }
    }

    fun deleteUploadCourse(id: Int) {
        _itemsToDelete = mutableListOf(id)
        viewModelScope.launch {
            runCatching {
                _myUploadDeleteState.value = UiState.Loading
                userRepository.putDeleteUploadCourse(RequestDeleteUploadCourse(_itemsToDelete))
            }.onSuccess {
                _myUploadDeleteState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _myUploadDeleteState.value = UiState.Failure
            }
        }
    }
}