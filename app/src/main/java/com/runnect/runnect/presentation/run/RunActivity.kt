package com.runnect.runnect.presentation.run

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
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
import com.runnect.runnect.databinding.ActivityRunBinding
import com.runnect.runnect.presentation.endrun.EndRunActivity
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Timer
import kotlin.concurrent.timer

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

    //타이머
    private var time = 0
    private var timerTask: Timer? = null
    private var timerSecond: Int = 0
    private var timerMinute: Int = 0
    private var timerHour: Int = 0

    var courseId: Int? = null
    var publicCourseId: Int? = null
    lateinit var departure: String
    lateinit var startLatLng: LatLng
    lateinit var touchList: ArrayList<LatLng>
    lateinit var captureUri: String
    lateinit var dataFrom: String
    var distanceSum: Double = 0.0


    private val viewModel: RunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        initView()
        startTimer()
        getCurrentLocation()
        showRecord()
        backButton()
    }

    override fun onBackPressed() {
        stopTimer()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun backButton() {
        binding.imgBtnBack.setOnClickListener {
            stopTimer()
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
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

        val distanceCut =
            BigDecimal(courseData.distance.toDouble()).setScale(1, RoundingMode.FLOOR)
                .toDouble()
        distanceSum = distanceCut
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
                        timerHour = timerHour,
                        timerMinute = timerMinute,
                        timerSecond = timerSecond,
                        dataFrom = dataFrom
                    )
                )
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun startTimer() {
        timerTask = timer(period = 1000) {

            time++ //1초에 한 번씩 timer 값이 1씩 증가, 초기값은 0

            val hour = time / 3600
            val minute = time / 60 //60이 되는 순간 몫이 1이 돼서 1로 표기
            val second = time % 60 //60이 되는순간 나머지가 0이라 0으로 표기

            runOnUiThread {
                if (hour < 10) {
                    binding.tvTimeHour.text = "0$hour"
                } else {
                    binding.tvTimeHour.text = "$hour"
                }

                if (minute < 10) {
                    binding.tvTimeMinute.text = "0$minute"
                } else {
                    binding.tvTimeMinute.text = "$minute"
                }

                if (second < 10) {
                    binding.tvTimeSecond.text = "0$second"
                } else {
                    binding.tvTimeSecond.text = "$second"
                }
            }

            timerHour = hour
            timerMinute = minute
            timerSecond = second

            Timber.tag(ContentValues.TAG).d("timerHour 값 : $timerHour")
            Timber.tag(ContentValues.TAG).d("timerMinute 값 : $timerMinute")
            Timber.tag(ContentValues.TAG).d("timerSecond 값 : $timerSecond")

            Timber.tag(ContentValues.TAG)
                .d("binding.tvTimeHour.text 값 : ${binding.tvTimeHour.text}")
            Timber.tag(ContentValues.TAG)
                .d("binding.tvTimeMinute.text 값 : ${binding.tvTimeMinute.text}")
            Timber.tag(ContentValues.TAG)
                .d("binding.tvTimeSecond.text 값 : ${binding.tvTimeSecond.text}")


        }
    }

    private fun stopTimer() {
        timerTask?.cancel()
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
