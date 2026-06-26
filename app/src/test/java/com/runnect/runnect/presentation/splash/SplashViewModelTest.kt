package com.runnect.runnect.presentation.splash

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태에서 isReady는 false다`() {
        val viewModel = SplashViewModel()
        assertFalse(viewModel.isReady.value)
    }

    @Test
    fun `1초 경과 전에는 isReady가 false다`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        advanceTimeBy(SplashViewModel.SPLASH_DELAY - 1)
        assertFalse(viewModel.isReady.value)
    }

    @Test
    fun `1초 경과 후 isReady가 true가 된다`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.isReady.value)
    }

    @Test
    fun `1초 후 navigateEvent가 emit된다`() = runTest(testDispatcher) {
        val viewModel = SplashViewModel()
        viewModel.navigateEvent.test {
            advanceTimeBy(SplashViewModel.SPLASH_DELAY)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
