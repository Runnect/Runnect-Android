package com.runnect.runnect.presentation.mypage.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitle
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyHistoryDetailViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _historyDeleteState =
        MutableLiveData<UiStateV2<ResponseDeleteHistory?>>()
    val historyDeleteState: LiveData<UiStateV2<ResponseDeleteHistory?>>
        get() = _historyDeleteState

    private val _titlePatchState = MutableLiveData<UiStateV2<ResponsePatchHistoryTitle?>>()
    val titlePatchState: LiveData<UiStateV2<ResponsePatchHistoryTitle?>>
        get() = _titlePatchState

    val _title = MutableLiveData("")
    val title: String get() = _title.value ?: ""

    val isValidTitle: LiveData<Boolean> = _title.map { it.isNotBlank() }

    private var _currentScreenMode: ScreenMode = ScreenMode.ReadOnlyMode
    val currentScreenMode get() = _currentScreenMode

    private var savedTitle: String = ""
    private var historyId: Int = -1

    fun updateHistoryTitle(title: String) {
        _title.value = title
    }

    fun updateHistoryId(id: Int) {
        historyId = id
    }

    fun saveCurrentTitle() {
        savedTitle = title
    }

    fun restoreOriginalTitle() {
        _title.value = savedTitle
    }

    fun updateCurrentScreenMode(mode: ScreenMode) {
        _currentScreenMode = mode
    }

    fun deleteHistory() {
        viewModelScope.launch {
            _historyDeleteState.value = UiStateV2.Loading

            val deleteItems = listOf(historyId)
            userRepository.putDeleteHistory(RequestDeleteHistory(deleteItems))
                .onSuccess { response ->
                    _historyDeleteState.value = UiStateV2.Success(response)
                }.onFailure { t ->
                    _historyDeleteState.value = UiStateV2.Failure(t.message.toString())
                }
        }
    }

    fun patchHistoryTitle() {
        viewModelScope.launch {
            _titlePatchState.value = UiStateV2.Loading

            userRepository.patchHistoryTitle(
                historyId = historyId,
                requestPatchHistoryTitle = RequestPatchHistoryTitle(title)
            ).onSuccess { response ->
                _titlePatchState.value = UiStateV2.Success(response)
            }.onFailure { t ->
                _titlePatchState.value = UiStateV2.Failure(t.message.toString())
            }
        }
    }
}
