package com.runnect.runnect.presentation.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.entity.DiscoverCourse
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
    // todo: UiStateV2 코드로 리팩토링 하자!
    private val _courseGetState = MutableLiveData<UiStateV2<List<DiscoverCourse>?>>()
    val courseGetState: LiveData<UiStateV2<List<DiscoverCourse>?>>
        get() = _courseGetState

    private val _bannerGetState = MutableLiveData<UiState>(UiState.Empty)
    val bannerGetState: LiveData<UiState>
        get() = _bannerGetState

    private val _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    val currentPageNo = MutableLiveData<Int>()

    val errorMessage = MutableLiveData<String>()

    var bannerData = mutableListOf<PromotionBanner>()

    private var _bannerCount = 0
    val bannerCount: Int get() = _bannerCount

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    private var currentPageNumber = 1

    init {
        getPromotionBanner()
        getRecommendCourse(pageNo = 1, "date")
    }

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }

    fun loadNextPage() {
        getRecommendCourse(currentPageNumber++, "date")
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
                .onSuccess {

                }
                .onFailure {

                }
        }
    }

    fun getRecommendCourse(pageNo: Int, ordering: String) {
        viewModelScope.launch {
            _courseGetState.value = UiStateV2.Loading

            courseRepository.getRecommendCourse(pageNo = pageNo.toString(), ordering = ordering)
                .onSuccess { response ->
                    _courseGetState.value = UiStateV2.Success(response)
//                    currentPageNo.value = it[0].pageNo
                }.onFailure { exception ->
                    _courseGetState.value = UiStateV2.Failure(exception.message.toString())
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
}