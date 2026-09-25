package com.runnect.runnect.presentation.run

import kotlin.math.roundToInt
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.R
import com.runnect.runnect.binding.BindingActivity
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.RunToEndRunData
import com.runnect.runnect.databinding.ActivityRunBinding
import com.runnect.runnect.presentation.endrun.EndRunActivity
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.util.analytics.Analytics
import com.runnect.runnect.util.analytics.EventName
import com.runnect.runnect.util.analytics.EventName.Param
import com.runnect.runnect.util.extension.round

class RunActivity : BindingActivity<ActivityRunBinding>(R.layout.activity_run),
    OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수
    private lateinit var departureLatLng: LatLng
    private lateinit var courseData: CourseData

    private val path = PathOverlay()

    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표
    private var coords = mutableListOf<LatLng>()

    var courseId: Int? = null
    var publicCourseId: Int? = null
    lateinit var departure: String
    lateinit var startLatLng: LatLng
    lateinit var touchList: ArrayList<LatLng>
    lateinit var captureUri: String
    lateinit var dataFrom: String
    var distanceSum: Double = 0.0

    private val viewModel: RunViewModel by viewModels()

    private var timerService: TimerService? = null
    private var isServiceBound = false
    lateinit var serviceIntent: Intent

    private val connection = object : ServiceConnection {
        //서비스가 연결되었을 때 호출
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService().also { it.setUiVisible(isUiStarted) }
            isServiceBound = true
            observeRunState()
        }

        //서비스 연결이 끊어졌을 때 호출
        override fun onServiceDisconnected(arg0: ComponentName) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        initView()
        initStatsComposeView()
        initAlertComposeView()
        initTimerService()
        getCurrentLocation()
        showRecord()
        backButton()
        setUpPauseResume()

        // 러닝 시작 이벤트는 처음 한 번만 — 화면 회전 등으로 재생성될 때는 다시 기록하지 않는다.
        if (savedInstanceState != null) return
        val runCourseData: CourseData? = intent.getParcelableExtra(EXTRA_COUNTDOWN_TO_RUN)
        val targetDistanceM = runCourseData?.distance?.let { (it * 1000f).roundToInt() }
        Analytics.logEvent(
            EventName.ACTION_RUN_START,
            Param.COURSE_ID to runCourseData?.courseId,
            Param.TARGET_DISTANCE_M to targetDistanceM
        )
    }

    private fun initView() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this) // 분리 필요

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(
            this,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun initStatsComposeView() {
        binding.composeRunStats.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RunnectTheme {
                    val state by viewModel.trackingState.collectAsStateWithLifecycle()
                    RunStatsPanel(state = state)
                }
            }
        }
    }

    private fun initAlertComposeView() {
        // 진동은 러닝 서비스가 울린다(백그라운드에서도 울려야 하므로). 화면은 배너만 그린다.
        binding.composeRunAlert.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RunnectTheme {
                    val state by viewModel.trackingState.collectAsStateWithLifecycle()
                    RunAlertBanner(alert = state.alert)
                }
            }
        }
    }

    private fun initTimerService() {
        // 서비스는 재생성 시 다시 start돼도 기존 타이머를 유지한다(TimerService.onStartCommand). 알림에서 러닝 화면을
        // 다시 열 때 필요한 코스 정보를 넘기기 위해 이 화면의 extras를 함께 전달한다.
        serviceIntent = Intent(this, TimerService::class.java).putExtras(intent)
        startService(serviceIntent)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    /** 러닝 서비스의 상태를 화면에 반영한다. 화면이 재생성돼도 다시 바인딩하면 같은 상태를 이어받는다. */
    private fun observeRunState() {
        val service = timerService ?: return
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                service.state.collect { state ->
                    viewModel.render(state)
                    updatePauseResumeUI(state.isPaused)
                }
            }
        }
    }

    private fun stopTimer() {
        timerService?.stopRun()
        stopService(serviceIntent) //서비스 객체 제거
    }

    private fun setUpPauseResume() {
        binding.btnRunPauseResume.setOnClickListener { togglePauseResume() }
    }

    private fun togglePauseResume() {
        val service = timerService ?: return
        if (service.state.value.isPaused) service.resume() else service.pause()
    }

    private fun updatePauseResumeUI(isPaused: Boolean) {
        binding.btnRunPauseResume.apply {
            setImageResource(if (isPaused) R.drawable.ic_run_resume else R.drawable.ic_run_pause)
            contentDescription = getString(
                if (isPaused) R.string.run_description_resume else R.string.run_description_pause
            )
        }
        binding.btnRunStop.visibility = if (isPaused) View.VISIBLE else View.GONE
        binding.tvPausedLabel.visibility = if (isPaused) View.VISIBLE else View.GONE
    }

    // 러닝 화면이 보이는 동안엔 알림을 화면 배너로, 안 보이면(백그라운드/화면 꺼짐) 상단 알림으로 띄우도록 서비스에 알린다.
    private var isUiStarted = false

    override fun onStart() {
        super.onStart()
        isUiStarted = true
        timerService?.setUiVisible(true)
    }

    override fun onStop() {
        super.onStop()
        isUiStarted = false
        timerService?.setUiVisible(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
        }
        // 화면 회전 등으로 재생성되는 경우엔 타이머 서비스를 살려둬야 기록이 이어진다. 러닝을 끝내고 나갈 때만 종료한다.
        if (isFinishing) stopTimer()
    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            Analytics.logEvent(
                EventName.ACTION_RUN_ABANDON,
                Param.COURSE_ID to courseId,
                Param.DISTANCE_M to (distanceSum * 1000.0).roundToInt()
            )
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        Analytics.logEvent(
            EventName.ACTION_RUN_ABANDON,
            Param.COURSE_ID to courseId,
            Param.DISTANCE_M to (distanceSum * 1000.0).roundToInt()
        )
        stopTimer()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        setZoomMinMax()
        setLocationTrackingMode()
        addCurrentLocationChangeListener(naverMap)
        hideZoomControl()
        setCourse()
        setCurrentLocationImage()
    }

    private fun setLocationTrackingMode() {
        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(this@RunActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow //위치추적 모드 Follow
    }

    private fun addCurrentLocationChangeListener(map: NaverMap) {
        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                isVisible = true //현재 위치 마커 가시성(default = false)
                position = LatLng(currentLocation.latitude, currentLocation.longitude)
            }
        }
    }

    private fun setZoomMinMax() {
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0
    }

    private fun hideZoomControl() {
        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
    }

    private fun setCurrentLocationImage() {
        //현위치 커스텀 이미지
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.current_location)
    }

    //카메라 위치 변경 함수
    private fun cameraUpdate(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            .animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)

    }

    private fun getCurrentLocation() {
        binding.btnCurrentLocation.setOnClickListener {
            cameraUpdate(currentLocation)
        }
    }


    private fun setCourse() {
        getIntentValue()
        createDepartureMarker()
        createRouteMarker()
    }

    private fun getIntentValue() {
        courseData = intent.getParcelableExtra(EXTRA_COUNTDOWN_TO_RUN)!!

        courseId = courseData.courseId
        publicCourseId = courseData.publicCourseId
        departure = courseData.departure ?: ""
        startLatLng = courseData.startLatLng
        touchList = courseData.touchList
        captureUri = courseData.image
        dataFrom = courseData.dataFrom
        distanceSum = courseData.distance?.toDouble()?.round(1) ?: 0.0

        viewModel.distanceSum.value = distanceSum
        // 이탈 판정 경로와 목표 페이스는 러닝 서비스가 같은 extras에서 읽는다(TimerService.onStartCommand).
        // debug 빌드에서만 GPS 시뮬레이터 패널을 띄운다.
        RunDebugTools.attach(this, listOf(startLatLng) + touchList) { timerService }
    }

    private fun createDepartureMarker() {
        val departureMarker = Marker()
        departureLatLng = courseData.startLatLng

        departureMarker.position =
            LatLng(departureLatLng.latitude, departureLatLng.longitude)
        departureMarker.anchor = PointF(0.5f, 0.5f)
        departureMarker.icon = OverlayImage.fromResource(R.drawable.runnect_marker)
        departureMarker.map = naverMap
        cameraUpdate(
            LatLng(departureLatLng.latitude, departureLatLng.longitude)
        )
        coords.add(LatLng(departureLatLng.latitude, departureLatLng.longitude))

        setCustomInfoWindow(marker = departureMarker)
    }

    private fun setCustomInfoWindow(marker: Marker) {
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.ViewAdapter() {
            override fun getView(p0: InfoWindow): View {
                return LayoutInflater.from(this@RunActivity)
                    .inflate(R.layout.custom_info_window, binding.root as ViewGroup, false)
            }
        }
        infoWindow.open(marker)
    }

    private fun createRouteMarker() {
        for (i in 1..courseData.touchList.size) {
            setRouteMarker(courseData, i)
            generateRouteLine(courseData, i)
        }
    }

    private fun setRouteMarker(
        courseData: CourseData,
        i: Int
    ) { //여기도 create랑 set이랑 역할 표현이 모호함
        val routeMarker = Marker()
        routeMarker.position = LatLng(
            courseData.touchList[i - 1].latitude,
            courseData.touchList[i - 1].longitude
        )
        routeMarker.anchor = PointF(0.5f, 0.5f)
        routeMarker.icon = OverlayImage.fromResource(R.drawable.marker_route)
        routeMarker.map = naverMap
    }

    private fun generateRouteLine(courseData: CourseData, i: Int) {
        coords.add(
            LatLng(
                courseData.touchList[i - 1].latitude, // coords에 터치로 받아온 좌표값을 추가
                courseData.touchList[i - 1].longitude
            )
        ) // coords에 터치로 받아온 좌표값 추가
        path.coords = coords // 경로선 그리기
        path.color = Color.parseColor("#593EEC") // 경로선 색상
        path.outlineColor = Color.parseColor("#593EEC") // 경로선 테두리 색상
        path.map = naverMap
    }

    private fun showRecord() {
        binding.btnRunStop.setOnClickListener {
            val elapsedSec = timerService?.state?.value?.elapsedSec ?: 0
            stopTimer()
            val totalTimeSec = elapsedSec
            Analytics.logEvent(
                EventName.ACTION_RUN_COMPLETE,
                Param.COURSE_ID to courseId,
                Param.TOTAL_TIME_SEC to totalTimeSec,
                Param.TOTAL_DISTANCE_M to (distanceSum * 1000.0).roundToInt()
            )
            val intent = Intent(this@RunActivity, EndRunActivity::class.java).apply {
                putExtra(
                    EXTRA_RUN_TO_ENDRUN,
                    RunToEndRunData(
                        courseId = courseId!!,
                        publicCourseId = publicCourseId,
                        totalDistance = distanceSum,
                        captureUri = captureUri,
                        departure = departure,
                        timerHour = elapsedSec / 3600,
                        timerMinute = (elapsedSec % 3600) / 60,
                        timerSecond = elapsedSec % 60,
                        dataFrom = dataFrom
                    )
                )
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val EXTRA_RUN_TO_ENDRUN = "RunToEndRunData"
        const val EXTRA_COUNTDOWN_TO_RUN = "CountToRunData"
        const val EXTRA_TARGET_PACE_SEC_PER_KM = "TargetPaceSecPerKm"
    }
}
