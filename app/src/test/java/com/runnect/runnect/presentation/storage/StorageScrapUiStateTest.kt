package com.runnect.runnect.presentation.storage

import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.domain.entity.PostScrap
import com.runnect.runnect.presentation.state.UiStateV2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageScrapUiStateTest {

    private fun course(title: String) = MyScrapCourse(
        courseId = 1,
        id = 1,
        publicCourseId = 10,
        image = null,
        city = "서울",
        region = "강남",
        title = title
    )

    @Test
    fun `목록_조회만_Loading이어도_isLoading은_true다`() {
        val state = StorageScrapUiState.from(
            getState = UiStateV2.Loading,
            scrapState = null,
            courses = emptyList(),
            errorMessage = null
        )

        assertTrue(state.isLoading)
    }

    @Test
    fun `스크랩_토글만_Loading이어도_isLoading은_true다`() {
        val state = StorageScrapUiState.from(
            getState = UiStateV2.Success(emptyList()),
            scrapState = UiStateV2.Loading,
            courses = emptyList(),
            errorMessage = null
        )

        assertTrue(state.isLoading)
    }

    @Test
    fun `둘다_Loading이_아니면_isLoading은_false다`() {
        val state = StorageScrapUiState.from(
            getState = UiStateV2.Success(emptyList()),
            scrapState = UiStateV2.Success(PostScrap(publicCourseId = 1L, scrapCount = 0L, scrapTF = false)),
            courses = emptyList(),
            errorMessage = null
        )

        assertFalse(state.isLoading)
    }

    @Test
    fun `courses와_errorMessage는_그대로_전달된다`() {
        val course = course(title = "테스트 코스")

        val state = StorageScrapUiState.from(
            getState = UiStateV2.Failure("에러"),
            scrapState = null,
            courses = listOf(course),
            errorMessage = "에러"
        )

        assertEquals(listOf(course), state.courses)
        assertEquals("에러", state.errorMessage)
    }
}
