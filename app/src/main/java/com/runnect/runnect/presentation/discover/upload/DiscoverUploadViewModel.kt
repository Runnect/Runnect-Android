package com.runnect.runnect.presentation.discover.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.runnect.runnect.data.dto.request.RequestPostPublicCourse
import com.runnect.runnect.domain.common.toLog
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.addSourceList
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class DiscoverUploadViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {

    var id = 0
    val title = MutableLiveData<String>()
    val desc = MutableLiveData<String>()
    val isUploadEnable = MediatorLiveData<Boolean>()

    private var _courseUpLoadState = MutableLiveData<UiState>()
    val courseUpLoadState: LiveData<UiState>
        get() = _courseUpLoadState

    val errorMessage = MutableLiveData<String>()

    init {
        isUploadEnable.value = false
        isUploadEnable.apply {
            addSourceList(title, desc) { checkIsUploadEnable() }
        }
    }

    private fun checkIsUploadEnable(): Boolean {
        return !(title.value.isNullOrEmpty() or desc.value.isNullOrEmpty())
    }

    fun postUploadMyCourse() = launchWithHandler {
        val requestPostPublicCourse = RequestPostPublicCourse(
            courseId = id,
            description = desc.value.toString(),
            title = title.value.toString()
        )

        courseRepository.postUploadMyCourse(requestPostPublicCourse)
            .onStart {
                _courseUpLoadState.value = UiState.Loading
            }.collectResult(
                onSuccess = {
                    _courseUpLoadState.value = UiState.Success
                },
                onFailure = {
                    errorMessage.value = it.toLog()
                    _courseUpLoadState.value = UiState.Failure
                }
            )
    }
}
