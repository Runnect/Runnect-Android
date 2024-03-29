package com.runnect.runnect.presentation.mypage.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.UserUploadCourse
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyUploadViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private var _myUploadCourseState = MutableLiveData<UiState>()
    val myUploadCourseState: LiveData<UiState>
        get() = _myUploadCourseState

    private var _myUploadDeleteState = MutableLiveData<UiState>()
    val myUploadDeleteState: LiveData<UiState>
        get() = _myUploadDeleteState

    private var _myUploadCourses = mutableListOf<UserUploadCourse>()
    val myUploadCourses: List<UserUploadCourse>
        get() = _myUploadCourses

    val errorMessage = MutableLiveData<String>()

    private var _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean>
        get() = _editMode

    private var itemsToDeleteLiveData = MutableLiveData<List<Int>>()

    private var _itemsToDelete: MutableList<Int> = mutableListOf()
    val itemsToDelete: List<Int>
        get() = _itemsToDelete

    val selectCountMediator = MediatorLiveData<List<Int>>()

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }

    init {
        selectCountMediator.addSource(itemsToDeleteLiveData) {
            setSelectedItemsCount(_itemsToDelete.count())
        }
    }

    private var _selectedItemsCount = MutableLiveData(DEFAULT_SELECTED_COUNT)
    val selectedItemsCount: LiveData<Int>
        get() = _selectedItemsCount

    private fun setSelectedItemsCount(count: Int) {
        _selectedItemsCount.value = count
    }

    fun getUserUploadCourse() {
        launchWithHandler {
            userRepository.getUserUploadCourse()
                .onStart {
                    _myUploadCourseState.value = UiState.Loading
                }.collect { result ->
                    result.onSuccess {
                        _myUploadCourses = it.toMutableList()
                        _myUploadCourseState.value = if (it.isEmpty()) UiState.Empty else UiState.Success
                    }.onFailure {
                        errorMessage.value = it.message
                        _myUploadCourseState.value = UiState.Failure
                    }
                }
        }
    }

    fun deleteUploadCourse() {
        launchWithHandler {
            val requestDeleteUploadCourse = RequestDeleteUploadCourse(_itemsToDelete)
            userRepository.putDeleteUploadCourse(requestDeleteUploadCourse)
                .onStart {
                    _myUploadDeleteState.value = UiState.Loading
                    setSelectedItemsCount(DEFAULT_SELECTED_COUNT)
                }
                .collect { result ->
                    result.onSuccess {
                        _myUploadCourses.removeAll { it.id in itemsToDelete }
                        _myUploadDeleteState.value = UiState.Success

                        //모든 기록 삭제 시, 편집 모드 -> 읽기 모드
                        if (_myUploadCourses.isEmpty()) {
                            _myUploadCourseState.value = UiState.Empty
                            convertMode()
                        }
                    }.onFailure {
                        errorMessage.value = it.toLog()
                        _myUploadDeleteState.value = UiState.Failure
                    }
                }
        }
    }


    fun convertMode() {
        _editMode.value = !_editMode.value!!
    }

    fun getCourseCount(): String {
        return "총 기록 ${_myUploadCourses.count()}개"
    }

    fun modifyItemsToDelete(id: Int) {
        if (_itemsToDelete.contains(id)) {
            _itemsToDelete.remove(id)
        } else {
            _itemsToDelete.add(id)
        }
        itemsToDeleteLiveData.value = _itemsToDelete
    }

    fun clearItemsToDelete() {
        _itemsToDelete.clear()
        itemsToDeleteLiveData.value = _itemsToDelete
    }

    companion object {
        const val DEFAULT_SELECTED_COUNT = 0
    }
}