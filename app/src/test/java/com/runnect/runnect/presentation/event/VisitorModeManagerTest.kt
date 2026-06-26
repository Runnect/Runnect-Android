package com.runnect.runnect.presentation.event

import android.content.Context
import com.runnect.runnect.application.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VisitorModeManagerTest {

    private val context: Context = mockk()
    private lateinit var visitorModeManager: VisitorModeManager

    @Before
    fun setUp() {
        mockkObject(PreferenceManager)
        visitorModeManager = VisitorModeManager(context)
    }

    @After
    fun tearDown() {
        unmockkObject(PreferenceManager)
    }

    @Test
    fun `access token이 visitor면 방문자 모드다`() {
        every { PreferenceManager.getString(any(), any()) } returns "visitor"

        assertTrue(visitorModeManager.isVisitorMode)
    }

    @Test
    fun `access token이 실제 로그인 토큰이면 방문자 모드가 아니다`() {
        every { PreferenceManager.getString(any(), any()) } returns "eyJhbGciOiJIUzI1NiJ9.sometoken"

        assertFalse(visitorModeManager.isVisitorMode)
    }

    @Test
    fun `토큰이 저장된 적 없는 기본값(none)이면 방문자 모드가 아니다`() {
        every { PreferenceManager.getString(any(), any()) } returns "none"

        assertFalse(visitorModeManager.isVisitorMode)
    }

    @Test
    fun `access token이 빈 문자열이면 EXPIRED로 처리되어 방문자 모드가 아니다`() {
        every { PreferenceManager.getString(any(), any()) } returns ""

        assertFalse(visitorModeManager.isVisitorMode)
    }
}
