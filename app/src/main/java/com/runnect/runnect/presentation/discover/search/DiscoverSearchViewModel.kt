package com.runnect.runnect.presentation.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.entity.DiscoverCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverSearchViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private var _courseSearchState = MutableLiveData<UiStateV2<List<DiscoverCourse>?>>()
    val courseSearchState: LiveData<UiStateV2<List<DiscoverCourse>?>>
        get() = _courseSearchState

    fun getCourseSearch(keyword: String) {
        viewModelScope.launch {
            _courseSearchState.value = UiStateV2.Loading

            courseRepository.getCourseSearch(
                keyword = keyword
            ).onSuccess { response ->
                if (response == null) return@launch

                if (response.isEmpty()) {
                    _courseSearchState.value = UiStateV2.Empty
                    return@launch
                }

                _courseSearchState.value = UiStateV2.Success(response)
            }.onFailure { exception ->
                _courseSearchState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            courseRepository.postCourseScrap(
                RequestPostCourseScrap(
                    publicCourseId = id, scrapTF = scrapTF.toString()
                )
            )
        }
    }
}