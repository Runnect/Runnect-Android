package com.runnect.runnect.presentation.run

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 러닝 화면 기능별 테스트 상황(debug 전용). 시뮬레이터 패널 버튼과 scripts/gps-sim-scenarios.sh(adb)가
 * 같은 정의를 쓰므로, 손으로 눌러보는 상황과 자동 TC가 항상 같은 조건이다.
 * adb: am broadcast -a com.runnect.debug.GPS_SIM -p com.runnect.runnect --es cmd scenario --es id <id>
 */
enum class RunSimScenario(
    val id: String,
    val title: String,
    val expectation: String,
    private val setUp: (GpsRouteSimulator, RunController?, CoroutineScope) -> Unit,
) {
    NORMAL("normal", "정상 주행", "거리·페이스 표시, 알림 없음 (목표 없음, 5'57\")", { sim, run, _ ->
        run?.setTargetPace(null)
        sim.offRouteM = 0.0
        sim.isHalted = false
        sim.speed = GpsRouteSimulator.Speed.NORMAL
    }),
    FASTER_THAN_TARGET("faster", "목표보다 빠르게", "알림 없음 (목표 5'30\", 실제 4'46\")", { sim, run, _ ->
        run?.setTargetPace(330.0)
        sim.speed = GpsRouteSimulator.Speed.FAST
    }),
    SLIGHTLY_SLOWER("slightly_slower", "목표보다 조금 느리게", "알림 없음 (목표 5'30\", 실제 5'57\" · 20% 미만)", { sim, run, _ ->
        run?.setTargetPace(330.0)
        sim.speed = GpsRouteSimulator.Speed.NORMAL
    }),
    PACE_DROP("pace_drop", "페이스 저하", "약 20초 뒤 노란 배너 + 긴 진동 1번 (목표 5'00\", 실제 7'35\")", { sim, run, _ ->
        run?.setTargetPace(300.0)
        sim.speed = GpsRouteSimulator.Speed.SLOW
    }),
    PACE_RECOVER("pace_recover", "페이스 회복", "약 15초 안에 노란 배너 사라짐 (4'46\")", { sim, _, _ ->
        sim.speed = GpsRouteSimulator.Speed.FAST
    }),
    SHORT_DETOUR("short_detour", "잠깐 이탈 후 복귀", "알림 없음 (6초만 벗어났다 돌아옴)", { sim, _, scope ->
        sim.offRouteM = 50.0
        scope.launch {
            delay(6_000)
            sim.offRouteM = 0.0
        }
    }),
    OFF_ROUTE("off_route", "코스 이탈", "약 10초 뒤 빨간 배너 + 짧은 진동 2번 (50m 벗어남)", { sim, _, _ ->
        sim.offRouteM = 50.0
    }),
    BACK_ON_ROUTE("back_on_route", "코스 복귀", "몇 초 안에 빨간 배너 사라짐", { sim, _, _ ->
        sim.offRouteM = 0.0
    }),
    HALT("halt", "제자리 멈춤", "약 20초 뒤 자동 일시정지", { sim, _, _ ->
        sim.isHalted = true
    }),
    RESUME("resume", "다시 달리기", "일시정지 해제, 달리는 동안 다시 멈추지 않음", { sim, run, _ ->
        sim.isHalted = false
        run?.resume()
    });

    /** 시뮬레이터가 멈춰 있으면 먼저 출발시키고 이 상황을 만든다. */
    fun apply(simulator: GpsRouteSimulator, controller: RunController?, scope: CoroutineScope) {
        if (!simulator.isRunning) simulator.start()
        setUp(simulator, controller, scope)
    }

    companion object {
        fun of(id: String?) = entries.firstOrNull { it.id == id }
    }
}
