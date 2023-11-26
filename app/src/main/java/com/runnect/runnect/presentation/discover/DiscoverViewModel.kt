package com.runnect.runnect.presentation.discover

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.domain.entity.DiscoverPromotionBanner
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.domain.BannerRepository
import com.runnect.runnect.domain.CourseRepository
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
    // todo: UiStateV2 코드로 리팩토링 하자!
    private val _courseGetState = MutableLiveData<UiState>(UiState.Empty)
    val courseGetState: LiveData<UiState>
        get() = _courseGetState

    private val _bannerGetState = MutableLiveData<UiState>(UiState.Empty)
    val bannerGetState: LiveData<UiState>
        get() = _bannerGetState

    private val _courseScrapState = MutableLiveData<UiStateV2<Unit?>>()
    val courseScrapState: LiveData<UiStateV2<Unit?>>
        get() = _courseScrapState

    private var _recommendCourseList = mutableListOf<RecommendCourseDTO>()
    val recommendCourseList: MutableList<RecommendCourseDTO>
        get() = _recommendCourseList

    val currentPageNo = MutableLiveData<Int>()

    val errorMessage = MutableLiveData<String>()

    var bannerData = mutableListOf<DiscoverPromotionBanner>()

    private var _bannerCount = 0
    val bannerCount: Int get() = _bannerCount

    init {
        getPromotionBanners()
        getRecommendCourses(pageNo = "1")
    }

    private fun getPromotionBanners() {
        viewModelScope.launch {
            runCatching {
                _bannerGetState.value = UiState.Loading

                // todo: Success 상태에서 데이터를 프래그먼트 쪽으로 넘겨주자!
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

    fun getRecommendCourses(pageNo: String?) {
        viewModelScope.launch {
            runCatching {
                _courseGetState.value = UiState.Loading

                courseRepository.getRecommendCourse(pageNo = pageNo)
            }.onSuccess {
                _recommendCourseList.addAll(it)
                currentPageNo.value = it[0].pageNo

                _courseGetState.value = UiState.Success
                Timber.tag(ContentValues.TAG).d("데이터 수신 완료")
            }.onFailure {
                Timber.tag(ContentValues.TAG).d("데이터 수신 실패")
                errorMessage.value = it.message

                _courseGetState.value = UiState.Failure
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