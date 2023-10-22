package com.runnect.runnect.presentation.mypage.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestDeleteHistoryDto
import com.runnect.runnect.data.dto.request.RequestPatchHistoryTitleDto
import com.runnect.runnect.data.dto.response.ResponseDeleteHistoryDto
import com.runnect.runnect.data.dto.response.ResponsePatchHistoryTitleDto
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyHistoryDetailViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    private val _historyDeleteState =
        MutableLiveData<UiStateV2<ResponseDeleteHistoryDto?>>()
    val historyDeleteState: LiveData<UiStateV2<ResponseDeleteHistoryDto?>>
        get() = _historyDeleteState

    private val _titlePatchState = MutableLiveData<UiStateV2<ResponsePatchHistoryTitleDto?>>()
    val titlePatchState: LiveData<UiStateV2<ResponsePatchHistoryTitleDto?>>
        get() = _titlePatchState

    val _title = MutableLiveData("")
    val title: String get() = _title.value ?: ""

    var titleForInterruption = ""
    var date = DEFAULT_DATE
    var departure = DEFAULT_DEPARTURE
    var distance = DEFAULT_DISTANCE
    var time = DEFAULT_TIME
    var pace = DEFAULT_PACE
    private var currentHistoryId: Int = -1

    fun setInitialHistoryTitle(title: String) {
        _title.value = title
    }

    fun setCurrentHistoryId(id: Int) {
        currentHistoryId = id
    }

    fun restoreInitialTitle(title: String) {
        _title.value = title
    }

    fun deleteHistory() {
        viewModelScope.launch {
            _historyDeleteState.value = UiStateV2.Loading

            val deleteItems = listOf(currentHistoryId)
            userRepository.putDeleteHistory(RequestDeleteHistoryDto(deleteItems))
                .onSuccess { response ->
                    _historyDeleteState.value = UiStateV2.Success(response)
                    Timber.d("SUCCESS DELETE HISTORY")
                }.onFailure { t ->
                    _historyDeleteState.value = UiStateV2.Failure(t.message.toString())

                    if (t is HttpException) {
                        Timber.e("HTTP FAIL DELETE HISTORY: ${t.code()} ${t.message()}")
                        return@launch
                    }

                    Timber.e("FAIL DELETE HISTORY: ${t.message}")
                }
        }
    }

    fun patchHistoryTitle() {
        viewModelScope.launch {
            _titlePatchState.value = UiStateV2.Loading

            userRepository.patchHistoryTitle(
                currentHistoryId,
                RequestPatchHistoryTitleDto(title)
            ).onSuccess { response ->
                _titlePatchState.value = UiStateV2.Success(response)
                Timber.d("SUCCESS PATCH HISTORY TITLE")
            }.onFailure { t ->
                _titlePatchState.value = UiStateV2.Failure(t.message.toString())

                if (t is HttpException) {
                    Timber.e("HTTP FAIL PATCH HISTORY TITLE: ${t.code()} ${t.message()}")
                    return@launch
                }

                Timber.e("FAIL PATCH HISTORY TITLE: ${t.message}")
            }
        }
    }

    companion object {
        const val DEFAULT_DATE = "2023.00.00"
        const val DEFAULT_DEPARTURE = "서울시"
        const val DEFAULT_PACE = "0’00"
        const val DEFAULT_TIME = "00:00:00"
        const val DEFAULT_DISTANCE = "0.0"
    }
}