package com.runnect.runnect.presentation.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponsePostScrap
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

    private val _marathonCourseState = MutableLiveData<UiStateV2<List<MarathonCourse>>>()
    val marathonCourseState: LiveData<UiStateV2<List<MarathonCourse>>>
        get() = _marathonCourseState

    private val _recommendCourseState = MutableLiveData<UiStateV2<List<RecommendCourse>>>()
    val recommendCourseState: LiveData<UiStateV2<List<RecommendCourse>>>
        get() = _recommendCourseState

    private val _nextPageState = MutableLiveData<UiStateV2<List<RecommendCourse>>>()
    val nextPageState: LiveData<UiStateV2<List<RecommendCourse>>>
        get() = _nextPageState

    private val _courseScrapState = MutableLiveData<UiStateV2<ResponsePostScrap?>>()
    val courseScrapState: LiveData<UiStateV2<ResponsePostScrap?>>
        get() = _courseScrapState

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    private var isRecommendCoursePageEnd = false
    private var currentPageNo = 1

    init {
        getDiscoverBanners()
        getMarathonCourse()
        getRecommendCourse(pageNo = 1, ordering = "date")
    }

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }

    fun refreshCurrentCourses() {
        currentPageNo = 1
        getMarathonCourse()
        getRecommendCourse(pageNo = currentPageNo, ordering = "date")
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
                        _marathonCourseState.value = UiStateV2.Failure("MARATHON COURSE DATA IS NULL")
                        return@launch
                    }

                    _marathonCourseState.value = UiStateV2.Success(courses)
                    Timber.d("MARATHON COURSE GET SUCCESS")
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
                .onSuccess { pagingData ->
                    if (pagingData == null) {
                        _recommendCourseState.value =
                            UiStateV2.Failure("RECOMMEND COURSE DATA IS NULL")
                        return@onSuccess
                    }

                    isRecommendCoursePageEnd = pagingData.isEnd
                    _recommendCourseState.value = UiStateV2.Success(pagingData.recommendCourses)
                    Timber.d("RECOMMEND COURSE GET SUCCESS")
                }.onFailure { exception ->
                    _recommendCourseState.value = UiStateV2.Failure(exception.message.toString())
                    Timber.e("RECOMMEND COURSE GET FAIL")
                }
        }
    }

    fun isNextPageLoading() = nextPageState.value is UiStateV2.Loading

    fun getRecommendCourseNextPage() {
        viewModelScope.launch {
            if (isRecommendCoursePageEnd) return@launch

            Timber.d("다음 페이지를 요청했어요!")
            _nextPageState.value = UiStateV2.Loading
            currentPageNo++

            courseRepository.getRecommendCourse(
                pageNo = currentPageNo.toString(),
                ordering = "date"
            )
                .onSuccess { pagingData ->
                    if (pagingData == null) {
                        _nextPageState.value =
                            UiStateV2.Failure("RECOMMEND COURSE NEXT PAGE DATA IS NULL")
                        return@onSuccess
                    }

                    isRecommendCoursePageEnd = pagingData.isEnd
                    _nextPageState.value = UiStateV2.Success(pagingData.recommendCourses)
                    Timber.d("RECOMMEND COURSE NEXT PAGE GET SUCCESS")
                }
                .onFailure { exception ->
                    _nextPageState.value = UiStateV2.Failure(exception.message.toString())
                    Timber.e("RECOMMEND COURSE NEXT PAGE GET FAIL")
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