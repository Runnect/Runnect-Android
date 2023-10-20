package com.runnect.runnect.presentation.mypage.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestEditHistoryTitle
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyHistoryDetailViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _historyDeleteState = MutableLiveData<UiState>()
    val historyDeleteState: LiveData<UiState>
        get() = _historyDeleteState

    private val _titleEditState = MutableLiveData<UiState>()
    val titleEditState: LiveData<UiState>
        get() = _titleEditState

    var editMode = MutableLiveData(READ_MODE)

    val _title = MutableLiveData("")
    val title: String get() = _title.value ?: ""

    val errorMessage = MutableLiveData<String>()

    var titleForInterruption = ""
    var date = DEFAULT_DATE
    var departure = DEFAULT_DEPARTURE
    var distance = DEFAULT_DISTANCE
    var time = DEFAULT_TIME
    var pace = DEFAULT_PACE
    var currentHistoryId = listOf(0)

    fun setInitialHistoryTitle(title: String) {
        _title.value = title
    }

    fun setCurrentHistoryId(id: Int) {
        currentHistoryId = listOf(id)
    }

    fun deleteHistory() {
        viewModelScope.launch {
            runCatching {
                _historyDeleteState.value = UiState.Loading
                userRepository.putDeleteHistory(RequestDeleteHistory(currentHistoryId))
            }.onSuccess {
                _historyDeleteState.value = UiState.Success
            }.onFailure {
                _historyDeleteState.value = UiState.Failure
                errorMessage.value = it.message
            }
        }
    }

    fun editHistoryTitle() {
        viewModelScope.launch {
            runCatching {
                _titleEditState.value = UiState.Loading
                userRepository.patchHistoryTitle(
                    currentHistoryId[0],
                    RequestEditHistoryTitle(title)
                )
            }.onSuccess {
                _titleEditState.value = UiState.Success
            }.onFailure {
                _titleEditState.value = UiState.Failure
                errorMessage.value = it.message
            }
        }
    }

    companion object {
        const val DEFAULT_DATE = "2023.00.00"
        const val DEFAULT_DEPARTURE = "서울시"
        const val DEFAULT_PACE = "0’00"
        const val DEFAULT_TIME = "00:00:00"
        const val DEFAULT_DISTANCE = "0.0"
        const val READ_MODE = "readMode"
    }
}