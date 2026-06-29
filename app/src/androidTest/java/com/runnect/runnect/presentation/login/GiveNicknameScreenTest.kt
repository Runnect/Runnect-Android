package com.runnect.runnect.presentation.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GiveNicknameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 닉네임_입력_화면_요소가_노출된다() {
        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState(),
                    onNickNameChange = {},
                    onStartClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("RUNNECT").assertIsDisplayed()
        composeTestRule.onNodeWithText("에서 사용할").assertIsDisplayed()
        composeTestRule.onNodeWithText("이름을 입력해주세요").assertIsDisplayed()
        composeTestRule.onNodeWithText("닉네임을 입력해주세요").assertIsDisplayed()
        composeTestRule.onNodeWithText("시작하기").assertIsDisplayed()
    }

    @Test
    fun 닉네임이_비어있으면_시작하기_버튼은_비활성화된다() {
        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState.from(
                        nickName = "",
                        uiState = com.runnect.runnect.presentation.state.UiState.Empty
                    ),
                    onNickNameChange = {},
                    onStartClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(GiveNicknameScreenTestTags.START_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun 닉네임을_입력하면_변경_콜백이_호출된다() {
        val inputs = mutableListOf<String>()

        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState(),
                    onNickNameChange = { inputs.add(it) },
                    onStartClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(GiveNicknameScreenTestTags.NICKNAME_INPUT)
            .performTextInput("러너")

        assertEquals("러너", inputs.last())
    }

    @Test
    fun 닉네임을_최대_길이보다_길게_입력하면_잘라서_콜백이_호출된다() {
        val inputs = mutableListOf<String>()

        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState(),
                    onNickNameChange = { inputs.add(it) },
                    onStartClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(GiveNicknameScreenTestTags.NICKNAME_INPUT)
            .performTextInput("러너러너러너러너")

        assertEquals("러너러너러너러", inputs.last())
    }

    @Test
    fun 로딩_상태면_닉네임_입력은_비활성화된다() {
        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState.from(
                        nickName = "러너",
                        uiState = com.runnect.runnect.presentation.state.UiState.Loading
                    ),
                    onNickNameChange = {},
                    onStartClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(GiveNicknameScreenTestTags.NICKNAME_INPUT)
            .assertIsNotEnabled()
    }

    @Test
    fun 시작하기_버튼을_누르면_콜백이_호출된다() {
        var clickedCount = 0

        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState.from(
                        nickName = "러너",
                        uiState = com.runnect.runnect.presentation.state.UiState.Empty
                    ),
                    onNickNameChange = {},
                    onStartClick = { clickedCount += 1 }
                )
            }
        }

        composeTestRule.onNodeWithTag(GiveNicknameScreenTestTags.START_BUTTON).performClick()

        assertEquals(1, clickedCount)
    }

    @Test
    fun 로딩_상태면_인디케이터가_노출된다() {
        composeTestRule.setContent {
            RunnectTheme {
                GiveNicknameScreen(
                    state = GiveNicknameUiState.from(
                        nickName = "러너",
                        uiState = com.runnect.runnect.presentation.state.UiState.Loading
                    ),
                    onNickNameChange = {},
                    onStartClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(GiveNicknameScreenTestTags.LOADING_INDICATOR)
            .assertIsDisplayed()
    }
}
