package com.runnect.runnect.presentation.mypage.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
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

    val isValidTitle: LiveData<Boolean> = _title.map { it.isNotBlank() }

    private var historyId: Int = -1

    fun updateHistoryTitle(title: String) {
        _title.value = title
    }

    fun updateHistoryId(id: Int) {
        historyId = id
    }

    fun deleteHistory() {
        viewModelScope.launch {
            _historyDeleteState.value = UiStateV2.Loading

            val deleteItems = listOf(historyId)
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
                historyId,
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
}
