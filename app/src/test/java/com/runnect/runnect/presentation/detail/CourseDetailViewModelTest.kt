package com.runnect.runnect.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.runnect.runnect.domain.entity.CourseRanking
import com.runnect.runnect.domain.entity.CourseRankingEntry
import com.runnect.runnect.domain.entity.MyCourseRanking
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.UserRepository
import com.runnect.runnect.presentation.state.UiStateV2
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CourseDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var courseRepository: CourseRepository
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: CourseDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        courseRepository = mockk()
        userRepository = mockk()
        viewModel = CourseDetailViewModel(courseRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCourseRanking 성공 시 랭킹 목록으로 상태가 갱신된다`() = runTest(testDispatcher) {
        val ranking = CourseRanking(
            totalCount = 2,
            entries = listOf(
                CourseRankingEntry(1, 1, "런너A", 100, "11:24", "4'58\"/km"),
                CourseRankingEntry(2, 2, "런너B", 101, "11:47", "5'07\"/km"),
            )
        )
        coEvery { courseRepository.getCourseRanking(courseId = 1, limit = 10) } returns flow {
            delay(1)
            emit(Result.success(ranking))
        }

        viewModel.getCourseRanking(1)
        advanceUntilIdle()

        assertEquals(UiStateV2.Success(ranking), viewModel.courseRankingState.value)
    }

    @Test
    fun `getCourseRanking 실패 시 Failure 상태로 갱신된다`() = runTest(testDispatcher) {
        coEvery { courseRepository.getCourseRanking(courseId = 1, limit = 10) } returns flow {
            delay(1)
            emit(Result.failure(RuntimeException("네트워크 오류")))
        }

        viewModel.getCourseRanking(1)
        advanceUntilIdle()

        assertTrue(viewModel.courseRankingState.value is UiStateV2.Failure)
    }

    @Test
    fun `getMyCourseRanking 성공 시 내 랭킹 상태가 갱신된다`() = runTest(testDispatcher) {
        val myRanking = MyCourseRanking(
            hasRecord = true,
            rank = 14,
            userId = 57,
            nickname = "나",
            time = "14:52",
            pace = "6'28\"/km"
        )
        coEvery { courseRepository.getMyCourseRanking(courseId = 1) } returns flow {
            delay(1)
            emit(Result.success(myRanking))
        }

        viewModel.getMyCourseRanking(1)
        advanceUntilIdle()

        assertEquals(UiStateV2.Success(myRanking), viewModel.myCourseRankingState.value)
    }

    @Test
    fun `기록이 없는 유저는 hasRecord=false 상태를 성공으로 받는다`() = runTest(testDispatcher) {
        val myRanking = MyCourseRanking(
            hasRecord = false,
            rank = null,
            userId = 57,
            nickname = null,
            time = null,
            pace = null
        )
        coEvery { courseRepository.getMyCourseRanking(courseId = 1) } returns flow {
            delay(1)
            emit(Result.success(myRanking))
        }

        viewModel.getMyCourseRanking(1)
        advanceUntilIdle()

        val state = viewModel.myCourseRankingState.value as UiStateV2.Success
        assertEquals(false, state.data.hasRecord)
    }
}
