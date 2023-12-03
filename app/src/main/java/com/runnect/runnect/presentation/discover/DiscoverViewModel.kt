package com.runnect.runnect.presentation.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.entity.DiscoverMultiItem.MarathonCourse
import com.runnect.runnect.domain.entity.DiscoverMultiItem.RecommendCourse
import com.runnect.runnect.domain.entity.PromotionBanner
import com.runnect.runnect.domain.repository.BannerRepository
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {
    private val _bannerGetState = MutableLiveData<UiState>()
    val bannerGetState: LiveData<UiState>
        get() = _bannerGetState

    private val _marathonCourseGetState = MutableLiveData<UiStateV2<List<MarathonCourse>?>>()
    val marathonCourseGetState: LiveData<UiStateV2<List<MarathonCourse>?>>
        get() = _marathonCourseGetState

    private val _recommendCourseGetState = MutableLiveData<UiStateV2<List<RecommendCourse>?>>()
    val recommendCourseGetState: LiveData<UiStateV2<List<RecommendCourse>?>>
        get() = _recommendCourseGetState

    private val _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    var bannerData = mutableListOf<PromotionBanner>()

    private var _bannerCount = 0
    val bannerCount: Int get() = _bannerCount

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    init {
        getPromotionBanner()
        getMarathonCourse()
        getRecommendCourse(pageNo = 1, "date")
    }

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
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
            _marathonCourseGetState.value = UiStateV2.Loading

            courseRepository.getMarathonCourse()
                .onSuccess { courses ->
                    if (courses == null) return@launch
                    _marathonCourseGetState.value = UiStateV2.Success(courses)
                }
                .onFailure { exception ->
                    _marathonCourseGetState.value = UiStateV2.Failure(exception.message.toString())
                }
        }
    }

    fun getRecommendCourse(pageNo: Int, ordering: String) {
        viewModelScope.launch {
            _recommendCourseGetState.value = UiStateV2.Loading

            courseRepository.getRecommendCourse(pageNo = pageNo.toString(), ordering = ordering)
                .onSuccess { courses ->
                    if (courses == null) return@launch
                    _recommendCourseGetState.value = UiStateV2.Success(courses)
                }.onFailure { exception ->
                    _recommendCourseGetState.value = UiStateV2.Failure(exception.message.toString())
                }
        }
    }

    fun initRecommendGetState() {
        _recommendCourseGetState.value = UiStateV2.Empty
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
}