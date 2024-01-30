package com.runnect.runnect.presentation.discover.pick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverPickViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val _courseGetState = MutableLiveData<UiStateV2<List<DiscoverUploadCourse>?>>()
    val courseGetState: LiveData<UiStateV2<List<DiscoverUploadCourse>?>>
        get() = _courseGetState

    private val _courseSelectState = MutableLiveData<Boolean>()
    val courseSelectState: LiveData<Boolean>
        get() = _courseSelectState

    private var _currentSelectedCourse = DiscoverUploadCourse()
    val currentSelectedCourse get() = _currentSelectedCourse

    init {
        getMyCourseLoad()
    }

    private fun getMyCourseLoad() {
        viewModelScope.launch {
            _courseGetState.value = UiStateV2.Loading

            courseRepository.getMyCourseLoad()
                .onSuccess { response ->
                    if (response == null) {
                        _courseGetState.value =
                            UiStateV2.Failure("DISCOVER UPLOAD COURSE DATA IS NULL")
                        return@launch
                    }

                    Timber.d("DISCOVER UPLOAD COURSE GET SUCCESS")
                    if (response.isEmpty()) UiStateV2.Empty
                    else _courseGetState.value = UiStateV2.Success(response)

                }.onFailure { t ->
                    Timber.e("DISCOVER UPLOAD COURSE GET FAIL")
                    Timber.e("${t.message}")
                    _courseGetState.value = UiStateV2.Failure(t.message.toString())
                }
        }
    }

    fun updateCourseSelectState(isSelected: Boolean) {
        _courseSelectState.value = isSelected
    }

    fun saveCurrentSelectedCourse(course: DiscoverUploadCourse) {
        _currentSelectedCourse = course
    }
}