package com.runnect.runnect.presentation.mypage.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRewardViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _stampState = MutableLiveData<UiState>(UiState.Loading)
    val stampState:LiveData<UiState>
        get() = _stampState

    var stampList:MutableList<String> = mutableListOf()

    fun getStampList() {
        viewModelScope.launch {
            runCatching {
                _stampState.value = UiState.Loading
                userRepository.getMyStamp()
            }.onSuccess {
                stampList = it
                _stampState.value = UiState.Success
            }.onFailure {
                _stampState.value = UiState.Failure
            }
        }
    }
}