package com.runnect.runnect.presentation.mypage

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.runnect.runnect.domain.entity.User
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: MyPageViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadUserInfo 성공 시 유저 정보로 상태가 갱신된다`() = runTest(testDispatcher) {
        val user = User(
            email = "runner@runnect.com",
            latestStamp = "CSPR0",
            level = 3,
            levelPercent = 42,
            nickname = "러너"
        )
        coEvery { userRepository.getUserInfo() } returns flow {
            delay(1) // onLoading 상태를 별도 프레임으로 관찰하기 위한 실제 suspension 지점
            emit(Result.success(user))
        }
        viewModel = MyPageViewModel(userRepository)

        viewModel.state.test {
            val initial = awaitItem()
            assertEquals(MyPageUiState(), initial)
            assertTrue("초기 상태는 isLoading=true가 기본값", initial.isLoading)

            viewModel.intent(MyPageIntent.LoadUserInfo)

            // MyPageUiState의 isLoading 기본값이 true라서, onLoading reduce는
            // 초기 상태와 동일해 StateFlow가 별도로 emit하지 않고 곧바로 결과 상태로 넘어간다.
            val success = awaitItem()
            assertFalse(success.isLoading)
            assertEquals("러너", success.nickname)
            assertEquals("CSPR0", success.stampId)
            assertEquals("3", success.level)
            assertEquals(42, success.levelPercent)
            assertEquals("runner@runnect.com", success.email)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadUserInfo 실패 시 에러 상태와 ShowError 이펙트가 발생한다`() = runTest(testDispatcher) {
        coEvery { userRepository.getUserInfo() } returns flow {
            delay(1)
            throw RuntimeException("네트워크 오류")
        }
        viewModel = MyPageViewModel(userRepository)

        turbineScope {
            val stateTurbine = viewModel.state.testIn(backgroundScope)
            val effectTurbine = viewModel.effect.testIn(backgroundScope)

            assertEquals(MyPageUiState(), stateTurbine.awaitItem())

            viewModel.intent(MyPageIntent.LoadUserInfo)

            // 초기 상태가 이미 isLoading=true라 onLoading reduce는 별도로 emit되지 않는다.
            val failure = stateTurbine.awaitItem()
            assertFalse(failure.isLoading)
            assertEquals("네트워크 오류 (unknown)", failure.error)

            val effect = effectTurbine.awaitItem()
            assertTrue(effect is MyPageEffect.ShowError)

            stateTurbine.cancelAndIgnoreRemainingEvents()
            effectTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateNickname 인텐트는 닉네임 상태를 갱신한다`() = runTest(testDispatcher) {
        viewModel = MyPageViewModel(userRepository)

        viewModel.state.test {
            assertEquals(MyPageUiState(), awaitItem())

            viewModel.intent(MyPageIntent.UpdateNickname("새닉네임"))

            assertEquals("새닉네임", awaitItem().nickname)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
