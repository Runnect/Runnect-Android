package com.runnect.runnect.presentation.mypage.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistoryDto
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyHistoryViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private var _historyState = MutableLiveData<UiState>()
    val historyState: LiveData<UiState>
        get() = _historyState

    private var _historyDeleteState = MutableLiveData<UiState>()
    val historyDeleteState: LiveData<UiState>
        get() = _historyDeleteState

    private var _historyItems: MutableList<HistoryInfoDTO> = mutableListOf()
    val historyItems: List<HistoryInfoDTO>
        get() = _historyItems

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

    val errorMessage = MutableLiveData<String>()

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

    private fun setSelectedItemsCount(count: Int) {
        _selectedItemsCount.value = count
    }


    fun getHistoryCount(): String {
        return "총 기록 ${_historyItems.size}개"
    }

    fun convertMode() {
        _editMode.value = !_editMode.value!!
    }

    fun getRecord() {
        _historyState.value = UiState.Loading
        _historyItems = mutableListOf()
        viewModelScope.launch {
            runCatching {
                _historyState.value = UiState.Loading
                userRepository.getRecord()
            }.onSuccess {
                if (it.isEmpty()) {
                    _historyState.value = UiState.Empty
                } else {
                    _historyItems = it
                    _historyState.value = UiState.Success
                }
            }.onFailure {
                errorMessage.value = it.message
                _historyState.value = UiState.Failure
            }
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            runCatching {
                _historyDeleteState.value = UiState.Loading
                setSelectedItemsCount(
                    count = DEFAULT_SELECTED_COUNT
                )
                userRepository.putDeleteHistory(
                    RequestDeleteHistoryDto(
                        recordIdList = _itemsToDelete
                    )
                )
            }.onSuccess {
                _historyItems =
                    _historyItems.filter { !itemsToDelete.contains(it.id) }.toMutableList()
                _historyDeleteState.value = UiState.Success
                //모든 기록 삭제 시, 편집 모드 취소
                if (_historyItems.isEmpty()) {
                    _historyState.value = UiState.Empty
                    convertMode()
                }
            }.onFailure {
                errorMessage.value = it.message
                _historyDeleteState.value = UiState.Failure
            }
        }
    }

    companion object {
        const val DEFAULT_SELECTED_COUNT = 0
    }

}