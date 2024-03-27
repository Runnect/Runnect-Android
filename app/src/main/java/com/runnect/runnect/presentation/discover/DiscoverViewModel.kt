package com.runnect.runnect.presentation.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.response.ResponsePostScrap
import com.runnect.runnect.domain.common.onFailure
import com.runnect.runnect.domain.common.onSuccess
import com.runnect.runnect.domain.entity.DiscoverMultiViewItem.*
import com.runnect.runnect.domain.entity.DiscoverBanner
import com.runnect.runnect.domain.repository.BannerRepository
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.state.UiStateV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val bannerRepository: BannerRepository
) : BaseViewModel() {
    private val _bannerGetState = MutableLiveData<UiStateV2<List<DiscoverBanner>>>()
    val bannerGetState: LiveData<UiStateV2<List<DiscoverBanner>>>
        get() = _bannerGetState

    private val _marathonCourseGetState = MutableLiveData<UiStateV2<List<MarathonCourse>>>()
    val marathonCourseGetState: LiveData<UiStateV2<List<MarathonCourse>>>
        get() = _marathonCourseGetState

    private val _recommendCourseGetState = MutableLiveData<UiStateV2<List<RecommendCourse>>>()
    val recommendCourseGetState: LiveData<UiStateV2<List<RecommendCourse>>>
        get() = _recommendCourseGetState

    private val _recommendCourseNextPageState = MutableLiveData<UiStateV2<List<RecommendCourse>>>()
    val recommendCourseNextPageState: LiveData<UiStateV2<List<RecommendCourse>>>
        get() = _recommendCourseNextPageState

    private val _recommendCourseSortState = MutableLiveData<UiStateV2<List<RecommendCourse>>>()
    val recommendCourseSortState: LiveData<UiStateV2<List<RecommendCourse>>>
        get() = _recommendCourseSortState

    private val _courseScrapState = MutableLiveData<UiStateV2<ResponsePostScrap?>>()
    val courseScrapState: LiveData<UiStateV2<ResponsePostScrap?>>
        get() = _courseScrapState

    private var _clickedCourseId = -1
    val clickedCourseId get() = _clickedCourseId

    private var isRecommendCoursePageEnd = false
    private var currentPageNumber = FIRST_PAGE_NUM
    private var currentSortCriteria = DEFAULT_SORT_CRITERIA

    init {
        getDiscoverBanners()
        getMarathonCourses()
        getRecommendCourses()
    }

    fun saveClickedCourseId(id: Int) {
        _clickedCourseId = id
    }

    fun refreshDiscoverCourses() {
        getMarathonCourses()
        updateRecommendCourseSortCriteria(
            criteria = DEFAULT_SORT_CRITERIA
        )
        getRecommendCourses()
    }

    private fun updateRecommendCoursePagingData(isEnd: Boolean, pageNo: Int) {
        isRecommendCoursePageEnd = isEnd
        currentPageNumber = pageNo
        Timber.d("isEnd: ${isRecommendCoursePageEnd}, page: ${currentPageNumber}")
    }

    private fun updateRecommendCourseSortCriteria(criteria: String) {
        currentSortCriteria = criteria
        Timber.d("sort: ${currentSortCriteria}")
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

    private fun getMarathonCourses() {
        launchWithHandler {
            courseRepository.getMarathonCourse()
                .onStart {
                    _marathonCourseGetState.value = UiStateV2.Loading
                }.collect { result ->
                    result.onSuccess {
                        _marathonCourseGetState.value = UiStateV2.Success(it)
                    }.onFailure {
                        _marathonCourseGetState.value = UiStateV2.Failure(it.toLog())
                    }
                }
        }
    }

    private fun getRecommendCourses() {
        viewModelScope.launch {
            _recommendCourseGetState.value = UiStateV2.Loading

            courseRepository.getRecommendCourse(
                pageNo = FIRST_PAGE_NUM.toString(),
                sort = currentSortCriteria
            ).onSuccess { pagingData ->
                if (pagingData == null) {
                    _recommendCourseGetState.value =
                        UiStateV2.Failure("RECOMMEND COURSE DATA IS NULL")
                    return@onSuccess
                }

                updateRecommendCoursePagingData(
                    isEnd = pagingData.isEnd,
                    pageNo = FIRST_PAGE_NUM
                )

                _recommendCourseGetState.value = UiStateV2.Success(pagingData.recommendCourses)
                Timber.d("RECOMMEND COURSE GET SUCCESS")

            }.onFailure { exception ->
                _recommendCourseGetState.value = UiStateV2.Failure(exception.message.toString())
                Timber.e("RECOMMEND COURSE GET FAIL")
            }
        }
    }

    fun isNextPageLoading() = recommendCourseNextPageState.value is UiStateV2.Loading

    fun getRecommendCourseNextPage() {
        viewModelScope.launch {
            // 다음 페이지가 없으면 요청하지 않는다.
            if (isRecommendCoursePageEnd) return@launch

            _recommendCourseNextPageState.value = UiStateV2.Loading

            courseRepository.getRecommendCourse(
                pageNo = (currentPageNumber + 1).toString(),
                sort = currentSortCriteria
            )
                .onSuccess { pagingData ->
                    if (pagingData == null) {
                        _recommendCourseNextPageState.value =
                            UiStateV2.Failure("RECOMMEND COURSE NEXT PAGE DATA IS NULL")
                        return@onSuccess
                    }

                    updateRecommendCoursePagingData(
                        isEnd = pagingData.isEnd,
                        pageNo = currentPageNumber + 1
                    )

                    _recommendCourseNextPageState.value = UiStateV2.Success(pagingData.recommendCourses)
                    Timber.d("RECOMMEND COURSE NEXT PAGE GET SUCCESS")
                }
                .onFailure { exception ->
                    _recommendCourseNextPageState.value =
                        UiStateV2.Failure(exception.message.toString())
                    Timber.e("RECOMMEND COURSE NEXT PAGE GET FAIL")
                }
        }
    }

    fun sortRecommendCourses(criteria: String) {
        updateRecommendCourseSortCriteria(criteria)

        viewModelScope.launch {
            _recommendCourseSortState.value = UiStateV2.Loading

            courseRepository.getRecommendCourse(
                pageNo = FIRST_PAGE_NUM.toString(),
                sort = currentSortCriteria
            ).onSuccess { pagingData ->
                if (pagingData == null) {
                    _recommendCourseSortState.value =
                        UiStateV2.Failure("RECOMMEND COURSE DATA IS NULL")
                    return@onSuccess
                }

                updateRecommendCoursePagingData(
                    isEnd = pagingData.isEnd,
                    pageNo = FIRST_PAGE_NUM
                )

                _recommendCourseSortState.value = UiStateV2.Success(pagingData.recommendCourses)
                Timber.d("RECOMMEND COURSE SORT SUCCESS")
            }.onFailure { exception ->
                _recommendCourseSortState.value = UiStateV2.Failure(exception.message.toString())
                Timber.e("RECOMMEND COURSE SORT FAIL")
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
        private const val FIRST_PAGE_NUM = 1
        private const val DEFAULT_SORT_CRITERIA = "date"
    }
}