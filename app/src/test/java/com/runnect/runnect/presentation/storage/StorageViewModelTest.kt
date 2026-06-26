package com.runnect.runnect.presentation.storage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.runnect.runnect.data.dto.request.RequestPostCourseScrap
import com.runnect.runnect.data.dto.request.RequestPutMyDrawCourse
import com.runnect.runnect.domain.entity.MyDrawCourse
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.domain.entity.PostScrap
import com.runnect.runnect.domain.repository.CourseRepository
import com.runnect.runnect.domain.repository.StorageRepository
import com.runnect.runnect.presentation.state.UiState
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StorageViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var storageRepository: StorageRepository
    private lateinit var courseRepository: CourseRepository
    private lateinit var viewModel: StorageViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        storageRepository = mockk()
        courseRepository = mockk()
        viewModel = StorageViewModel(storageRepository, courseRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun myDrawCourse(id: Int, title: String) = MyDrawCourse(
        courseId = id,
        image = null,
        city = "서울",
        region = "강남",
        title = title
    )

    @Test
    fun `getMyDrawList 성공 시 코스 목록과 상태가 갱신된다`() = runTest(testDispatcher) {
        val courses = listOf(myDrawCourse(1, "코스1"), myDrawCourse(2, "코스2"))
        coEvery { storageRepository.getMyDrawCourse() } returns flow {
            delay(1)
            emit(Result.success(courses))
        }

        val states = mutableListOf<UiState>()
        viewModel.myDrawCourseGetState.observeForever { states.add(it) }

        viewModel.getMyDrawList()
        advanceUntilIdle()

        assertEquals(listOf(UiState.Empty, UiState.Loading, UiState.Success), states)
        assertEquals(courses, viewModel.myDrawCourses)
    }

    @Test
    fun `getMyDrawList 실패 시 에러 메시지와 Failure 상태로 갱신된다`() = runTest(testDispatcher) {
        coEvery { storageRepository.getMyDrawCourse() } returns flow {
            delay(1)
            emit(Result.failure(RuntimeException("네트워크 오류")))
        }

        val states = mutableListOf<UiState>()
        viewModel.myDrawCourseGetState.observeForever { states.add(it) }

        viewModel.getMyDrawList()
        advanceUntilIdle()

        assertEquals(listOf(UiState.Empty, UiState.Loading, UiState.Failure), states)
        assertEquals("네트워크 오류", viewModel.errorMessage.value)
    }

    @Test
    fun `deleteMyDrawCourse 성공 시 선택한 코스만 목록에서 제거된다`() = runTest(testDispatcher) {
        val courses = listOf(myDrawCourse(1, "코스1"), myDrawCourse(2, "코스2"))
        coEvery { storageRepository.getMyDrawCourse() } returns flow { emit(Result.success(courses)) }
        coEvery {
            storageRepository.deleteMyDrawCourse(RequestPutMyDrawCourse(courseIdList = listOf(1)))
        } returns flow {
            delay(1)
            emit(Result.success(Unit))
        }

        viewModel.getMyDrawList()
        advanceUntilIdle()
        viewModel.modifyItemsToDelete(1)

        val states = mutableListOf<UiState>()
        viewModel.myDrawCourseDeleteState.observeForever { states.add(it) }

        viewModel.deleteMyDrawCourse()
        advanceUntilIdle()

        assertEquals(listOf(UiState.Loading, UiState.Success), states)
        assertEquals(listOf(courses[1]), viewModel.myDrawCourses)
    }

    @Test
    fun `deleteMyDrawCourse 실패 시 Failure 상태로 갱신된다`() = runTest(testDispatcher) {
        coEvery {
            storageRepository.deleteMyDrawCourse(RequestPutMyDrawCourse(courseIdList = listOf(1)))
        } returns flow {
            delay(1)
            emit(Result.failure(RuntimeException("삭제 실패")))
        }
        viewModel.modifyItemsToDelete(1)

        val states = mutableListOf<UiState>()
        viewModel.myDrawCourseDeleteState.observeForever { states.add(it) }

        viewModel.deleteMyDrawCourse()
        advanceUntilIdle()

        assertEquals(listOf(UiState.Loading, UiState.Failure), states)
    }

    @Test
    fun `getMyScrapCourses 성공 시 스크랩 목록과 itemSize가 갱신된다`() = runTest(testDispatcher) {
        val scrapCourses = listOf(
            MyScrapCourse(courseId = 1, id = 1, publicCourseId = 10, image = null, city = "서울", region = "강남", title = "스크랩1")
        )
        coEvery { storageRepository.getMyScrapCourse() } returns flow {
            delay(1)
            emit(Result.success(scrapCourses))
        }

        val states = mutableListOf<UiStateV2<List<MyScrapCourse>>?>()
        viewModel.myScrapCourseGetState.observeForever { states.add(it) }

        viewModel.getMyScrapCourses()
        advanceUntilIdle()

        assertEquals(listOf(UiStateV2.Loading, UiStateV2.Success(scrapCourses)), states)
        assertEquals(1, viewModel.itemSize.value)
    }

    @Test
    fun `getMyScrapCourses 실패 시 Failure 상태로 갱신된다`() = runTest(testDispatcher) {
        coEvery { storageRepository.getMyScrapCourse() } returns flow {
            delay(1)
            emit(Result.failure(RuntimeException("스크랩 조회 실패")))
        }

        val states = mutableListOf<UiStateV2<List<MyScrapCourse>>?>()
        viewModel.myScrapCourseGetState.observeForever { states.add(it) }

        viewModel.getMyScrapCourses()
        advanceUntilIdle()

        assertEquals(UiStateV2.Failure("스크랩 조회 실패"), states.last())
    }

    @Test
    fun `postCourseScrap 성공 시 Success 상태로 갱신된다`() = runTest(testDispatcher) {
        val postScrap = PostScrap(publicCourseId = 10L, scrapCount = 3L, scrapTF = true)
        coEvery {
            courseRepository.postCourseScrap(RequestPostCourseScrap(publicCourseId = 10, scrapTF = "true"))
        } returns flow {
            delay(1)
            emit(Result.success(postScrap))
        }

        val states = mutableListOf<UiStateV2<PostScrap>>()
        viewModel.courseScrapState.observeForever { states.add(it) }

        viewModel.postCourseScrap(id = 10, scrapTF = true)
        advanceUntilIdle()

        assertEquals(listOf(UiStateV2.Loading, UiStateV2.Success(postScrap)), states)
    }

    @Test
    fun `modifyItemsToDelete는 같은 id를 다시 호출하면 선택을 해제한다`() = runTest(testDispatcher) {
        viewModel.modifyItemsToDelete(1)
        viewModel.modifyItemsToDelete(2)
        assertEquals(listOf(1, 2), viewModel.itemsToDeleteLiveData.value)

        viewModel.modifyItemsToDelete(1)
        assertEquals(listOf(2), viewModel.itemsToDeleteLiveData.value)
    }

    @Test
    fun `clearItemsToDelete는 선택 목록을 비운다`() = runTest(testDispatcher) {
        viewModel.modifyItemsToDelete(1)

        viewModel.clearItemsToDelete()

        assertEquals(emptyList<Int>(), viewModel.itemsToDeleteLiveData.value)
    }

    @Test
    fun `saveClickedCourseId는 clickedCourseId를 갱신한다`() = runTest(testDispatcher) {
        viewModel.saveClickedCourseId(7)

        assertEquals(7, viewModel.clickedCourseId)
    }
}
