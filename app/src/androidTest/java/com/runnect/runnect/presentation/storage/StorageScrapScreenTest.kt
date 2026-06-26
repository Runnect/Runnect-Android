package com.runnect.runnect.presentation.storage

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StorageScrapScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun `카드탭_시_onScrapItemClick이_해당_코스로_호출된다`() {
        val testCourse = course(title = "테스트 코스")
        var clicked: MyScrapCourse? = null

        composeTestRule.setContent {
            RunnectTheme {
                StorageScrapScreen(
                    state = StorageScrapUiState(courses = listOf(testCourse)),
                    onRefresh = {},
                    onScrapItemClick = { clicked = it },
                    onHeartClick = {},
                    onGoToScrapClick = {},
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNodeWithText("테스트 코스").performClick()

        assertEquals(testCourse, clicked)
    }

    @Test
    fun `로딩_중에는_스크랩이_없어도_빈_화면이_보이지_않는다`() {
        composeTestRule.setContent {
            RunnectTheme {
                StorageScrapScreen(
                    state = StorageScrapUiState(courses = emptyList(), isLoading = true),
                    onRefresh = {},
                    onScrapItemClick = {},
                    onHeartClick = {},
                    onGoToScrapClick = {},
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNodeWithText("아직 스크랩한 코스가 없어요", substring = true)
            .assertDoesNotExist()
    }

    @Test
    fun `로딩이_끝나고_스크랩이_없으면_빈_화면이_보인다`() {
        composeTestRule.setContent {
            RunnectTheme {
                StorageScrapScreen(
                    state = StorageScrapUiState(courses = emptyList(), isLoading = false),
                    onRefresh = {},
                    onScrapItemClick = {},
                    onHeartClick = {},
                    onGoToScrapClick = {},
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNodeWithText("아직 스크랩한 코스가 없어요", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun `isLoading이_true이면_로딩_인디케이터가_보인다`() {
        composeTestRule.setContent {
            RunnectTheme {
                StorageScrapScreen(
                    state = StorageScrapUiState(courses = listOf(course("코스")), isLoading = true),
                    onRefresh = {},
                    onScrapItemClick = {},
                    onHeartClick = {},
                    onGoToScrapClick = {},
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }
}
