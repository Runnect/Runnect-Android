package com.runnect.runnect.presentation.mypage.history.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.runnect.runnect.domain.UserRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyHistoryDetailViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {
    val uiState: LiveData<UiState>
        get() = _uiState
    private val _uiState = MutableLiveData<UiState>()

    val title: MutableLiveData<String> = MutableLiveData()
    val date: MutableLiveData<String> = MutableLiveData()
    val departure: MutableLiveData<String> = MutableLiveData()
    val distance: MutableLiveData<String> = MutableLiveData(DEFAULT_DISTANCE)
    val time: MutableLiveData<String> =
        MutableLiveData(DEFAULT_TIME)
    val pace: MutableLiveData<String> = MutableLiveData(DEFAULT_PACE)


    fun setTitle(titleParam: String) {
        title.value = titleParam
    }

    companion object {
        const val DEFAULT_PACE = "0’00"
        const val DEFAULT_TIME = "00:00:00"
        const val DEFAULT_DISTANCE = "0.0"
    }
}