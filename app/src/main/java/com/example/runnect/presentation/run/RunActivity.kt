package com.example.runnect.presentation.run

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.example.runnect.R
import com.example.runnect.binding.BindingActivity
import com.example.runnect.data.model.entity.LocationLatLngEntity
import com.example.runnect.databinding.ActivityRunBinding
import com.example.runnect.presentation.endrun.EndRunActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.custom_dialog_finish_run.view.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.timer
import kotlin.properties.Delegates

class RunActivity : BindingActivity<ActivityRunBinding>(R.layout.activity_run),
    OnMapReadyCallback {


    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var touchList = arrayListOf<LatLng>()
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표

    var secPublic by Delegates.notNull<Int>()
    var milliPublic by Delegates.notNull<Int>()

    //타이머
    var time = 0
    var timerTask: Timer? = null

    private fun startTimer() {
        timerTask = timer(period = 10) {
            time++

            val sec = time / 100
            val milli = time % 100

            secPublic = sec
            milliPublic = milli

            runOnUiThread{
                binding.tvTimeRecord.text = "${sec} : ${milli}"
            }

        }
    }

    private fun stopTimer(){
        timerTask?.cancel()
    }

    lateinit var startLatLngPublic: LocationLatLngEntity

    val viewModel: RunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        init()
        initView()
        startTimer()
        getCurrentLocation()
        seeRecord()


    }


    private fun initView() {

        //MapFragment 추가
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        mapFragment.getMapAsync(this) //지도 객체 얻어오기
        locationSource = FusedLocationSource(
            this,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this) //
//        initView() //지도 뷰 표시
    }
    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0


        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Follow //위치추적 모드 Follow

        //네이버 맵 sdk에 위치 정보 제공
        locationSource = FusedLocationSource(this@RunActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                isVisible = true //현재 위치 마커 가시성(default = false)
                position = LatLng(currentLocation.latitude, currentLocation.longitude)
            }
        }
        drawCourse()

        val uiSettings = naverMap.uiSettings
        uiSettings.isZoomControlEnabled = false

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
    //여기서 터치로 그려주는 게 아니라 그냥 받아온 걸로 세팅하게 만들어야 함
    private fun drawCourse() {
        touchList = intent.getSerializableExtra("touchList") as ArrayList<LatLng>
        startLatLngPublic = intent.getParcelableExtra("startLatLng")!!
        val totalDistance = intent.getSerializableExtra("totalDistance")
        viewModel.distanceSum.value = totalDistance as Double?

        Timber.tag(ContentValues.TAG).d("startLatLng : ${startLatLngPublic}")
        Timber.tag(ContentValues.TAG).d("touchList : ${touchList}")
        Timber.tag(ContentValues.TAG).d("totalDistance : ${totalDistance}")
        Timber.tag(ContentValues.TAG).d("viewModel : ${viewModel.distanceSum.value}")
        //수신 완료

        val path = PathOverlay()
        //startMarker-start
        val startMarker = Marker()
        val startLatLng =
            LatLng(startLatLngPublic.latitude.toDouble(), startLatLngPublic.longitude.toDouble())

        startMarker.position =
            LatLng(startLatLng.latitude, startLatLng.longitude) // 출발지점
        startMarker.anchor = PointF(0.5f, 0.7f)
        startMarker.icon = OverlayImage.fromResource(R.drawable.startmarker)
        startMarker.map = naverMap

        cameraUpdate(
            LatLng(startLatLng.latitude, startLatLng.longitude)
        )

        val coords = mutableListOf(
            LatLng(startLatLng.latitude, startLatLng.longitude)
        )
        //startMarker-end


        //lineMarker-start
        val marker = Marker()


        touchList.forEach { touch ->
            marker.position = LatLng(touch.latitude, touch.longitude)
            marker.anchor = PointF(0.5f, 0.5f)
            marker.icon = OverlayImage.fromResource(R.drawable.marker_line)
            marker.map = naverMap

            // 경로선 list인 coords에 터치로 받아온 좌표값을 추가
            coords.add(LatLng(touch.latitude, touch.longitude))

            // 경로선 그리기

            path.coords = coords

            Timber.tag(ContentValues.TAG).d("pat.coords : ${path.coords}")

            // 경로선 색상
            path.color = Color.parseColor("#593EEC")
            // 경로선 테두리 색상
            path.outlineColor = Color.parseColor("#593EEC")
            path.map = naverMap
        }

        //lineMarker-end


    }

    private fun seeRecord() {
        binding.btnRunFinish.setOnClickListener {
            stopTimer()
            bottomSheet()

        }
    }

    @SuppressLint("MissingInflatedId")
    fun bottomSheet(){
        // bottomSheetDialog 객체 생성
        val bottomSheetDialog = BottomSheetDialog(
            this@RunActivity, R.style.BottomSheetDialogTheme
        )
        // layout_bottom_sheet를 뷰 객체로 생성
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.custom_dialog_finish_run,
            findViewById(R.id.bottomSheet) as LinearLayout?
        )
        // bottomSheetDialog의 dismiss 버튼 선택시 dialog disappear
        bottomSheetView.findViewById<View>(R.id.btn_see_record).setOnClickListener {
            val intent = Intent(this@RunActivity, EndRunActivity::class.java).apply {
//                putExtra("distanceSum", viewModel.distanceSum.value)
//                putExtra("timerSec",secPublic)
//                putExtra("timerMilli",milliPublic)
            }
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
        // bottomSheetDialog 뷰 생성
        bottomSheetDialog.setContentView(bottomSheetView)
        // bottomSheetDialog 호출
        bottomSheetDialog.show()
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val SEARCH_RESULT_BEFORE_RUN = "SearchResultBeforeRun"

    }
}
