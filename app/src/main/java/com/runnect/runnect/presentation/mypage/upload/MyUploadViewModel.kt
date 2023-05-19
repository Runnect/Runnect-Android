package com.runnect.runnect.presentation.mypage.upload

import androidx.lifecycle.*
import com.runnect.runnect.data.dto.UserUploadCourseDTO
import com.runnect.runnect.data.dto.request.RequestDeleteUploadCourse
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

    private var _myUploadDeleteState = MutableLiveData<UiState>()
    val myUploadDeleteState: LiveData<UiState>
        get() = _myUploadDeleteState

    private var _myUploadCourses = mutableListOf<UserUploadCourseDTO>()
    val myUploadCourses: List<UserUploadCourseDTO>
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
        viewModelScope.launch {
            runCatching {
                _myUploadCourseState.value = UiState.Loading
                userRepository.getUserUploadCourse()
            }.onSuccess {
                _myUploadCourses = it
                if (_myUploadCourses.isEmpty()) {
                    _myUploadCourseState.value = UiState.Empty
                } else {
                    _myUploadCourseState.value = UiState.Success
                }
            }.onFailure {
                errorMessage.value = it.message
                _myUploadCourseState.value = UiState.Failure
            }
        }
    }

    fun deleteUploadCourse() {
        viewModelScope.launch {
            runCatching {
                _myUploadDeleteState.value = UiState.Loading
                setSelectedItemsCount(DEFAULT_SELECTED_COUNT)
                userRepository.putDeleteUploadCourse(RequestDeleteUploadCourse(_itemsToDelete))
            }.onSuccess {
                _myUploadCourses =
                    _myUploadCourses.filter { !itemsToDelete.contains(it.id) }.toMutableList()
                _myUploadDeleteState.value = UiState.Success
                //모든 기록 삭제 시, 편집 모드 -> 읽기 모드
                if (_myUploadCourses.isEmpty()) {
                    _myUploadCourseState.value = UiState.Empty
                    convertMode()
                }
            }.onFailure {
                errorMessage.value = it.message
                _myUploadDeleteState.value = UiState.Failure
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