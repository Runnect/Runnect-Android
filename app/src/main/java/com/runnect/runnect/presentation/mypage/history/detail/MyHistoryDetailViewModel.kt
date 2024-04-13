package com.runnect.runnect.presentation.mypage.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.runnect.runnect.data.dto.request.RequestDeleteHistory
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitle
import com.runnect.runnect.data.dto.response.ResponseDeleteHistory
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitle
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.CourseTitle
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.collectResult
import com.runnect.runnect.util.mode.ScreenMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyHistoryDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _historyDeleteState = MutableLiveData<UiStateV2<Unit>>()
    val historyDeleteState: LiveData<UiStateV2<Unit>>
        get() = _historyDeleteState

    private val _titlePatchState = MutableLiveData<UiStateV2<String>>()
    val titlePatchState: LiveData<UiStateV2<String>>
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

    fun deleteHistory() = launchWithHandler {
        val requestDeleteHistory = RequestDeleteHistory(listOf(historyId))

        userRepository.putDeleteHistory(requestDeleteHistory)
            .onStart {
                _historyDeleteState.value = UiStateV2.Loading
            }.collectResult(
                onSuccess = {
                    _historyDeleteState.value = UiStateV2.Success(it)
                },
                onFailure = {
                    _historyDeleteState.value = UiStateV2.Failure(it.toLog())
                }
            )
    }

    fun patchHistoryTitle() = launchWithHandler {
        userRepository.patchHistoryTitle(
            historyId = historyId,
            requestPatchHistoryTitle = RequestPatchHistoryTitle(title)
        ).onStart {
            _titlePatchState.value = UiStateV2.Loading
        }.collectResult(
            onSuccess = {
                _titlePatchState.value = UiStateV2.Success(it)
            },
            onFailure = {
                _titlePatchState.value = UiStateV2.Failure(it.toLog())
            }
        )
    }
}
