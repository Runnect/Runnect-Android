package com.runnect.runnect.presentation.run

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng

/**
 * debug 빌드 전용 러닝 화면 GPS 시뮬레이터 패널. 화면 왼쪽에 조작 버튼을 띄우고, adb로도 조작할 수 있다.
 *
 * adb shell am broadcast -a com.runnect.debug.GPS_SIM -p com.runnect.runnect --es cmd <명령>
 *  - start / stop / rewind(출발점으로) / halt(제자리) / go / slow / normal / fast
 *  - offroute(--ei meters <m>, 기본 50m) / onroute
 *  - target(--ei sec <초/km>, 0이면 해제): 목표 페이스 지정
 *  - resume: 일시정지 해제(재개 버튼과 동일)
 *  - scenario(--es id <RunSimScenario.id>): 패널의 시나리오 버튼과 같은 상황을 만든다
 * 러닝 상태는 onServiceTick이 1초마다 logcat 태그 RunSimState로 남긴다.
 */
object RunDebugTools {
    const val ACTION = "com.runnect.debug.GPS_SIM"
    private const val DEFAULT_OFF_ROUTE_M = 50

    // 화면 회전 등으로 RunActivity가 재생성돼도 같은 러닝의 시뮬레이션을 이어가기 위해 보관한다.
    private var retained: GpsRouteSimulator? = null

    fun attach(activity: AppCompatActivity, route: List<LatLng>, controller: () -> RunController?) {
        val simulator = retained?.takeIf { it.route == route } ?: GpsRouteSimulator(activity, route).also {
            retained?.stop()
            retained = it
        }
        simulator.apply {
            onError = {
                Toast.makeText(
                    activity.applicationContext, // 보관된 시뮬레이터가 재생성 전 Activity를 붙잡지 않게
                    "모의 위치 권한 필요: adb shell appops set ${activity.packageName} android:mock_location allow",
                    Toast.LENGTH_LONG
                ).show()
                stop()
            }
        }
        // 버튼/adb 어느 쪽으로 바꿔도 패널 표시가 갱신되도록 상태 변경 시 증가시키는 값
        var version by mutableIntStateOf(0)
        var lastScenario by mutableStateOf<RunSimScenario?>(null)
        fun runScenario(scenario: RunSimScenario) {
            scenario.apply(simulator, controller(), activity.lifecycleScope)
            lastScenario = scenario
            Log.i("RunSimScenario", "apply ${scenario.id}")
            version++
        }
        fun runCommand(command: String, meters: Int = DEFAULT_OFF_ROUTE_M, paceSec: Int = 0) {
            when (command) {
                "rewind" -> simulator.rewind()
                "target" -> controller()?.setTargetPace(paceSec.takeIf { it > 0 }?.toDouble())
                "resume" -> controller()?.resume()
                "start" -> simulator.start()
                "stop" -> simulator.stop()
                "offroute" -> simulator.offRouteM = meters.toDouble()
                "onroute" -> simulator.offRouteM = 0.0
                "halt" -> simulator.isHalted = true
                "go" -> simulator.isHalted = false
                "slow" -> simulator.speed = GpsRouteSimulator.Speed.SLOW
                "normal" -> simulator.speed = GpsRouteSimulator.Speed.NORMAL
                "fast" -> simulator.speed = GpsRouteSimulator.Speed.FAST
            }
            version++
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val command = intent.getStringExtra("cmd") ?: return
                if (command == "scenario") {
                    RunSimScenario.of(intent.getStringExtra("id"))?.let(::runScenario)
                    return
                }
                runCommand(command, intent.getIntExtra("meters", DEFAULT_OFF_ROUTE_M), intent.getIntExtra("sec", 0))
            }
        }
        ContextCompat.registerReceiver(activity, receiver, IntentFilter(ACTION), ContextCompat.RECEIVER_EXPORTED)
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                activity.unregisterReceiver(receiver)
                if (activity.isFinishing) {
                    simulator.stop()
                    retained = null
                }
            }
        })

        val panel = ComposeView(activity).apply {
            setContent {
                version // 상태 변경 시 리컴포지션
                SimulatorPanel(simulator, lastScenario, ::runCommand, ::runScenario)
            }
        }
        activity.addContentView(
            panel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL
            )
        )
    }

    /**
     * 러닝 서비스가 1초마다 호출한다. 서비스에서 남기므로 화면이 꺼지거나 백그라운드여도 계속 기록된다.
     * scripts/gps-sim-scenarios.sh가 이 로그로 TC를 판정한다.
     */
    fun onServiceTick(state: RunTrackingState) {
        val alert = when (state.alert) {
            is RunAlert.OffRoute -> "OFF_ROUTE"
            is RunAlert.PaceDrop -> "PACE_DROP"
            null -> "NONE"
        }
        Log.i(
            "RunSimState",
            "distKm=${state.distanceKm} distM=${state.distanceM.toInt()} pace=${state.paceSecPerKm?.toInt()} alert=$alert " +
                "paused=${state.isPaused} sim=${retained?.isRunning == true}"
        )
    }

    @Composable
    private fun SimulatorPanel(
        simulator: GpsRouteSimulator,
        lastScenario: RunSimScenario?,
        onCommand: (String) -> Unit,
        onScenario: (RunSimScenario) -> Unit,
    ) {
        var isExpanded by rememberSaveable { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(if (isExpanded) 168.dp else 84.dp)
                .background(Color(0xE6171717), RoundedCornerShape(10.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            // 제목과 상태 줄 전체를 눌러 펼치기/접기 — 작은 글자만 누르게 하면 터치가 잘 안 먹는다.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                PanelText(
                    text = if (isExpanded) "GPS 시뮬 ▾ 접기" else "GPS 시뮬 ▸",
                    color = Color(0xFF9C9AFD),
                )
                PanelText(
                    text = buildString {
                        append(if (simulator.isRunning) "● " else "○ ")
                        append(simulator.speed.label)
                        if (simulator.offRouteM > 0) append(" · 이탈")
                        if (simulator.isHalted) append(" · 멈춤")
                    },
                    color = Color(0xFFC1C1C1),
                )
            }
            if (!isExpanded) return@Column

            PanelButton(if (simulator.isRunning) "■ 시뮬 정지" else "▶ 시뮬 시작", Color(0xFF444444)) {
                onCommand(if (simulator.isRunning) "stop" else "start")
            }
            RunSimScenario.entries.forEach { scenario ->
                PanelButton(
                    text = scenario.title,
                    color = if (scenario == lastScenario) Color(0xFF7E71FF) else Color(0xFF593EEC),
                ) { onScenario(scenario) }
            }
            lastScenario?.let {
                PanelText(text = "기대: ${it.expectation}", color = Color(0xFFFFC617))
            }
            PanelText(text = "같은 알림은 60초 동안 다시 안 뜸", color = Color(0xFF8B8B8B))
        }
    }

    @Composable
    private fun PanelButton(text: String, color: Color = Color(0xFF593EEC), onClick: () -> Unit) {
        PanelText(
            text = text,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(color, RoundedCornerShape(6.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
    }

    @Composable
    private fun PanelText(text: String, color: Color, modifier: Modifier = Modifier) {
        Text(text = text, color = color, fontSize = 11.sp, modifier = modifier)
    }
}
