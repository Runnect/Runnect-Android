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

    var editMode = MutableLiveData(READ_MODE)

    var mapImg: MutableLiveData<String> = MutableLiveData<String>(DEFAULT_IMAGE)
    val title: MutableLiveData<String> = MutableLiveData()
    var titleForInterruption = ""
    var date = DEFAULT_DATE
    var departure = DEFAULT_DEPARTURE
    var distance = DEFAULT_DISTANCE
    var time = DEFAULT_TIME
    var pace = DEFAULT_PACE

    fun setTitle(titleParam: String) {
        title.value = titleParam
    }

    companion object {
        const val DEFAULT_IMAGE = "https://insopt-bucket-rin.s3.ap-northeast-2.amazonaws.com/1683259309418_temprentpk5892730152618614049.png"
        const val DEFAULT_DATE = "2023.00.00"
        const val DEFAULT_DEPARTURE = "서울시"
        const val DEFAULT_PACE = "0’00"
        const val DEFAULT_TIME = "00:00:00"
        const val DEFAULT_DISTANCE = "0.0"
        const val EDIT_MODE = "editMode"
        const val READ_MODE = "readMode"
    }
}