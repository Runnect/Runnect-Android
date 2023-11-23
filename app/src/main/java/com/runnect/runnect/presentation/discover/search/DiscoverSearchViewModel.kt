package com.runnect.runnect.presentation.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.CourseSearchDTO
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
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

    val errorMessage = MutableLiveData<String>()

    fun getCourseSearch(keyword: String) {
        viewModelScope.launch {
            _courseSearchState.value = UiState.Loading
            runCatching {
                courseRepository.getCourseSearch(
                    keyword = keyword
                )
            }.onSuccess {
                courseSearchList = it
                if (courseSearchList.isEmpty()) {
                    _courseSearchState.value = UiState.Empty
                } else {
                    _courseSearchState.value = UiState.Success
                }
            }.onFailure {
                Timber.d("검색 실패 ${it.message} ${it.cause}")
                errorMessage.value = it.message
                _courseSearchState.value = UiState.Failure
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