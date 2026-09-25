package com.runnect.runnect.presentation.run

import com.naver.maps.geometry.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RunAlertEvaluatorTest {

    private val evaluator = RunAlertEvaluator()

    private fun offRoute(now: Long, distanceM: Double = 45.0) =
        evaluator.evaluate(now, distanceToRouteM = distanceM, paceSecPerKm = null, targetPaceSecPerKm = null)

    private fun pace(now: Long, paceSecPerKm: Double, target: Double = 300.0, distanceM: Double = 0.0) =
        evaluator.evaluate(now, distanceToRouteM = distanceM, paceSecPerKm = paceSecPerKm, targetPaceSecPerKm = target)

    @Test
    fun `경로에서 30m 넘게 벗어나도 8초 연속되기 전에는 이탈 알림을 띄우지 않는다`() {
        assertNull(offRoute(0))
        assertNull(offRoute(7_999))
    }

    @Test
    fun `경로에서 30m 넘게 벗어난 상태가 8초 연속되면 이탈 거리와 함께 알림을 띄운다`() {
        offRoute(0)
        assertEquals(RunAlert.OffRoute(45), offRoute(8_000))
    }

    @Test
    fun `중간에 경로로 돌아오면 연속 시간이 초기화된다`() {
        offRoute(0)
        offRoute(5_000, distanceM = 10.0)
        assertNull(offRoute(9_000)) // 다시 벗어난 시점(9초)부터 새로 셈
        assertNull(offRoute(16_999))
        assertEquals(RunAlert.OffRoute(45), offRoute(17_000))
    }

    @Test
    fun `경로로 돌아오면 이탈 알림이 바로 사라진다`() {
        offRoute(0)
        offRoute(8_000)
        assertNull(offRoute(9_000, distanceM = 5.0))
    }

    @Test
    fun `이탈 알림이 떠 있는 동안에는 거리가 바뀌어도 계속 유지된다`() {
        offRoute(0)
        offRoute(8_000)
        assertEquals(RunAlert.OffRoute(80), offRoute(70_000, distanceM = 80.0))
    }

    @Test
    fun `이탈 알림은 마지막으로 띄운 뒤 60초 안에는 다시 띄우지 않는다`() {
        offRoute(0)
        offRoute(8_000) // 알림 발생
        offRoute(9_000, distanceM = 5.0) // 복귀
        offRoute(10_000)
        assertNull(offRoute(18_000)) // 조건은 충족했지만 쿨다운 중
        assertEquals(RunAlert.OffRoute(45), offRoute(68_000)) // 쿨다운 종료
    }

    @Test
    fun `목표보다 20퍼센트 이상 느린 상태가 10초 연속되면 페이스 저하 알림을 띄운다`() {
        assertNull(pace(0, paceSecPerKm = 360.0))
        assertNull(pace(9_999, paceSecPerKm = 360.0))
        assertEquals(RunAlert.PaceDrop(300.0, 360.0), pace(10_000, paceSecPerKm = 360.0))
    }

    @Test
    fun `목표보다 느리더라도 20퍼센트 미만이면 페이스 저하 알림을 띄우지 않는다`() {
        pace(0, paceSecPerKm = 359.0)
        assertNull(pace(20_000, paceSecPerKm = 359.0))
    }

    @Test
    fun `목표 페이스가 없으면 페이스 저하 알림을 띄우지 않는다`() {
        evaluator.evaluate(0, 0.0, 600.0, null)
        assertNull(evaluator.evaluate(20_000, 0.0, 600.0, null))
    }

    @Test
    fun `이탈과 페이스 저하가 동시에 해당하면 이탈 알림을 우선한다`() {
        pace(0, paceSecPerKm = 400.0, distanceM = 50.0)
        val alert = pace(10_000, paceSecPerKm = 400.0, distanceM = 50.0)
        assertEquals(RunAlert.OffRoute(50), alert)
    }

    @Test
    fun `이탈 알림에 가려졌던 페이스 저하 알림은 쿨다운 없이 이탈 해소 직후 바로 뜬다`() {
        pace(0, paceSecPerKm = 400.0, distanceM = 50.0)
        pace(10_000, paceSecPerKm = 400.0, distanceM = 50.0)
        val alert = pace(11_000, paceSecPerKm = 400.0, distanceM = 5.0)
        assertEquals(RunAlert.PaceDrop(300.0, 400.0), alert)
    }

    @Test
    fun `reset하면 떠 있던 알림과 연속 판정이 초기화된다`() {
        offRoute(0)
        offRoute(8_000)
        evaluator.reset()
        assertNull(offRoute(9_000))
    }

    @Test
    fun `경로 위 선분까지의 최단 거리를 계산한다`() {
        // 동서 방향 선분에서 북쪽으로 약 0.0003도(약 33m) 떨어진 점
        val path = listOf(LatLng(37.5665, 126.9780), LatLng(37.5665, 126.9820))
        val distance = RouteGeometry.distanceToPathM(LatLng(37.5668, 126.9800), path)
        requireNotNull(distance)
        assertTrue("약 33m여야 한다: $distance", distance in 31.0..35.0)
    }

    @Test
    fun `선분 끝을 벗어난 점은 가장 가까운 끝점까지의 거리로 계산한다`() {
        val path = listOf(LatLng(37.5665, 126.9780), LatLng(37.5665, 126.9790))
        val point = LatLng(37.5665, 126.9800) // 선분 동쪽 끝에서 약 88m 더 동쪽
        val distance = requireNotNull(RouteGeometry.distanceToPathM(point, path))
        assertEquals(point.distanceTo(path.last()), distance, 1.0)
    }

    @Test
    fun `경로가 비어 있으면 거리를 계산하지 않는다`() {
        assertNull(RouteGeometry.distanceToPathM(LatLng(37.5665, 126.9780), emptyList()))
    }

    @Test
    fun `같은 종류에서 값만 바뀐 알림은 새 알림이 아니다`() {
        assertEquals(false, RunAlert.OffRoute(55).isNewComparedTo(RunAlert.OffRoute(40)))
        assertEquals(false, RunAlert.PaceDrop(300.0, 410.0).isNewComparedTo(RunAlert.PaceDrop(300.0, 400.0)))
    }

    @Test
    fun `없다가 생기거나 종류가 바뀐 알림은 새 알림이다`() {
        assertEquals(true, RunAlert.OffRoute(40).isNewComparedTo(null))
        assertEquals(true, RunAlert.OffRoute(40).isNewComparedTo(RunAlert.PaceDrop(300.0, 400.0)))
    }
}
