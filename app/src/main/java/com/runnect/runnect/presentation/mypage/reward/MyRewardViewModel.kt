package com.runnect.runnect.presentation.mypage.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MyRewardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _getStampListState = MutableLiveData<UiState>(UiState.Loading)
    val getStampListState: LiveData<UiState>
        get() = _getStampListState

    var stampList: MutableList<String> = mutableListOf()

    val errorMessage = MutableLiveData<String>()

    fun getStampList() = launchWithHandler {
        userRepository.getMyStamp()
            .onStart {
                _getStampListState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    stampList = it.toMutableList()
                    _getStampListState.value = UiState.Success
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                    _getStampListState.value = UiState.Failure
                }
            )
    }
}