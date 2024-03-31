package com.runnect.runnect.presentation.mypage.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.HistoryInfoDTO
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

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

    private var _itemsToDeleteLiveData = MutableLiveData<List<Int>>()
    val itemsToDeleteLiveData: MutableLiveData<List<Int>>
        get() = _itemsToDeleteLiveData

    private var _itemsToDelete: MutableList<Int> = mutableListOf()
    val itemsToDelete: List<Int>
        get() = _itemsToDelete

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

    fun getHistoryCount(): String {
        return "총 기록 ${_historyItems.size}개"
    }

    fun convertMode() {
        _editMode.value = !_editMode.value!!
    }

    fun getRecord() = launchWithHandler {
        userRepository.getRecord()
            .onStart {
                _historyItems.clear()
                _historyState.value = UiState.Loading
            }.collect { result ->
                result.onSuccess {
                    _historyItems = it.toMutableList()
                    _historyState.value = if (it.isEmpty()) UiState.Empty else UiState.Success
                }.onFailure {
                    errorMessage.value = it.toLog()
                    _historyState.value = UiState.Failure
                }
            }
    }

    fun deleteHistory() = launchWithHandler {
        val requestDeleteHistory = RequestDeleteHistory(
            recordIdList = _itemsToDelete
        )

        userRepository.putDeleteHistory(requestDeleteHistory)
            .onStart {
                _historyDeleteState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    _historyItems = _historyItems.filterNot { item ->
                        itemsToDelete.contains(item.id)
                    }.toMutableList()
                    _historyDeleteState.value = UiState.Success

                    //모든 기록 삭제 시, 편집 모드 취소
                    if (_historyItems.isEmpty()) {
                        _historyState.value = UiState.Empty
                        convertMode()
                    }
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                    _historyDeleteState.value = UiState.Failure
                }
            )
    }

    companion object {
        const val DEFAULT_SELECTED_COUNT = 0
    }

}