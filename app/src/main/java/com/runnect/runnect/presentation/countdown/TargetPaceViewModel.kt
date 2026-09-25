package com.runnect.runnect.presentation.countdown

import androidx.lifecycle.SavedStateHandle
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.presentation.base.BaseViewModel
import com.runnect.runnect.presentation.run.PaceFormat
import com.runnect.runnect.util.extension.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class TargetPaceOption { NONE, TOP_RANK, MY_BEST, CUSTOM }

data class TargetPaceUiState(
    val topRankPaceSecPerKm: Double? = null,
    val myBestPaceSecPerKm: Double? = null,
    val selection: TargetPaceOption = TargetPaceOption.NONE,
    val customPaceSecPerKm: Double = TargetPaceViewModel.DEFAULT_CUSTOM_PACE_SEC,
) {
    /** 현재 선택된 목표 페이스(초/km). 설정 안 함이거나 선택한 추천값이 없으면 null. */
    val selectedPaceSecPerKm: Double?
        get() = when (selection) {
            TargetPaceOption.NONE -> null
            TargetPaceOption.TOP_RANK -> topRankPaceSecPerKm
            TargetPaceOption.MY_BEST -> myBestPaceSecPerKm
            TargetPaceOption.CUSTOM -> customPaceSecPerKm
        }
}

@HiltViewModel
class TargetPaceViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    // 사용자가 고른 값(선택지, 직접 입력 페이스)은 Activity가 시스템에 의해 재생성돼도 유지한다. 추천값은 다시 불러온다.
    private val _uiState = MutableStateFlow(
        TargetPaceUiState(
            selection = savedStateHandle.get<String>(KEY_SELECTION)
                ?.let { runCatching { TargetPaceOption.valueOf(it) }.getOrNull() }
                ?: TargetPaceOption.NONE,
            customPaceSecPerKm = savedStateHandle[KEY_CUSTOM_PACE] ?: DEFAULT_CUSTOM_PACE_SEC,
        )
    )
    val uiState: StateFlow<TargetPaceUiState> = _uiState.asStateFlow()

    private var loadedCourseId: Int? = null

    /**
     * 공개 코스면 랭킹 1위 / 내 최고기록을 추천값으로 불러온다. 내가 그린 코스(publicCourseId 없음)는 랭킹이 없어 추천값 없이 진행.
     * 서버 pace 문자열은 신뢰할 수 없어서(PaceFormat.parseDurationSec 참고) 기록 시간과 코스 거리로 직접 계산한다.
     */
    fun loadRecommendations(publicCourseId: Int?, distanceKm: Double?) {
        if (publicCourseId == null || loadedCourseId == publicCourseId) return
        loadedCourseId = publicCourseId

        launchWithHandler {
            courseRepository.getCourseRanking(courseId = publicCourseId, limit = 1).collectResult(
                onSuccess = { ranking ->
                    val pace = PaceFormat.paceFromDuration(ranking.entries.firstOrNull()?.time, distanceKm)
                    _uiState.update { it.copy(topRankPaceSecPerKm = pace) }
                }
            )
        }
        launchWithHandler {
            courseRepository.getMyCourseRanking(courseId = publicCourseId).collectResult(
                onSuccess = { myRanking ->
                    val pace = if (myRanking.hasRecord) PaceFormat.paceFromDuration(myRanking.time, distanceKm) else null
                    _uiState.update { it.copy(myBestPaceSecPerKm = pace) }
                }
            )
        }
    }

    fun select(option: TargetPaceOption) {
        _uiState.update { it.copy(selection = option) }
        savedStateHandle[KEY_SELECTION] = option.name
    }

    /** 직접 입력 페이스를 5초 단위로 조정한다. 너무 빠르거나 느린 값은 범위 안으로 제한한다. */
    fun adjustCustomPace(deltaSec: Int) {
        _uiState.update {
            it.copy(
                customPaceSecPerKm = (it.customPaceSecPerKm + deltaSec)
                    .coerceIn(MIN_CUSTOM_PACE_SEC, MAX_CUSTOM_PACE_SEC)
            )
        }
        savedStateHandle[KEY_CUSTOM_PACE] = _uiState.value.customPaceSecPerKm
    }

    companion object {
        private const val KEY_SELECTION = "targetPaceSelection"
        private const val KEY_CUSTOM_PACE = "targetPaceCustom"
        const val DEFAULT_CUSTOM_PACE_SEC = 360.0 // 6'00"
        const val MIN_CUSTOM_PACE_SEC = 180.0 // 3'00"
        const val MAX_CUSTOM_PACE_SEC = 900.0 // 15'00"
        const val CUSTOM_PACE_STEP_SEC = 5
    }
}
