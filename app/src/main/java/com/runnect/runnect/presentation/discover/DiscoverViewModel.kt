package com.runnect.runnect.presentation.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.RecommendCourse
import com.runnect.runnect.domain.entity.PromotionBanner
import com.runnect.runnect.domain.repository.BannerRepository
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {
    private val _bannerGetState = MutableLiveData<UiState>()
    val bannerGetState: LiveData<UiState>
        get() = _bannerGetState

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

    var bannerData = mutableListOf<PromotionBanner>()

    private var _bannerCount = 0
    val bannerCount: Int get() = _bannerCount

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    init {
        getPromotionBanner()
        getMarathonCourse()
        getRecommendCourse(pageNo = currentPageNumber, "date")
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

    private fun getPromotionBanner() {
        viewModelScope.launch {
            runCatching {
                _bannerGetState.value = UiState.Loading

                bannerRepository.getPromotionBanners().collect { bannerList ->
                    bannerData = bannerList
                    _bannerCount = bannerData.size
                    _bannerGetState.value = UiState.Success
                }
            }.onFailure {
                _bannerGetState.value = UiState.Failure
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

                    _multiViewItems.add(courses)
                    if (multiViewItems.size >= MULTI_VIEW_TYPE_SIZE) {
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