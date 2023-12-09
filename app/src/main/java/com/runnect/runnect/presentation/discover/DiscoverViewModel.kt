package com.runnect.runnect.presentation.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
import com.runnect.runnect.domain.entity.DiscoverBanner
import com.runnect.runnect.domain.repository.BannerRepository
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {
    private val _bannerGetState = MutableLiveData<UiStateV2<List<DiscoverBanner>>>()
    val bannerGetState: LiveData<UiStateV2<List<DiscoverBanner>>>
        get() = _bannerGetState

    private val _marathonCourseState = MutableLiveData<UiStateV2<List<MarathonCourse>?>>()
    val marathonCourseState: LiveData<UiStateV2<List<MarathonCourse>?>>
        get() = _marathonCourseState

    private val _recommendCourseState = MutableLiveData<UiStateV2<List<RecommendCourse>?>>()
    val recommendCourseState: LiveData<UiStateV2<List<RecommendCourse>?>>
        get() = _recommendCourseState

    private val _courseLoadState = MutableLiveData<UiStateV2<List<List<DiscoverMultiViewItem>>>>()
    val courseLoadState: LiveData<UiStateV2<List<List<DiscoverMultiViewItem>>>>
        get() = _courseLoadState

    private val _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    private val _multiViewItems: ArrayList<List<DiscoverMultiViewItem>> = arrayListOf()
    val multiViewItems: List<List<DiscoverMultiViewItem>> get() = _multiViewItems

    private var _currentPageNumber = 1
    val currentPageNumber get() = _currentPageNumber

    private var _bannerCount = 0
    val bannerCount: Int get() = _bannerCount

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    init {
        getDiscoverBanners()
        getMarathonCourse()
        getRecommendCourse(pageNo = currentPageNumber, ordering = "date")
    }

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }

    fun updateCurrentPageNumber(number: Int) {
        _currentPageNumber = number
    }

    fun resetMultiViewItems() {
        _multiViewItems.clear()
        _currentPageNumber = 1
    }

    fun refreshCurrentCourses() {
        getMarathonCourse()
        getRecommendCourse(pageNo = currentPageNumber, ordering = "date")
    }

    private fun getDiscoverBanners() {
        viewModelScope.launch {
            _bannerGetState.value = UiStateV2.Loading

            bannerRepository.getDiscoverBanners()
                .catch { exception ->
                    _bannerGetState.value = UiStateV2.Failure(exception.message.toString())
                }
                .collect { banners ->
                    _bannerCount = banners.size
                    _bannerGetState.value = UiStateV2.Success(banners)
                }
        }
    }

    private fun getMarathonCourse() {
        viewModelScope.launch {
            courseRepository.getMarathonCourse()
                .onSuccess { courses ->
                    if (courses == null) {
                        _courseLoadState.value = UiStateV2.Failure("marathon course data is null")
                        return@launch
                    }

                    _multiViewItems.add(courses)
                    _courseLoadState.value = UiStateV2.Loading
                }
                .onFailure { exception ->
                    _courseLoadState.value = UiStateV2.Failure(exception.message.toString())
                }
        }
    }

    fun getRecommendCourse(pageNo: Int, ordering: String) {
        viewModelScope.launch {
            courseRepository.getRecommendCourse(pageNo = pageNo.toString(), ordering = ordering)
                .onSuccess { courses ->
                    if (courses == null) {
                        _courseLoadState.value = UiStateV2.Failure("recommend course data is null")
                        return@launch
                    }

                    // todo: 무한 로딩을 막기 위한 동기 처리 로직이 필요하다.
                    withContext(Dispatchers.IO) {
                        Timber.e("withContext block")
                        _multiViewItems.add(courses)
                    }

                    if (multiViewItems.size >= MULTI_VIEW_TYPE_SIZE) {
                        Timber.e("onSuccess block")
                        _courseLoadState.value = UiStateV2.Success(multiViewItems)
                    }

                }.onFailure { exception ->
                    _courseLoadState.value = UiStateV2.Failure(exception.message.toString())
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
            }.onFailure { exception ->
                _courseScrapState.value = UiStateV2.Failure(exception.message.toString())
            }
        }
    }

    companion object {
        private const val MULTI_VIEW_TYPE_SIZE = 2
    }
}