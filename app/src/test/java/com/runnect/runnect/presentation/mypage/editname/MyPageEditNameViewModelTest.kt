package com.runnect.runnect.presentation.mypage.editname

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.runnect.runnect.domain.common.RunnectException
import com.runnect.runnect.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageEditNameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: MyPageEditNameViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        viewModel = MyPageEditNameViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Init 인텐트는 닉네임과 프로필 이미지를 초기화한다`() = runTest(testDispatcher) {
        viewModel.state.test {
            awaitItem() // 초기 상태

            viewModel.intent(EditNameIntent.Init("러너", PROFILE_RES_ID))

            val updated = awaitItem()
            assertEquals("러너", updated.nickname)
            assertEquals(PROFILE_RES_ID, updated.profileImgResId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateNickname 인텐트는 닉네임 상태를 갱신한다`() = runTest(testDispatcher) {
        viewModel.state.test {
            awaitItem()

            viewModel.intent(EditNameIntent.UpdateNickname("새닉네임"))

            assertEquals("새닉네임", awaitItem().nickname)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Submit 성공 시 isLoading이 false로 복원되고 NavigateSuccess 이펙트가 발생한다`() =
        runTest(testDispatcher) {
            coEvery { userRepository.updateNickName(any()) } returns flow {
                delay(1)
                emit(Result.success(Unit))
            }

            turbineScope {
                val stateTurbine = viewModel.state.testIn(backgroundScope)
                val effectTurbine = viewModel.effect.testIn(backgroundScope)

                assertEquals(EditNameUiState(), stateTurbine.awaitItem())

                viewModel.intent(EditNameIntent.Init("러너", PROFILE_RES_ID))
                val initState = stateTurbine.awaitItem()
                assertEquals("러너", initState.nickname)

                viewModel.intent(EditNameIntent.Submit)

                val loading = stateTurbine.awaitItem()
                assertTrue(loading.isLoading)

                val done = stateTurbine.awaitItem()
                assertFalse(done.isLoading)

                val effect = effectTurbine.awaitItem()
                assertTrue(effect is EditNameEffect.NavigateSuccess)
                assertEquals("러너", (effect as EditNameEffect.NavigateSuccess).newNickname)

                stateTurbine.cancelAndIgnoreRemainingEvents()
                effectTurbine.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Submit 실패 시 isLoading이 false로 복원되고 ShowDuplicateError 이펙트가 발생한다`() =
        runTest(testDispatcher) {
            coEvery { userRepository.updateNickName(any()) } returns flow {
                delay(1)
                throw RunnectException(code = 400, message = "닉네임 중복")
            }

            turbineScope {
                val stateTurbine = viewModel.state.testIn(backgroundScope)
                val effectTurbine = viewModel.effect.testIn(backgroundScope)

                assertEquals(EditNameUiState(), stateTurbine.awaitItem())

                viewModel.intent(EditNameIntent.Init("중복닉", PROFILE_RES_ID))
                stateTurbine.awaitItem() // Init 상태

                viewModel.intent(EditNameIntent.Submit)

                val loading = stateTurbine.awaitItem()
                assertTrue(loading.isLoading)

                val done = stateTurbine.awaitItem()
                assertFalse(done.isLoading)

                assertTrue(effectTurbine.awaitItem() is EditNameEffect.ShowDuplicateError)

                stateTurbine.cancelAndIgnoreRemainingEvents()
                effectTurbine.cancelAndIgnoreRemainingEvents()
            }
        }

    companion object {
        private const val PROFILE_RES_ID = 1234
    }
}
