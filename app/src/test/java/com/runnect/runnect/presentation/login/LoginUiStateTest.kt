package com.runnect.runnect.presentation.login

import com.runnect.runnect.presentation.state.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUiStateTest {

    @Test
    fun `loginState가_Loading이면_isLoading은_true다`() {
        val state = LoginUiState.from(
            loginState = UiState.Loading,
            errorMessage = null
        )

        assertTrue(state.isLoading)
    }

    @Test
    fun `loginState가_Loading이_아니면_isLoading은_false다`() {
        val state = LoginUiState.from(
            loginState = UiState.Success,
            errorMessage = null
        )

        assertFalse(state.isLoading)
    }

    @Test
    fun `errorMessage는_그대로_전달된다`() {
        val state = LoginUiState.from(
            loginState = UiState.Failure,
            errorMessage = "로그인 실패"
        )

        assertEquals("로그인 실패", state.errorMessage)
    }
}
