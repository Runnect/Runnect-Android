package com.runnect.runnect.presentation.run

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.util.ForegroundServiceUtil
import com.runnect.runnect.util.IntentUtil
import com.runnect.runnect.util.extension.getCompatibleParcelableExtra
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

/**
 * 러닝 한 번의 진행을 책임지는 포그라운드 서비스. 화면이 꺼지거나 앱이 백그라운드로 가도 위치를 계속 받아
 * 시간/거리/페이스/자동 일시정지/코스 이탈·페이스 저하 알림(진동 포함)을 처리한다. RunActivity는 바인딩해서 state를 그리기만 한다.
 * RunActivity가 재생성(configuration change 등)돼도 서비스는 그대로이므로 기록이 이어진다.
 */
class TimerService : Service(), RunController {

    private val binder = LocalBinder()
    private val tracker = RunTracker()
    val state: StateFlow<RunTrackingState> get() = tracker.state

    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val fusedClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private lateinit var haptics: RunAlertHaptics

    private var isStarted = false
    private var isUiVisible = true
    private var runActivityExtras: Bundle? = null
    private var player: MediaPlayer? = null
    private lateinit var notificationBuilder: NotificationCompat.Builder

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private val tick = object : Runnable {
        override fun run() {
            tracker.onSecondElapsed(System.currentTimeMillis())
            updateOngoingNotification()
            RunDebugTools.onServiceTick(tracker.state.value)
            mainHandler.postDelayed(this, TICK_MILLIS)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach {
                tracker.onLocationUpdated(LatLng(it.latitude, it.longitude), System.currentTimeMillis())
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // RunActivity가 재생성되면 startService가 다시 불린다 — 이미 달리는 중이면 아무것도 새로 시작하지 않는다.
        if (isStarted) return START_NOT_STICKY
        isStarted = true
        runActivityExtras = intent?.extras

        intent?.getCompatibleParcelableExtra<CourseData>(RunActivity.EXTRA_COUNTDOWN_TO_RUN)?.let {
            tracker.setRoute(listOf(it.startLatLng) + it.touchList)
        }
        tracker.setTargetPace(
            intent?.getDoubleExtra(RunActivity.EXTRA_TARGET_PACE_SEC_PER_KM, -1.0)?.takeIf { it > 0.0 }
        )
        haptics = RunAlertHaptics(this, initialAlert = null)

        notifyStartRun()
        initNotification()
        startLocationUpdates()
        observeAlerts()
        mainHandler.postDelayed(tick, TICK_MILLIS)
        // 프로세스가 종료되면 러닝 상태도 함께 사라지므로, 서비스만 0초부터 다시 살아나지 않게 한다.
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission") // 러닝 시작 전 위치 권한을 받은 상태에서만 이 서비스가 시작된다
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL_MILLIS).build()
        runCatching { fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper()) }
    }

    /** 알림이 새로 뜨면 진동하고, 화면을 보고 있지 않으면 상단(헤드업) 알림으로도 띄운다. */
    private fun observeAlerts() {
        scope.launch {
            tracker.state.distinctUntilChangedBy { it.alert }.collect { state ->
                haptics.onAlertChanged(state.alert)
                RunAlertNotifier.update(this@TimerService, state.alert, isUiVisible, contentIntent())
            }
        }
    }

    /** 러닝 화면이 보이는지 여부. 보일 때는 화면 배너로 충분하므로 상단 알림을 띄우지 않는다. */
    fun setUiVisible(visible: Boolean) {
        isUiVisible = visible
        if (visible) RunAlertNotifier.cancel(this)
        else RunAlertNotifier.update(this, tracker.state.value.alert, isUiVisible = false, contentIntent())
    }

    override fun pause() {
        tracker.pause()
        updateOngoingNotification()
    }

    override fun resume() {
        tracker.resume(System.currentTimeMillis())
        updateOngoingNotification()
    }

    override fun setTargetPace(paceSecPerKm: Double?) = tracker.setTargetPace(paceSecPerKm)

    fun stopRun() {
        mainHandler.removeCallbacks(tick)
        fusedClient.removeLocationUpdates(locationCallback)
        RunAlertNotifier.cancel(this)
    }

    override fun onDestroy() {
        stopRun()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun notifyStartRun() {
        if (player == null) {
            player = MediaPlayer.create(this@TimerService, R.raw.start_run)
            player?.setOnCompletionListener { mediaPlayer ->
                mediaPlayer.release()
                player = null
            }
        }
        player?.start()
    }

    private fun updateOngoingNotification() {
        if (!::notificationBuilder.isInitialized) return
        val state = tracker.state.value
        notificationBuilder.setContentText(if (state.isPaused) "일시정지 중" else formatElapsed(state.elapsedSec))
        getSystemService(NotificationManager::class.java).notify(NOTI_ID, notificationBuilder.build())
    }

    private fun contentIntent(): PendingIntent {
        // 알림으로 RunActivity가 새로 만들어져도 코스 정보를 받을 수 있게 시작할 때 받은 extras를 그대로 넘긴다.
        val notificationIntent = Intent(this@TimerService, RunActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .apply { runActivityExtras?.let(::putExtras) }
        return IntentUtil.createSafePendingIntent(
            this@TimerService,
            0,
            notificationIntent,
            FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT, // 이전 러닝의 코스 extras가 남지 않게 갱신
            false
        )
    }

    private fun initNotification() {
        notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.ic_notification_run)
            .setColor(ContextCompat.getColor(this@TimerService, R.color.M1))
            .setContentTitle("러닝")
            .setContentText("00:00:00")
            .setOnlyAlertOnce(true)
            .setOngoing(true) // true 일 경우 알림 리스트에서 클릭하거나 좌우로 드래그해도 사라지지 않음
            .setContentIntent(contentIntent()) // 알림 클릭 시 이동

        val notificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val notification = notificationBuilder.build()
        notificationManager.notify(NOTI_ID, notification)
        ForegroundServiceUtil.startForegroundSafely(
            this,
            NOTI_ID,
            notification,
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
    }

    companion object {
        private const val NOTI_ID = 1
        private const val TICK_MILLIS = 1_000L
        private const val LOCATION_INTERVAL_MILLIS = 1_000L

        fun formatElapsed(totalSec: Int): String =
            String.format("%02d:%02d:%02d", totalSec / 3600, (totalSec % 3600) / 60, totalSec % 60)
    }
}
