package com.runnect.runnect.presentation.run

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PaceFormatTest {

    @Test
    fun `초 단위 페이스를 분과 초로 표시한다`() {
        assertEquals("5'48\"", PaceFormat.format(348.0))
        assertEquals("6'05\"", PaceFormat.format(365.0))
    }

    @Test
    fun `페이스가 없으면 대시로 표시한다`() {
        assertEquals("-", PaceFormat.format(null))
        assertEquals("-", PaceFormat.format(Double.POSITIVE_INFINITY))
    }

    @Test
    fun `시분초와 분초 형식의 소요 시간을 초로 변환한다`() {
        assertEquals(3_723, PaceFormat.parseDurationSec("01:02:03"))
        assertEquals(684, PaceFormat.parseDurationSec("11:24"))
    }

    @Test
    fun `형식이 맞지 않는 소요 시간은 null이다`() {
        assertNull(PaceFormat.parseDurationSec(null))
        assertNull(PaceFormat.parseDurationSec("abc"))
        assertNull(PaceFormat.parseDurationSec("5"))
        assertNull(PaceFormat.parseDurationSec("0:6:-50"))
    }

    @Test
    fun `소요 시간과 거리로 초 단위 페이스를 계산한다`() {
        assertEquals(300.0, requireNotNull(PaceFormat.paceFromDuration("00:10:00", 2.0)), 0.001)
    }

    @Test
    fun `거리가 없거나 0이면 페이스를 계산하지 않는다`() {
        assertNull(PaceFormat.paceFromDuration("00:10:00", null))
        assertNull(PaceFormat.paceFromDuration("00:10:00", 0.0))
    }
}
