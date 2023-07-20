package com.runnect.runnect.presentation.discover.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestUploadMyCourse
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.util.extension.addSourceList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverUploadViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {
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

    fun postUploadMyCourse() {
        viewModelScope.launch {
            Timber.d("업로드 호출")
            runCatching {
                _courseUpLoadState.value = UiState.Loading
                courseRepository.postUploadMyCourse(
                    RequestUploadMyCourse(
                        courseId = id,
                        description = desc.value.toString(),
                        title = title.value.toString()
                    )
                )
            }.onSuccess {
                Timber.d("업로드 성공")
                _courseUpLoadState.value = UiState.Success
            }.onFailure {
                Timber.d("업로드 실패")
                errorMessage.value = it.message
                _courseUpLoadState.value = UiState.Failure
            }
        }
    }
}
