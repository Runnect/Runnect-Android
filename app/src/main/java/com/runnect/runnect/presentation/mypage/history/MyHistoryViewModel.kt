package com.runnect.runnect.presentation.mypage.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.HistoryInfoDTO
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

    private var _historyItems: MutableList<HistoryInfoDTO> = mutableListOf()
    val historyItem: List<HistoryInfoDTO>
        get() = _historyItems

    private var _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean>
        get() = _editMode

    private var itemsToDelete: MutableList<Int> = mutableListOf()

    private var _selectedItemsCount = MutableLiveData<Int>()
    val selectedItemsCount:LiveData<Int>
        get() = _selectedItemsCount

    val errorMessage = MutableLiveData<String>()

    fun modifyItemsToDelete(id: Int) {
        if(itemsToDelete.contains(id)){
            itemsToDelete.remove(id)
            setSelectedItemsCount()
        }
        else{
            itemsToDelete.add(id)
            setSelectedItemsCount()
        }
    }

    fun clearItemsToDelete() {
        itemsToDelete.clear()
        setSelectedItemsCount()
    }

    private fun setSelectedItemsCount() {
        _selectedItemsCount.value = itemsToDelete.count()
    }

    fun getHistoryCount(): String {
        return "총 기록 ${_historyItems.size}개"
    }

    fun convertMode() {
        _editMode.value = !_editMode.value!!
    }

    fun getRecord() {
        viewModelScope.launch {
            runCatching {
                _historyState.value = UiState.Loading
                userRepository.getRecord()
            }.onSuccess {
                _historyItems = it
                _historyState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _historyState.value = UiState.Failure
            }
        }
    }

}