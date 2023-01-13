package com.runnect.runnect.presentation.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.data.dto.response.ResponseCourseSearch
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverSearchViewModel @Inject constructor(private val courseRepository: CourseRepository) :
    ViewModel() {
    private var _courseSearchState = MutableLiveData<UiState>()
    val courseSearchState: LiveData<UiState>
        get() = _courseSearchState

    var courseSearchList = mutableListOf<CourseSearchDTO>()

    fun getCourseSearch(keyword: String) {
        viewModelScope.launch {
            _courseSearchState.value = UiState.Loading
            runCatching {
                courseRepository.getCourseSearch(keyword)
            }.onSuccess {
                Timber.d("검색 성공")
                courseSearchList = it
                _courseSearchState.value = UiState.Success
            }.onFailure {
                Timber.d("검색 실패 ${it.message} ${it.cause}")
                _courseSearchState.value = UiState.Failure
            }
        }
    }
    fun postCourseScrap(id:Int,scrapTF:Boolean) {
        viewModelScope.launch {
            runCatching {
                courseRepository.postCourseScrap(RequestCourseScrap(id,scrapTF.toString()))
            }.onSuccess {
            }.onFailure {
            }
        }
    }
}