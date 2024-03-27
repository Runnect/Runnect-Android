package com.runnect.runnect.presentation.discover.pick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.entity.DiscoverUploadCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverPickViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {
    private val _courseGetState = MutableLiveData<UiStateV2<List<DiscoverUploadCourse>?>>()
    val courseGetState: LiveData<UiStateV2<List<DiscoverUploadCourse>?>>
        get() = _courseGetState

    private val _courseSelectState = MutableLiveData(false)
    val courseSelectState: LiveData<Boolean>
        get() = _courseSelectState

    private var _selectedCourse = DiscoverUploadCourse(-1, "", "", "")
    val selectedCourse get() = _selectedCourse

    init {
        getMyCourseLoad()
    }

    private fun getMyCourseLoad() {
        launchWithHandler {
            _courseGetState.value = UiStateV2.Loading

            courseRepository.getMyCourseLoad()
                .onStart {
                    _courseGetState.value = UiStateV2.Loading
                }.collect { result ->
                    result.onSuccess { response ->
                        Timber.d("DISCOVER UPLOAD COURSE GET SUCCESS")
                        _courseGetState.value = if (response.isEmpty()) UiStateV2.Empty else UiStateV2.Success(response)
                    }.onFailure { t ->
                        Timber.e("DISCOVER UPLOAD COURSE GET FAIL")
                        Timber.e("${t.message}")
                        _courseGetState.value = UiStateV2.Failure(t.toLog())
                    }
                }
        }
    }

    fun updateCourseSelectState(isSelected: Boolean) {
        _courseSelectState.value = isSelected
    }

    fun saveSelectedCourse(course: DiscoverUploadCourse) {
        _selectedCourse = course
    }
}