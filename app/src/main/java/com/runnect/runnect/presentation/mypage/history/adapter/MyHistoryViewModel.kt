package com.runnect.runnect.presentation.mypage.history.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.RecordInfoDTO
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyHistoryViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private var _recordState = MutableLiveData<UiState>()
    val recordState: LiveData<UiState>
        get() = _recordState

    private var _recordList: MutableList<RecordInfoDTO> = mutableListOf()
    val recordList: List<RecordInfoDTO>
        get() = _recordList

    val errorMessage = MutableLiveData<String>()

    fun getRecord() {
        viewModelScope.launch {
            runCatching {
                _recordState.value = UiState.Loading
                userRepository.getRecord()
            }.onSuccess {
                _recordList = it
                _recordState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _recordState.value = UiState.Failure
            }
        }
    }

}