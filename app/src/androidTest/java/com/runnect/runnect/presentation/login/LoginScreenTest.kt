package com.runnect.runnect.presentation.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 로그인_버튼과_방문자_모드가_노출된다() {
        composeTestRule.setContent {
            RunnectTheme {
                LoginScreen(
                    state = LoginUiState(),
                    onGoogleLoginClick = {},
                    onKakaoLoginClick = {},
                    onVisitorModeClick = {},
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNodeWithText("구글로 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("카카오로 로그인").assertIsDisplayed()
        composeTestRule.onNodeWithText("회원가입 없이 둘러보기").assertIsDisplayed()
    }

    @Test
    fun 각_버튼을_누르면_대응하는_콜백이_호출된다() {
        val clicked = mutableListOf<String>()

        composeTestRule.setContent {
            RunnectTheme {
                LoginScreen(
                    state = LoginUiState(),
                    onGoogleLoginClick = { clicked.add("google") },
                    onKakaoLoginClick = { clicked.add("kakao") },
                    onVisitorModeClick = { clicked.add("visitor") },
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(LoginScreenTestTags.GOOGLE_LOGIN_BUTTON).performClick()
        composeTestRule.onNodeWithTag(LoginScreenTestTags.KAKAO_LOGIN_BUTTON).performClick()
        composeTestRule.onNodeWithTag(LoginScreenTestTags.VISITOR_MODE_BUTTON).performClick()

        assertEquals(listOf("google", "kakao", "visitor"), clicked)
    }

    @Test
    fun 로딩_상태면_인디케이터가_노출된다() {
        composeTestRule.setContent {
            RunnectTheme {
                LoginScreen(
                    state = LoginUiState(isLoading = true),
                    onGoogleLoginClick = {},
                    onKakaoLoginClick = {},
                    onVisitorModeClick = {},
                    onErrorShown = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(LoginScreenTestTags.LOADING_INDICATOR).assertIsDisplayed()
    }
}
