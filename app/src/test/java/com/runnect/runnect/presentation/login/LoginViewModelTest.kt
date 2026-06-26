package com.runnect.runnect.presentation.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.runnect.runnect.data.dto.LoginDTO
import com.runnect.runnect.data.dto.request.RequestPostLogin
import com.runnect.runnect.domain.repository.LoginRepository
import com.runnect.runnect.presentation.state.UiState
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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var loginRepository: LoginRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginRepository = mockk()
        viewModel = LoginViewModel(loginRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `postLogin 성공 시 loginResult와 Success 상태로 갱신된다`() = runTest(testDispatcher) {
        val request = RequestPostLogin(token = "social-token", provider = "GOOGLE")
        val loginResult = LoginDTO(
            accessToken = "access",
            refreshToken = "refresh",
            email = "test@runnect.com",
            type = "Login"
        )
        coEvery { loginRepository.postLogin(request) } returns flow {
            delay(1)
            emit(Result.success(loginResult))
        }

        val states = mutableListOf<UiState>()
        viewModel.loginState.observeForever { states.add(it) }

        viewModel.postLogin(request)
        advanceUntilIdle()

        assertEquals(listOf(UiState.Empty, UiState.Loading, UiState.Success), states)
        assertEquals(loginResult, viewModel.loginResult.value)
    }

    @Test
    fun `postLogin 실패 시 errorMessage와 Failure 상태로 갱신된다`() = runTest(testDispatcher) {
        val request = RequestPostLogin(token = "social-token", provider = "KAKAO")
        coEvery { loginRepository.postLogin(request) } returns flow {
            delay(1)
            emit(Result.failure(RuntimeException("로그인 실패")))
        }

        val states = mutableListOf<UiState>()
        viewModel.loginState.observeForever { states.add(it) }

        viewModel.postLogin(request)
        advanceUntilIdle()

        assertEquals(listOf(UiState.Empty, UiState.Loading, UiState.Failure), states)
        assertEquals("로그인 실패 (unknown)", viewModel.errorMessage.value)
    }

    @Test
    fun `clearErrorMessage는_errorMessage를_null로_초기화한다`() {
        viewModel.errorMessage.value = "로그인 실패"

        viewModel.clearErrorMessage()

        assertEquals(null, viewModel.errorMessage.value)
    }
}
