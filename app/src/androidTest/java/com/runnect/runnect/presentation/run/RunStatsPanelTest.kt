package com.runnect.runnect.presentation.run

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import org.junit.Rule
import org.junit.Test

class RunStatsPanelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setPanel(state: RunTrackingState) {
        composeTestRule.setContent { RunnectTheme { RunStatsPanel(state = state) } }
    }

    @Test
    fun 거리_시간_페이스가_각_칸에_표시된다() {
        setPanel(RunTrackingState(elapsedSec = 3_725, distanceM = 1_200.0, paceSecPerKm = 334.0))

        composeTestRule.onNodeWithTag(RunStatsPanelTestTags.DISTANCE).assertTextEquals("1.2")
        composeTestRule.onNodeWithTag(RunStatsPanelTestTags.TIME).assertTextEquals("01:02:05")
        composeTestRule.onNodeWithTag(RunStatsPanelTestTags.PACE).assertTextEquals("5'34\"")
    }

    @Test
    fun 목표_페이스를_정했으면_페이스_아래에_목표가_보인다() {
        setPanel(RunTrackingState(paceSecPerKm = 358.0, targetPaceSecPerKm = 300.0))

        composeTestRule.onNodeWithTag(RunStatsPanelTestTags.TARGET)
            .assertIsDisplayed()
            .assertTextEquals("목표 5'00\"")
    }

    @Test
    fun 목표_페이스가_없어도_같은_자리에_목표_없음을_표시한다() {
        setPanel(RunTrackingState(paceSecPerKm = null, targetPaceSecPerKm = null))

        composeTestRule.onNodeWithTag(RunStatsPanelTestTags.TARGET)
            .assertIsDisplayed()
            .assertTextEquals("목표 없음")
        composeTestRule.onNodeWithTag(RunStatsPanelTestTags.PACE).assertTextEquals("-")
    }
}
