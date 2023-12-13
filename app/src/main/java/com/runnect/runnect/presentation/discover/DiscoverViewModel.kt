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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
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

    private val _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    private val _multiViewItems: ArrayList<List<DiscoverMultiViewItem>> = arrayListOf()
    val multiViewItems: List<List<DiscoverMultiViewItem>> get() = _multiViewItems

    private var _currentPageNumber = 1
    val currentPageNumber get() = _currentPageNumber

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
        getRecommendCourse(pageNo = 1, ordering = "date")
    }

    private fun getDiscoverBanners() {
        viewModelScope.launch {
            _bannerGetState.value = UiStateV2.Loading

            bannerRepository.getDiscoverBanners()
                .catch { exception ->
                    _bannerGetState.value = UiStateV2.Failure(exception.message.toString())
                }
                .collect { banners ->
                    _bannerGetState.value = UiStateV2.Success(banners)
                }
        }
    }

    private fun getMarathonCourse() {
        viewModelScope.launch {
            _marathonCourseState.value = UiStateV2.Loading

            courseRepository.getMarathonCourse()
                .onSuccess { courses ->
                    if (courses == null) {
                        _marathonCourseState.value =
                            UiStateV2.Failure("MARATHON COURSE DATA IS NULL")
                        Timber.e("MARATHON COURSE DATA IS NULL")
                        return@launch
                    }

                    _multiViewItems.add(courses)
                    _marathonCourseState.value = UiStateV2.Success(courses)
                    Timber.e("MARATHON COURSE GET SUCCESS")
                }
                .onFailure { exception ->
                    _marathonCourseState.value = UiStateV2.Failure(exception.message.toString())
                    Timber.e("MARATHON COURSE GET FAIL")
                }
        }
    }

    fun getRecommendCourse(pageNo: Int, ordering: String) {
        viewModelScope.launch {
            _recommendCourseState.value = UiStateV2.Loading

            courseRepository.getRecommendCourse(pageNo = pageNo.toString(), ordering = ordering)
                .onSuccess { courses ->
                    if (courses == null) {
                        _recommendCourseState.value =
                            UiStateV2.Failure("RECOMMEND COURSE DATA IS NULL")
                        Timber.e("RECOMMEND COURSE DATA IS NULL")
                        return@launch
                    }

                    _multiViewItems.add(courses)
                    _recommendCourseState.value = UiStateV2.Success(courses)
                    Timber.e("RECOMMEND COURSE GET SUCCESS")
                    Timber.e("ITEM SIZE: ${multiViewItems.size}")
                }.onFailure { exception ->
                    _recommendCourseState.value = UiStateV2.Failure(exception.message.toString())
                    Timber.e("RECOMMEND COURSE GET FAIL")
                }
        }
    }

    fun checkCourseLoadState(): Boolean {
        return marathonCourseState.value is UiStateV2.Success &&
                recommendCourseState.value is UiStateV2.Success &&
                multiViewItems.size >= MULTI_VIEW_TYPE_SIZE
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