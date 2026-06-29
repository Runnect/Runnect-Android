package com.runnect.runnect.presentation.countdown

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import com.runnect.runnect.R
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CountDownScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 카운트다운_배경_숫자_안내문구가_노출된다() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            RunnectTheme {
                CountDownContent(count = 3)
            }
        }

        composeTestRule.onNodeWithTag(CountDownScreenTestTags.BACKGROUND).assertIsDisplayed()
        composeTestRule.onNodeWithTag(CountDownScreenTestTags.NUMBER)
            .assertIsDisplayed()
            .assertContentDescriptionEquals("3")
        composeTestRule.onNodeWithTag(CountDownScreenTestTags.DESCRIPTION)
            .assertIsDisplayed()
            .assertTextEquals(context.getString(R.string.count_down_desc))
    }

    @Test
    fun 카운트다운이_끝나면_완료_콜백이_호출된다() {
        var finishedCount = 0
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            RunnectTheme {
                CountDownRoute(
                    onFinished = { finishedCount += 1 }
                )
            }
        }

        composeTestRule.waitForIdle()
        repeat(3) {
            composeTestRule.mainClock.advanceTimeBy(CountDownStateMachine.TICK_MILLIS)
            composeTestRule.waitForIdle()
        }
        composeTestRule.waitUntil(timeoutMillis = 5_000L) {
            finishedCount == 1
        }

        assertEquals(1, finishedCount)
    }
}
