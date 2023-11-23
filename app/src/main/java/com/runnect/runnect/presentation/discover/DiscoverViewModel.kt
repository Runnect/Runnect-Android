package com.runnect.runnect.presentation.discover

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.DiscoverPromotionItem
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

    var bannerData = mutableListOf<DiscoverPromotionItem>()

    private var _bannerCount = 0
    val bannerCount: Int get() = _bannerCount

    init {
        getBannerData()
        getRecommendCourse(pageNo = "1")
    }

    private fun getBannerData() {
        viewModelScope.launch {
            runCatching {
                _bannerGetState.value = UiState.Loading

                bannerRepository.getBannerData().collect { bannerList ->
                    bannerData = bannerList
                    _bannerCount = bannerData.size
                    _bannerGetState.value = UiState.Success
                }
            }.onFailure {
                _bannerGetState.value = UiState.Failure
            }
        }
    }

    fun getRecommendCourse(pageNo: String?) {
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