package com.runnect.runnect.presentation.mypage.reward

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRewardViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _getStampListState = MutableLiveData<UiState>(UiState.Loading)
    val getStampListState: LiveData<UiState>
        get() = _getStampListState

    var stampList: MutableList<String> = mutableListOf()

    val errorMessage = MutableLiveData<String>()

    fun getStampList() {
        viewModelScope.launch {
            runCatching {
                _getStampListState.value = UiState.Loading
                userRepository.getMyStamp()
            }.onSuccess {
                stampList = it
                _getStampListState.value = UiState.Success
            }.onFailure {
                errorMessage.value = it.message
                _getStampListState.value = UiState.Failure
            }
        }
    }
}