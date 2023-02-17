package com.runnect.runnect.presentation.mypage.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyUploadViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private var _myUploadCourseState = MutableLiveData<UiState>()
    val myUploadCourseState: LiveData<UiState>
        get() = _myUploadCourseState

    private var _myUploadCourseList = mutableListOf<UserUploadCourseDTO>()
    val myUploadCourseList: List<UserUploadCourseDTO>
        get() = _myUploadCourseList

    val errorMessage = MutableLiveData<String>()

    fun getUserUploadCourse() {
        viewModelScope.launch {
            runCatching {
                _myUploadCourseState.value = UiState.Loading
                userRepository.getUserUploadCourse()
            }.onSuccess {
                _myUploadCourseList = it
                _myUploadCourseState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _myUploadCourseState.value = UiState.Failure
            }
        }
    }
}