package com.runnect.runnect.presentation.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.domain.entity.DiscoverSearchCourse
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverSearchViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : BaseViewModel() {
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
        launchWithHandler {
            courseRepository.postCourseScrap(
                RequestPostCourseScrap(
                    publicCourseId = id, scrapTF = scrapTF.toString()
                )
            ).onStart {
                _courseScrapState.value = UiStateV2.Loading
            }.collectResult(
                onSuccess = {
                    _courseScrapState.value = UiStateV2.Success(it)
                },
                onFailure = {
                    _courseScrapState.value = UiStateV2.Failure(it.message.toString())
                }
            )
        }
    }
}