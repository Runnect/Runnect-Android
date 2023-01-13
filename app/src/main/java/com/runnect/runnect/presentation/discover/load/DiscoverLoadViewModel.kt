package com.runnect.runnect.presentation.discover.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseLoadInfoDTO
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverLoadViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private var _courseLoadState = MutableLiveData<UiState>()
    val courseLoadState: LiveData<UiState>
        get() = _courseLoadState

    var courseLoadList = mutableListOf<CourseLoadInfoDTO>()

    private var _idSelectedItem: MutableLiveData<Int> = MutableLiveData(0)
    val idSelectedItem: LiveData<Int>
        get() = _idSelectedItem

    private var _imgSelectedItem: MutableLiveData<String> = MutableLiveData()
    val imgSelectedItem: LiveData<String>
        get() = _imgSelectedItem

    private var _departureSelectedItem: MutableLiveData<String> = MutableLiveData()
    val departureSelectedItem: LiveData<String>
        get() = _departureSelectedItem

    private var _distanceSelectedItem: MutableLiveData<String> = MutableLiveData()
    val distanceSelectedItem: LiveData<String>
        get() = _distanceSelectedItem

    fun checkSelectEnable(id: Int, img: String, departure: String, distance:String) {
        Timber.d("3. 선택된 아이템의 아이디값을 Adapter로부터 받아와서 라이브 데이터 변경")
        _idSelectedItem.value = id
        _imgSelectedItem.value = img
        _departureSelectedItem.value = departure
        _distanceSelectedItem.value = distance
    }

    fun getMyCourseLoad() {
        viewModelScope.launch {
            _courseLoadState.value = UiState.Loading
            runCatching {
                courseRepository.getMyCourseLoad()
            }.onSuccess {
                courseLoadList = it
                _courseLoadState.value = UiState.Success
            }.onFailure {
                _courseLoadState.value = UiState.Failure
            }
        }
    }

}