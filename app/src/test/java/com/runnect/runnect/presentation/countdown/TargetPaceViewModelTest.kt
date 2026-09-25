package com.runnect.runnect.presentation.countdown

import androidx.lifecycle.SavedStateHandle
import com.runnect.runnect.domain.entity.CourseRanking
import com.runnect.runnect.domain.entity.CourseRankingEntry
import com.runnect.runnect.domain.entity.MyCourseRanking
import com.runnect.runnect.domain.repository.CourseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TargetPaceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var courseRepository: CourseRepository
    private lateinit var viewModel: TargetPaceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        courseRepository = mockk()
        viewModel = TargetPaceViewModel(courseRepository, SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun givenRanking(topTime: String?, myTime: String?) {
        val entries = topTime?.let { listOf(CourseRankingEntry(1, 1, "런너A", 100, it, "무시되는 값")) }.orEmpty()
        coEvery { courseRepository.getCourseRanking(courseId = 7, limit = 1) } returns
            flowOf(Result.success(CourseRanking(totalCount = entries.size, entries = entries)))
        coEvery { courseRepository.getMyCourseRanking(courseId = 7) } returns flowOf(
            Result.success(
                MyCourseRanking(hasRecord = myTime != null, rank = 3, userId = 2, nickname = "나", time = myTime, pace = null)
            )
        )
    }

    @Test
    fun `랭킹 1위와 내 기록의 소요 시간과 코스 거리로 추천 페이스를 계산한다`() = runTest(testDispatcher) {
        givenRanking(topTime = "00:10:00", myTime = "00:12:30")

        viewModel.loadRecommendations(publicCourseId = 7, distanceKm = 2.0)
        advanceUntilIdle()

        assertEquals(300.0, requireNotNull(viewModel.uiState.value.topRankPaceSecPerKm), 0.001)
        assertEquals(375.0, requireNotNull(viewModel.uiState.value.myBestPaceSecPerKm), 0.001)
    }

    @Test
    fun `내 기록이 없으면 내 최고기록 추천값은 없다`() = runTest(testDispatcher) {
        givenRanking(topTime = "00:10:00", myTime = null)

        viewModel.loadRecommendations(publicCourseId = 7, distanceKm = 2.0)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.myBestPaceSecPerKm)
    }

    @Test
    fun `랭킹 조회에 실패하면 추천값 없이 진행한다`() = runTest(testDispatcher) {
        coEvery { courseRepository.getCourseRanking(any(), any()) } returns flowOf(Result.failure(RuntimeException()))
        coEvery { courseRepository.getMyCourseRanking(any()) } returns flowOf(Result.failure(RuntimeException()))

        viewModel.loadRecommendations(publicCourseId = 7, distanceKm = 2.0)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.topRankPaceSecPerKm)
        assertNull(viewModel.uiState.value.myBestPaceSecPerKm)
    }

    @Test
    fun `공개 코스가 아니면 랭킹을 조회하지 않는다`() = runTest(testDispatcher) {
        viewModel.loadRecommendations(publicCourseId = null, distanceKm = 2.0)
        advanceUntilIdle()

        coVerify(exactly = 0) { courseRepository.getCourseRanking(any(), any()) }
    }

    @Test
    fun `기본 선택은 설정 안 함이라 목표 페이스가 없다`() {
        assertEquals(TargetPaceOption.NONE, viewModel.uiState.value.selection)
        assertNull(viewModel.uiState.value.selectedPaceSecPerKm)
    }

    @Test
    fun `추천값을 고르면 해당 페이스가 목표 페이스가 된다`() = runTest(testDispatcher) {
        givenRanking(topTime = "00:10:00", myTime = null)
        viewModel.loadRecommendations(publicCourseId = 7, distanceKm = 2.0)
        advanceUntilIdle()

        viewModel.select(TargetPaceOption.TOP_RANK)

        assertEquals(300.0, requireNotNull(viewModel.uiState.value.selectedPaceSecPerKm), 0.001)
    }

    @Test
    fun `직접 입력은 5초 단위로 조정되고 허용 범위를 넘지 않는다`() {
        viewModel.select(TargetPaceOption.CUSTOM)
        viewModel.adjustCustomPace(5)
        assertEquals(365.0, requireNotNull(viewModel.uiState.value.selectedPaceSecPerKm), 0.001)

        repeat(200) { viewModel.adjustCustomPace(-5) }
        assertEquals(TargetPaceViewModel.MIN_CUSTOM_PACE_SEC, viewModel.uiState.value.customPaceSecPerKm, 0.001)
    }

    @Test
    fun `Activity가 재생성돼도 고른 선택지와 직접 입력 페이스가 복원된다`() {
        val savedState = SavedStateHandle()
        TargetPaceViewModel(courseRepository, savedState).apply {
            select(TargetPaceOption.CUSTOM)
            adjustCustomPace(10)
        }

        val restored = TargetPaceViewModel(courseRepository, savedState)

        assertEquals(TargetPaceOption.CUSTOM, restored.uiState.value.selection)
        assertEquals(370.0, requireNotNull(restored.uiState.value.selectedPaceSecPerKm), 0.001)
    }
}
