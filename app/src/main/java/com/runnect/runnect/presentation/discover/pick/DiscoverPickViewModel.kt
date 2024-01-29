package com.runnect.runnect.presentation.discover.pick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverPickViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private var _uploadCourseGetState = MutableLiveData<UiStateV2<List<DiscoverUploadCourse>?>>()
    val uploadCourseGetState: LiveData<UiStateV2<List<DiscoverUploadCourse>?>>
        get() = _uploadCourseGetState

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

    init {
        getMyCourseLoad()
    }

    private fun getMyCourseLoad() {
        viewModelScope.launch {
            _uploadCourseGetState.value = UiStateV2.Loading

            courseRepository.getMyCourseLoad()
                .onSuccess { response ->
                    if (response == null) {
                        _uploadCourseGetState.value =
                            UiStateV2.Failure("DISCOVER UPLOAD COURSE DATA IS NULL")
                        return@launch
                    }

                    if (response.isEmpty()) UiStateV2.Empty
                    else _uploadCourseGetState.value = UiStateV2.Success(response)
                    Timber.d("DISCOVER UPLOAD COURSE GET SUCCESS")
                }.onFailure { t ->
                    _uploadCourseGetState.value = UiStateV2.Failure(t.message.toString())
                    Timber.e("DISCOVER UPLOAD COURSE GET FAIL")
                    Timber.e("${t.message}")
                }
        }
    }

    fun checkSelectEnable(id: Int, img: String, departure: String, distance: String) {
        Timber.d("3. 선택된 아이템의 아이디값을 Adapter로부터 받아와서 라이브 데이터 변경")
        _idSelectedItem.value = id
        _imgSelectedItem.value = img
        _departureSelectedItem.value = departure
        _distanceSelectedItem.value = distance
    }
}