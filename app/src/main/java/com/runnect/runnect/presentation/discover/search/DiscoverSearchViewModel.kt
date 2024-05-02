package com.runnect.runnect.presentation.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.domain.entity.DiscoverSearchCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverSearchViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private var _courseSearchState = MutableLiveData<UiStateV2<List<DiscoverSearchCourse>?>>()
    val courseSearchState: LiveData<UiStateV2<List<DiscoverSearchCourse>?>>
        get() = _courseSearchState

    private val _courseScrapState = MutableLiveData<UiStateV2<ResponsePostScrap?>>()
    val courseScrapState: LiveData<UiStateV2<ResponsePostScrap?>>
        get() = _courseScrapState

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }

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
            _courseScrapState.value = UiStateV2.Loading

            courseRepository.postCourseScrap(
                RequestPostCourseScrap(
                    publicCourseId = id, scrapTF = scrapTF.toString()
                )
            ).onSuccess { response ->
                _courseScrapState.value = UiStateV2.Success(response)
            }
            .onFailure { exception ->
                _courseScrapState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }
}