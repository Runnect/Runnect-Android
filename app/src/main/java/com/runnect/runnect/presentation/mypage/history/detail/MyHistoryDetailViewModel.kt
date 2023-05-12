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
    val deleteState: LiveData<UiState>
        get() = _deleteState
    private val _deleteState = MutableLiveData<UiState>()

    val editState: LiveData<UiState>
        get() = _editState
    private val _editState = MutableLiveData<UiState>()

    val errorMessage = MutableLiveData<String>()

    var editMode = MutableLiveData(READ_MODE)

    var mapImg: MutableLiveData<String> = MutableLiveData<String>(DEFAULT_IMAGE)
    val title: MutableLiveData<String> = MutableLiveData()
    var titleForInterruption = ""
    var date = DEFAULT_DATE
    var departure = DEFAULT_DEPARTURE
    var distance = DEFAULT_DISTANCE
    var time = DEFAULT_TIME
    var pace = DEFAULT_PACE
    var historyIdToDelete = listOf(0)

    fun setTitle(titleParam: String) {
        title.value = titleParam
    }

    fun deleteHistory() {
        viewModelScope.launch {
            runCatching {
                _deleteState.value = UiState.Loading
                userRepository.putDeleteHistory(RequestDeleteHistory(historyIdToDelete))
            }.onSuccess {
                _deleteState.value = UiState.Success
            }.onFailure {
                _deleteState.value = UiState.Failure
                errorMessage.value = it.message
            }
        }
    }

    fun editHistoryTitle() {
        viewModelScope.launch {
            runCatching {
                _editState.value = UiState.Loading
                userRepository.patchHistoryTitle(
                    historyIdToDelete[0],
                    RequestEditHistoryTitle(title.value.toString())
                )
            }.onSuccess {
                _editState.value = UiState.Success
            }.onFailure {
                _editState.value = UiState.Failure
                errorMessage.value = it.message
            }
        }
    }

    companion object {
        const val DEFAULT_IMAGE =
            "https://insopt-bucket-rin.s3.ap-northeast-2.amazonaws.com/1683259309418_temprentpk5892730152618614049.png"
        const val DEFAULT_DATE = "2023.00.00"
        const val DEFAULT_DEPARTURE = "서울시"
        const val DEFAULT_PACE = "0’00"
        const val DEFAULT_TIME = "00:00:00"
        const val DEFAULT_DISTANCE = "0.0"
        const val READ_MODE = "readMode"
    }
}