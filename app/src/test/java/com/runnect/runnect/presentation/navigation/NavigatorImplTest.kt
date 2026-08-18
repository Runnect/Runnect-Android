package com.runnect.runnect.presentation.navigation

import android.content.Context
import android.content.Intent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class NavigatorImplTest {

    private lateinit var navigator: NavigatorImpl
    private lateinit var context: Context

    @Before
    fun setUp() {
        navigator = NavigatorImpl()
        context = mockk(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(any<String>(), any<java.io.Serializable>()) } returns mockk(relaxed = true)
        every { anyConstructed<Intent>().addFlags(any()) } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkConstructor(Intent::class)
    }

    @Test
    fun `DEFAULT 모드는 플래그를 추가하지 않는다`() {
        navigator.navigateToMain(context, MainTab.STORAGE, NavigationMode.DEFAULT)

        verify { anyConstructed<Intent>().addFlags(0) }
        verify { context.startActivity(any()) }
    }

    @Test
    fun `CLEAR_TOP 모드는 FLAG_ACTIVITY_CLEAR_TOP을 추가한다`() {
        navigator.navigateToMain(context, MainTab.DISCOVER, NavigationMode.CLEAR_TOP)

        verify { anyConstructed<Intent>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
    }

    @Test
    fun `NEW_TASK_CLEAR_TASK 모드는 두 플래그를 함께 추가한다`() {
        navigator.navigateToMain(context, MainTab.STORAGE, NavigationMode.NEW_TASK_CLEAR_TASK)

        verify {
            anyConstructed<Intent>().addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }
    }

    @Test
    fun `tab이 지정되면 EXTRA_MAIN_TAB extra를 담는다`() {
        navigator.navigateToMain(context, MainTab.MY_PAGE, NavigationMode.DEFAULT)

        verify { anyConstructed<Intent>().putExtra(EXTRA_MAIN_TAB, MainTab.MY_PAGE) }
    }

    @Test
    fun `tab이 null이면 extra를 담지 않는다`() {
        navigator.navigateToMain(context, null, NavigationMode.DEFAULT)

        verify(exactly = 0) { anyConstructed<Intent>().putExtra(any<String>(), any<java.io.Serializable>()) }
        verify { context.startActivity(any()) }
    }
}
