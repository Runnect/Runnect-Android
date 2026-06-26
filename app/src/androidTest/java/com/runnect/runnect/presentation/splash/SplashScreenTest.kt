package com.runnect.runnect.presentation.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import org.junit.Rule
import org.junit.Test

class SplashScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `SplashScreen이_렌더링된다`() {
        composeTestRule.setContent {
            SplashScreen()
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }
}
