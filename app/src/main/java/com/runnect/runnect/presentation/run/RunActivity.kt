package com.runnect.runnect.presentation.run

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.runnect.runnect.R
import com.runnect.runnect.data.dto.CourseData
import com.runnect.runnect.data.dto.RunToEndRunData
import com.runnect.runnect.data.dto.TimerData
import com.runnect.runnect.databinding.ActivityRunBinding
import com.runnect.runnect.presentation.endrun.EndRunActivity
import com.runnect.runnect.presentation.run.TimerService.Companion.EXTRA_TIMER_VALUE
import com.runnect.runnect.util.extension.round
import java.math.BigDecimal
import java.math.RoundingMode

class RunActivity :
    com.runnect.runnect.binding.BindingActivity<ActivityRunBinding>(R.layout.activity_run),
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

    lateinit var timerData: TimerData
    private var timerService: TimerService? = null
    private var isServiceBound = false
    lateinit var serviceIntent: Intent

    private val connection = object : ServiceConnection {
        //서비스가 연결되었을 때 호출
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            isServiceBound = true
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
        initTimerService()
        getCurrentLocation()
        showRecord()
        backButton()
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

    private fun initTimerService() {
        serviceIntent = Intent(this, TimerService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun stopTimer() {
        timerService?.stopTimer()
        stopService(serviceIntent) //서비스 객체 제거
    }

    override fun onStart() {
        super.onStart()
        // Timer 결과값을 받기 위해 브로드캐스트 리시버 등록
        registerReceiver(timerReceiver, IntentFilter(TimerService.TIMER_UPDATE_ACTION))
    }

    override fun onStop() {
        super.onStop()
        // 브로드캐스트 리시버 등록 해제
        unregisterReceiver(timerReceiver)
    }

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            timerData = intent.getParcelableExtra(EXTRA_TIMER_VALUE)!!
            val timerUI = String.format(
                "%02d:%02d:%02d",
                timerData.hour,
                timerData.minute,
                timerData.second
            )
            updateTimerUI(timerUI)
        }
    }

    private fun updateTimerUI(timerValue: String) {
        binding.tvTimer.text = timerValue
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
        }
        stopTimer()
        finish()
    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            stopTimer()
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
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
        courseData = intent.getParcelableExtra("CountToRunData")!!

        courseId = courseData.courseId
        publicCourseId = courseData.publicCourseId
        departure = courseData.departure
        startLatLng = courseData.startLatLng
        touchList = courseData.touchList
        captureUri = courseData.image
        dataFrom = courseData.dataFrom

        viewModel.distanceSum.value = courseData.distance.toDouble().round(1)
    }

    private fun createDepartureMarker() {
        val departureMarker = Marker()
        departureLatLng = courseData.startLatLng

        departureMarker.position =
            LatLng(departureLatLng.latitude, departureLatLng.longitude)
        departureMarker.anchor = PointF(0.5f, 0.7f)
        departureMarker.icon = OverlayImage.fromResource(R.drawable.marker_departure)
        departureMarker.map = naverMap
        cameraUpdate(
            LatLng(departureLatLng.latitude, departureLatLng.longitude)
        )
        coords.add(LatLng(departureLatLng.latitude, departureLatLng.longitude))
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
        binding.btnRunFinish.setOnClickListener {
            stopTimer()
            val intent = Intent(this@RunActivity, EndRunActivity::class.java).apply {
                putExtra(
                    "RunToEndRunData",
                    RunToEndRunData(
                        courseId = courseId!!,
                        publicCourseId = publicCourseId,
                        totalDistance = distanceSum,
                        captureUri = captureUri,
                        departure = departure,
                        timerHour = timerData.hour,
                        timerMinute = timerData.minute,
                        timerSecond = timerData.second,
                        dataFrom = dataFrom
                    )
                )
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
