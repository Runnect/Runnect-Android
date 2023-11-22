package com.runnect.runnect.presentation.discover

import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.DiscoverPromotionItem
import com.runnect.runnect.data.dto.RecommendCourseDTO
import com.runnect.runnect.data.dto.request.RequestCourseScrap
import com.runnect.runnect.domain.BannerRepository
import com.runnect.runnect.domain.CourseRepository
import com.runnect.runnect.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val bannerRepository: BannerRepository
) :
    ViewModel() {
    private var _courseInfoState = MutableLiveData<UiState>(UiState.Empty)
    val courseInfoState: LiveData<UiState>
        get() = _courseInfoState

    private var _recommendCourseList = mutableListOf<RecommendCourseDTO>()
    val recommendCourseList: MutableList<RecommendCourseDTO>
        get() = _recommendCourseList

    val currentPageNo = MutableLiveData<Int>()

    val errorMessage = MutableLiveData<String>()

    var bannerData = mutableListOf<DiscoverPromotionItem>()

    private var _bannerState = MutableLiveData<UiState>(UiState.Empty)
    val bannerState: LiveData<UiState>
        get() = _bannerState

    private var _bannerCount = 0
    val bannerCount: Int
        get() = _bannerCount

    init {
        getBannerData()
        getRecommendCourse(pageNo = "1")
    }

   private fun getBannerData() {
        viewModelScope.launch {
            runCatching {
                _bannerState.value = UiState.Loading
                bannerRepository.getBannerData().collect { bannerList ->
                    bannerData = bannerList
                    _bannerCount = bannerData.size
                    _bannerState.value = UiState.Success
                }
            }.onFailure {
                _bannerState.value = UiState.Failure
            }
        }
    }

    fun getRecommendCourse(pageNo: String?) {
        viewModelScope.launch {
            runCatching {
                _courseInfoState.value = UiState.Loading
                courseRepository.getRecommendCourse(pageNo = pageNo)
            }.onSuccess {
                _recommendCourseList.addAll(it)
                currentPageNo.value = it[0].pageNo
                _courseInfoState.value = UiState.Success
                Timber.tag(ContentValues.TAG).d("데이터 수신 완료")
            }.onFailure {
                Timber.tag(ContentValues.TAG).d("데이터 수신 실패")
                errorMessage.value = it.message
                _courseInfoState.value = UiState.Failure
            }
        }
    }

    fun postCourseScrap(id: Int, scrapTF: Boolean) {
        viewModelScope.launch {
            runCatching {
                courseRepository.postCourseScrap(
                    RequestCourseScrap(
                        publicCourseId = id, scrapTF = scrapTF.toString()
                    )
                )
            }.onSuccess {
                Timber.d("스크랩 성공")
            }.onFailure {
                Timber.d("스크랩 실패")
            }
        }
    }
}