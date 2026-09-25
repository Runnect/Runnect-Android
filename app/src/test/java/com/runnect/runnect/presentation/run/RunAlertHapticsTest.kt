package com.runnect.runnect.presentation.run

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RunAlertHapticsTest {

    @Test
    fun `이탈 알림이 새로 뜨면 이탈 진동 패턴으로 울린다`() {
        assertArrayEquals(RunAlertHaptics.OFF_ROUTE_PATTERN, RunAlertHaptics.patternFor(null, RunAlert.OffRoute(40)))
    }

    @Test
    fun `페이스 저하 알림이 새로 뜨면 페이스 저하 진동 패턴으로 울린다`() {
        assertArrayEquals(
            RunAlertHaptics.PACE_DROP_PATTERN,
            RunAlertHaptics.patternFor(null, RunAlert.PaceDrop(300.0, 400.0))
        )
    }

    @Test
    fun `같은 알림이 유지되며 값만 바뀌면 다시 울리지 않는다`() {
        assertNull(RunAlertHaptics.patternFor(RunAlert.OffRoute(40), RunAlert.OffRoute(55)))
    }

    @Test
    fun `알림이 사라질 때는 울리지 않는다`() {
        assertNull(RunAlertHaptics.patternFor(RunAlert.OffRoute(40), null))
    }

    @Test
    fun `알림 종류가 바뀌면 새 알림 패턴으로 울린다`() {
        assertArrayEquals(
            RunAlertHaptics.OFF_ROUTE_PATTERN,
            RunAlertHaptics.patternFor(RunAlert.PaceDrop(300.0, 400.0), RunAlert.OffRoute(40))
        )
    }
}
