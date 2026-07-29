package com.runnect.runnect.presentation.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.runnect.runnect.data.dto.request.RequestPatchNickName
import com.runnect.runnect.domain.repository.UserRepository
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
class GiveNickNameViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: GiveNickNameViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        viewModel = GiveNickNameViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateNickNameInput은 닉네임 값을 갱신한다`() {
        viewModel.updateNickNameInput("러넥트")

        assertEquals("러넥트", viewModel.nickName.value)
    }

    @Test
    fun `updateNickName 성공 시 Loading 이후 Success 상태로 갱신된다`() = runTest(testDispatcher) {
        val request = RequestPatchNickName("러너")
        viewModel.updateNickNameInput("러너")
        coEvery { userRepository.updateNickName(request) } returns flow {
            delay(1)
            emit(Result.success(Unit))
        }

        val states = mutableListOf<UiState>()
        viewModel.uiState.observeForever { states.add(it) }

        viewModel.updateNickName()
        advanceUntilIdle()

        assertEquals(listOf(UiState.Loading, UiState.Success), states)
    }

    @Test
    fun `updateNickName 실패 시 statusCode와 Failure 상태로 갱신된다`() = runTest(testDispatcher) {
        val request = RequestPatchNickName("중복")
        viewModel.updateNickNameInput("중복")
        coEvery { userRepository.updateNickName(request) } returns flow {
            delay(1)
            emit(Result.failure(RuntimeException("duplicated nickname")))
        }

        val states = mutableListOf<UiState>()
        viewModel.uiState.observeForever { states.add(it) }

        viewModel.updateNickName()
        advanceUntilIdle()

        assertEquals(listOf(UiState.Loading, UiState.Failure), states)
        assertEquals(GiveNickNameViewModel.REDUNDANT_NICKNAME_ERROR, viewModel.statusCode.value)
    }
}
