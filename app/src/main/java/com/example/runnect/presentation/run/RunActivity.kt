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
import com.example.runnect.databinding.ActivityRunBinding
import com.example.runnect.presentation.endrun.EndRunActivity
import com.example.runnect.presentation.search.entity.LocationLatLngEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.custom_dialog.view.*
import timber.log.Timber

class RunActivity : BindingActivity<ActivityRunBinding>(R.layout.activity_run),
    OnMapReadyCallback {


    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var touchList = arrayListOf<LatLng>()
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표

//    private lateinit var searchResult: SearchResultEntity
    //뭘 넘겨받아야 하냐면, 출발 지점 좌표, 그리고 이 사람이 찍어놓은 좌표들을 모아놓은 TouchList
    //마커리스트는 내 기억으로 마커를 지우기 위해서 따로 만들어준 리스트인데 여기선 지울 필요가 없으니까 안 받아도 됨
    //받아야와야 될 것 3개 : 출발 지점, touchList, 총거리합


    //지금 어디 코드 시점부터 넘어온 data를 받는 건지 정확한 파악이 안 됨. 그래서 받아온 data가 필요한 코드들을
    //draw()에다 싹 넣어줬는데 너무 길어짐. 그래서 일단 정리는 나중에 하기로 하고
    //별도의 전역 변수를 만들어서 쓰기로 함. 거리계산

    lateinit var startLatLngPublic: LocationLatLngEntity

    val viewModel: RunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel
        binding.lifecycleOwner = this

        init()
        initView()
        getCurrentLocation()
        seeRecord()


//        Timber.tag(ContentValues.TAG).d("searchResult@ : ${searchResult}")

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

//        // 시작 지점 설정
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885, 127.027512))
//        naverMap.moveCamera(cameraUpdate)

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
            bottomSheet()
//            cuDialog(binding.root)
//            startActivity(Intent(this, CountDownActivity::class.java))
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
            val intent = Intent(this@RunActivity, EndRunActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
        // bottomSheetDialog 뷰 생성
        bottomSheetDialog.setContentView(bottomSheetView)
        // bottomSheetDialog 호출
        bottomSheetDialog.show()
    }

//
//    }
//
//    private fun addListeners() {
//        binding.btnDraw.setOnClickListener {
//            createMbr()
//        }
//    }
//
//    //모든 마커를 포함할 수 있도록 하는 꼭지점 좌표 두개를 만들고
//    //중간지점의 좌표값을 구해서 카메라 위치를 이동할 수 있게 함.
//    private fun createMbr() {
//        val startLatLng = searchResult.locationLatLng
//        val bounds = LatLngBounds.Builder()
//            .include(LatLng(startLatLng.latitude.toDouble(), startLatLng.longitude.toDouble()))
//            .include(touchList)
//            .build()
//        naverMap.setContentPadding(100, 100, 100, 100)
//        cameraUpdate(bounds)
//
//        naverMap.addOnCameraIdleListener {
//            Toast.makeText(context, "카메라 움직임 종료", Toast.LENGTH_SHORT).show()
//            captureMap()
//        }
//
//    }
//
//    private fun captureMap() {
//        //캡쳐해서 이미지 뷰에 set하기~
//        naverMap.takeSnapshot {
//            binding.ivDrawingCaptured.setImageBitmap(it)
//        }
//    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        const val SEARCH_RESULT_BEFORE_RUN = "SearchResultBeforeRun"

    }
}
